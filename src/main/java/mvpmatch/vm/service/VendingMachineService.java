package mvpmatch.vm.service;

import mvpmatch.vm.domain.Product;
import mvpmatch.vm.domain.User;
import mvpmatch.vm.service.dto.PurchaseStatus;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static mvpmatch.vm.domain.User.emptyDeposit;

@ApplicationScoped
public class VendingMachineService {

    public static List<Integer> denominationsOrderedDesc = emptyDeposit().keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());

    @Transactional
    public Map<Integer, Integer> deposit(String buyerUserName, Integer coin, Integer number) {
        User buyer = User.findOneByUsername(buyerUserName).get();
        Map<Integer, Integer> buyerDeposit = buyer.getDeposit();
        buyerDeposit.put(coin, buyerDeposit.get(coin) + number);
        return buyerDeposit;
    }

    @Transactional
    public PurchaseStatus buy(String userName, Long productId, Integer quantity) {
        User loggedBuyer = User.findOneByUsername(userName).get();
        Product product = Product.<Product>findByIdOptional(productId).orElseThrow(() -> new BadRequestException(Response.status(BAD_REQUEST).entity("Product not available.").build()));

        if (product.getAmountAvailable() < quantity) {
            new BadRequestException(Response.status(BAD_REQUEST).entity("Quantity not available").build());
        }

        Integer totalCost = product.getCost() * quantity;

        buy(loggedBuyer.getDeposit(), totalCost);

        PurchaseStatus purchaseStatus = new PurchaseStatus();
        purchaseStatus.setTotalSpent(totalCost);

        product.setAmountAvailable(product.getAmountAvailable() - quantity);
        purchaseStatus.setProduct(product);

        Map<Integer, Integer> change = loggedBuyer.getDeposit();
        loggedBuyer.setDeposit(emptyDeposit());
        purchaseStatus.setChange(change);

        return purchaseStatus;
    }

    private static Integer availableFunds(Map<Integer, Integer> deposit) {
        return deposit.entrySet().stream()
                .mapToInt(denomination -> denomination.getKey() * denomination.getValue())
                .sum();
    }

    @Transactional
    public Map<Integer, Integer> reset(String userName) {
        User user = User.findOneByUsername(userName).get();
        Map<Integer, Integer> accountBalance = user.getDeposit();
        accountBalance.forEach((denomination, coinNr) -> {
            accountBalance.put(denomination, 0);
        });
        return accountBalance;
    }

    public static void buy(Map<Integer, Integer> deposit, Integer value) {
        if (availableFunds(deposit) < value) {
            throw new BadRequestException(Response.status(BAD_REQUEST).entity("Not enough money.").build());
        }

        ListIterator<Integer> denominations = denominationsOrderedDesc.listIterator();
        while (value > 0 && denominations.hasNext()) {
            Integer coin = denominations.next();
            Integer needed = value/coin;
            if (needed < deposit.get(coin)) {
                deposit.put(coin, deposit.get(coin) - needed);
                value -= coin * needed;
                int minorDenominationsTotal = availableFundsInMinorDenominations(deposit, coin);
                if (minorDenominationsTotal < value) {
                    disposeAvailableMinorCoins(deposit, coin);
                    deposit.put(coin, deposit.get(coin) - 1); // change 1 coin
                    returnChange(deposit, coin,  (coin + minorDenominationsTotal) - value);
                    return;
                }
            } else {
                value -= coin * deposit.get(coin);
                deposit.put(coin, 0);
            }
        }
    }

    private static void disposeAvailableMinorCoins(Map<Integer, Integer> deposit, int coin) {
        deposit.entrySet().stream()
                .filter(denomination -> denomination.getKey() < coin)
                .forEach(denomination -> denomination.setValue(0));
    }

    private static void returnChange(Map<Integer, Integer> deposit, int coin, int value) {
        for (int denomination : denominationsOrderedDesc) {
            if ((denomination < coin) && (value > 0)) {
                int needed = value / denomination;
                deposit.put(denomination, deposit.get(denomination) + needed);
                value -= denomination * needed;
            }
        }
    }

    private static Integer availableFundsInMinorDenominations(Map<Integer, Integer> deposit, Integer coin) {
        return deposit.entrySet().stream()
                .filter(denomination -> denomination.getKey() < coin)
                .mapToInt(denomination -> denomination.getKey() * denomination.getValue())
                .sum();
    }

}
