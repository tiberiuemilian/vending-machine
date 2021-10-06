package mvpmatch.vm.web.rest;

import lombok.extern.slf4j.Slf4j;
import mvpmatch.vm.service.VendingMachineService;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Arrays;
import java.util.stream.IntStream;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static mvpmatch.vm.config.Constants.COIN_TYPES;
import static mvpmatch.vm.domain.Role.BUYER;
import static mvpmatch.vm.service.AuthenticationService.getLoggedUserName;

@Slf4j
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class VendingMachineController {

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
            @Valid @Min(value = 1, message = "The value must be positive") @QueryParam("number") int number,
            @Context SecurityContext sec) {

        if (IntStream.of(COIN_TYPES).noneMatch(coin::equals)) {
            throw new BadRequestException(Response.status(BAD_REQUEST).entity("Wrong coin value. Allowed coins are: " + Arrays.toString(COIN_TYPES)).build());
        }

        return Response.ok().entity(vendingMachineService.deposit(getLoggedUserName(sec), coin, number)).build();
    }

    @POST
    @Path("/reset")
    @RolesAllowed({BUYER})
    public Response reset(@Context SecurityContext sec) {
        return Response.ok().entity(vendingMachineService.reset(getLoggedUserName(sec))).build();
    }

    @POST
    @Path("/buy")
    @RolesAllowed({BUYER})
    public Response buy(
            @QueryParam("productId") Long productId,
            @Valid @Min(value = 1, message = "The quantity must be positive") @QueryParam("quantity") Integer quantity,
            @Context SecurityContext sec) {

        return Response.ok().entity(vendingMachineService.buy(getLoggedUserName(sec), productId, quantity)).build();
    }

}
