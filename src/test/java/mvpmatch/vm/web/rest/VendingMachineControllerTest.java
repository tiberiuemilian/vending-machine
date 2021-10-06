package mvpmatch.vm.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import mvpmatch.vm.service.VendingMachineService;
import mvpmatch.vm.service.dto.PurchaseStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static java.util.Map.entry;
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

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    @TestSecurity(authorizationEnabled = false)
    void negativeNumberOfCoinsNotValidForDeposit() {
        int nr = -3;

        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .when()
                .queryParam("number", nr)
                .post("/api/deposit")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    @TestSecurity(authorizationEnabled = false)
    void badRequestForNotAcceptedCoin() {
        int coin = 7;

        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .when()
                .queryParam("coin", coin)
                .post("/api/deposit")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    @TestSecurity(user = "testUser", roles = {BUYER})
    void depositWorksForBuyer() throws JsonProcessingException {
        Integer coin = 5;
        int nr = 3;
        Map<Integer, Integer> dummyDeposit = new HashMap<>(
                Map.ofEntries(
                        entry(100, 1),
                        entry(50, 0),
                        entry(20, 0),
                        entry(10, 0),
                        entry(5, 2)
                )
        );
        Mockito.when(vendingMachineService.deposit(any(), eq(coin), eq(nr))).thenReturn(dummyDeposit);

        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .when()
                .queryParam("coin", coin)
                .queryParam("number", nr)
                .post("/api/deposit")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(mapper.writer().writeValueAsString(dummyDeposit)));
    }

    @Test
    @TestSecurity(user = "testUser", roles = {SELLER})
    void depositForbiddenForSeller() {

        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .when()
                .post("/api/deposit")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    @TestSecurity(user = "testUser", roles = {BUYER})
    void resetWorksForBuyer() throws JsonProcessingException {
        Map<Integer, Integer> dummyAccountBalance = new HashMap<>(
                Map.ofEntries(
                        entry(100, 1),
                        entry(50, 0),
                        entry(20, 0),
                        entry(10, 0),
                        entry(5, 2)
                )
        );

        Mockito.when(vendingMachineService.reset(any())).thenReturn(dummyAccountBalance);

        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .when()
                .post("/api/reset")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(mapper.writer().writeValueAsString(dummyAccountBalance)));
    }

    @Test
    @TestSecurity(user = "testUser", roles = {SELLER})
    void resetForbiddenForSeller() {
        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .when()
                .post("/api/reset")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    @TestSecurity(user = "testUser", roles = {BUYER})
    void buyWorksForBuyer() throws JsonProcessingException {
        PurchaseStatus purchaseStatus = new PurchaseStatus();
        Mockito.when(vendingMachineService.buy(any(), any(), any())).thenReturn(purchaseStatus);

        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .queryParam("productId", 1)
                .queryParam("quantity", 1)
                .when()
                .post("/api/buy")
                .then()
                .statusCode(OK.getStatusCode())
                .body(is(mapper.writer().writeValueAsString(purchaseStatus)));
    }

    @Test
    @TestSecurity(user = "testUser", roles = {SELLER})
    void buyForbiddenForSeller() {
        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .queryParam("productId", 1)
                .queryParam("quantity", 1)
                .when()
                .post("/api/buy")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

}
