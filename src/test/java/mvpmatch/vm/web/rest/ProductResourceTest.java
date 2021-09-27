package mvpmatch.vm.web.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import mvpmatch.vm.domain.Product;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.OK;
import static mvpmatch.vm.domain.Role.SELLER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@QuarkusTest
class ProductResourceTest {

    @InjectSpy
    ProductResource productResourceSpy;

    @Test
    @TestSecurity(user = "testUser", roles = {SELLER})
//    @PrepareForTest(ProductResource.class)
    void sellerCanCreateProduct() throws Exception {
//        ProductResource productResourceSpy = Mockito.spy(new ProductResource());
//        doNothing().when(productResourceSpy, "validateCost", any());
//        PowerMockito.doNothing().when(productResourceSpy, "validateCost", any());
//        PowerMockito.doNothing().when(productResourceSpy, "validateSellerId", any(), any());

       doNothing().when(productResourceSpy).validateCost(any());
       doNothing().when(productResourceSpy).validateSellerId(any(), any());

        try (MockedStatic<Product> productMock = Mockito.mockStatic(Product.class)) {
            productMock.when(() -> Product.persistOrUpdate(any()))
                    .thenReturn(new Product());
        }
//        MockedStatic<Product> productMock = Mockito.mockStatic(Product.class);
//        productMock.when(() -> Product.persistOrUpdate(any()))
//                .thenReturn(new Product());

        Product product = new Product(1L, "colgate", 10L, 2L,2L);
//        Product mockedPr,product
        given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/api/products")
                .then()
                .statusCode(OK.getStatusCode());
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
