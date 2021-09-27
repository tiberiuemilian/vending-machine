package mvpmatch.vm.web.rest;

import lombok.extern.slf4j.Slf4j;
import mvpmatch.vm.service.VendingMachineService;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.stream.IntStream;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static mvpmatch.vm.domain.Role.BUYER;

@Slf4j
@Path("/api")
//@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class VendingMachineController {

    private static final int[] COIN_TYPES = {5, 10, 20, 50, 100};

    final VendingMachineService vendingMachineService;

    @Inject
    public VendingMachineController(VendingMachineService vendingMachineService) {
        this.vendingMachineService = vendingMachineService;
    }

    @POST
    @Path("/deposit")
    @RolesAllowed({BUYER})
    public Response deposit(
            @QueryParam("coin") Integer coin,
            @Valid @Min(value = 0, message = "The value must be positive") @QueryParam("number") int number,
            @Context SecurityContext sec) {

        if (IntStream.of(COIN_TYPES).noneMatch(coin::equals)) {
            throw new WebApplicationException("Wrong coin value.", BAD_REQUEST);
        }

        Principal user = sec.getUserPrincipal();
        if (user == null) {
            throw new WebApplicationException("Invalid user", BAD_REQUEST);
        }
        String userName = user.getName();

        return Response.ok().entity(vendingMachineService.deposit(userName, coin, number)).build();
    }

    @POST
    @Path("/reset")
    @RolesAllowed({BUYER})
    public Response reset(@Context SecurityContext sec) {
        Principal user = sec.getUserPrincipal();
        if (user == null) {
            throw new WebApplicationException("Invalid user", BAD_REQUEST);
        }
        String userName = user.getName();

        return Response.ok().entity(vendingMachineService.reset(userName)).build();
    }

    @POST
    @Path("/buy")
    @RolesAllowed({BUYER})
    public Response buy(
            @QueryParam("productId") Long productId,
            @QueryParam("quantity") Long quantity,
            @Context SecurityContext sec) {
        Principal user = sec.getUserPrincipal();
        if (user == null) {
            throw new WebApplicationException("Invalid user", BAD_REQUEST);
        }
        String userName = user.getName();

        return Response.ok().entity(vendingMachineService.buy(userName, productId, quantity)).build();
    }


}
