package mvpmatch.vm.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import mvpmatch.vm.domain.Product;
import mvpmatch.vm.service.ProductService;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.*;
import static mvpmatch.vm.domain.Role.BUYER;
import static mvpmatch.vm.domain.Role.SELLER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@QuarkusTest
class ProductResourceTest {

    @InjectMock
    ProductService productService;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    @TestSecurity(user = "testUser", roles = {SELLER})
    void sellerCanCreateProduct() throws Exception {
        Product product = Product.builder()
                .id(1L)
                .productName("colgate")
                .amountAvailable(10)
                .cost(20)
                .build();

        when(productService.createProduct(anyString(), any())).thenReturn(product);

        given()
                .contentType(ContentType.JSON)
                .body(mapper.writer().writeValueAsString(product))
                .when()
                .post("/api/products")
                .then()
                .statusCode(CREATED.getStatusCode());
    }

    @Test
    @TestSecurity(authorizationEnabled = true)
    void createProductUnavailableForUnauthenticatedUser() throws Exception {
        Product product = Product.builder()
                .productName("colgate")
                .amountAvailable(10)
                .cost(20)
                .build();

        when(productService.createProduct(anyString(), any())).thenReturn(product);

        given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/api/products")
                .then()
                .statusCode(UNAUTHORIZED.getStatusCode());
    }

    @Test
    @TestSecurity(user = "testUser", roles = {BUYER})
    void createProductForbiddenForBuyer() throws Exception {
        Product product = Product.builder()
                .productName("colgate")
                .amountAvailable(10)
                .cost(20)
                .build();

        when(productService.createProduct(anyString(), any())).thenReturn(product);

        given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/api/products")
                .then()
                .statusCode(FORBIDDEN.getStatusCode());
    }

    @Test
    void updateProduct() {
    }

    @Test
    void deleteProduct() {
    }

    @Test
    void getAllProducts() {
    }

    @Test
    void getProduct() {
    }
}
