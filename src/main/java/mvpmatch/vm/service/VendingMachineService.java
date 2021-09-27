package mvpmatch.vm.service;

import mvpmatch.vm.domain.Product;
import mvpmatch.vm.domain.User;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@ApplicationScoped
public class VendingMachineService {


    @Transactional
    public Long deposit(String userName, Integer coin, Integer number) {
        User user = User.findByUsername(userName);
        if (user == null) {
            throw new WebApplicationException("Invalid user", BAD_REQUEST);
        }

        user.setDeposit(user.getDeposit() + (number * coin));

        return user.getDeposit();
    }

    @Transactional
    public Long buy(String userName, Long productId, Long quantity) {
        User user = User.findByUsername(userName);
        if (user == null) {
            throw new WebApplicationException("Invalid user", BAD_REQUEST);
        }

        Product product = Product.findById(productId);
        if (user == null) {
            throw new WebApplicationException("Invalid product", BAD_REQUEST);
        }

        if (product.getAmountAvailable() < quantity) {
            throw new WebApplicationException("Quantity not available", BAD_REQUEST);
        }

        Long totalCost = product.getCost() * quantity;
        if (user.getDeposit() < totalCost) {
            throw new WebApplicationException("Not enough funds", BAD_REQUEST);
        }

        product.setAmountAvailable(product.getAmountAvailable() - quantity);
        user.setDeposit(user.getDeposit() - totalCost);

        User seller = User.findById(product.getSellerId());
        seller.setDeposit(seller.getDeposit() + totalCost);

        return quantity;
    }

    @Transactional
    public Long reset(String userName) {
        User user = User.findByUsername(userName);
        if (user == null) {
            throw new WebApplicationException("Invalid user", BAD_REQUEST);
        }

        Long accountBalance = user.getDeposit();
        user.setDeposit(0L);
        return accountBalance;
    }
}
