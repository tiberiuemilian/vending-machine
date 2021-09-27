package mvpmatch.vm.web.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import mvpmatch.vm.service.VendingMachineService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.*;
import static mvpmatch.vm.domain.Role.BUYER;
import static mvpmatch.vm.domain.Role.SELLER;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@QuarkusTest
class VendingMachineControllerTest {

    @InjectMock
    VendingMachineService vendingMachineService;

    @Test
    @TestSecurity(authorizationEnabled = false)
    void negativeNumberOfCoinsNotValidForDeposit() {
        int nr = -3;

        given()
                .when()
                .queryParam("number", nr)
                .post("/api/deposit")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    @TestSecurity(authorizationEnabled = false)
    void badRequestForCoinNotMultipleOfFive() {
        int coin = 7;

        given()
                .when()
                .queryParam("coin", coin)
                .post("/api/deposit")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    @TestSecurity(user = "testUser", roles = {BUYER})
    void depositWorksForBuyer() {
        Integer coin = 5;
        int nr = 3;
        Long deposit = 20L;
        Mockito.when(vendingMachineService.deposit(eq("testUser"), eq(coin), eq(nr))).thenReturn(deposit);

        given()
                .when()
                .queryParam("coin", coin)
                .queryParam("number", nr)
                .post("/api/deposit")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(deposit.toString()));
    }

    @Test
    @TestSecurity(user = "testUser", roles = {SELLER})
    void depositForbiddenForSeller() {

        given()
                .when()
                .post("/api/deposit")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    @TestSecurity(user = "testUser", roles = {BUYER})
    void resetWorksForBuyer() {
        Long dummyAccountBalance = 10L;
        Mockito.when(vendingMachineService.reset(any())).thenReturn(dummyAccountBalance);

        given()
                .when()
                .post("/api/reset")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(dummyAccountBalance.toString()));
    }

    @Test
    @TestSecurity(user = "testUser", roles = {SELLER})
    void resetForbiddenForSeller() {
        given()
                .when()
                .post("/api/reset")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    void buy() {
    }
}
