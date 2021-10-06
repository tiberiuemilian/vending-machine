package mvpmatch.vm.web.rest;

import lombok.extern.slf4j.Slf4j;
import mvpmatch.vm.domain.Product;
import mvpmatch.vm.domain.User;
import mvpmatch.vm.service.ProductService;
import mvpmatch.vm.web.rest.errors.BadRequestAlertException;
import mvpmatch.vm.web.rest.util.HeaderUtil;
import mvpmatch.vm.web.rest.util.ResponseUtil;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.UriBuilder.fromPath;
import static mvpmatch.vm.domain.Role.SELLER;
import static mvpmatch.vm.service.AuthenticationService.getLoggedUserName;

@Slf4j
@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class ProductResource {

    private static final String ENTITY_NAME = "product";

    @ConfigProperty(name = "application.name")
    String applicationName;

    final ProductService productService;

    @Inject
    public ProductResource(ProductService productService) {
        this.productService = productService;
    }

    /**
     * {@code POST  /products} : Create a new product.
     *
     * @param product the product to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new product, or with status {@code 400 (Bad Request)} if the product has already an ID.
     */
    @POST
    @Transactional
    @RolesAllowed({SELLER})
    public Response createProduct(@Valid Product product, @Context UriInfo uriInfo, @Context SecurityContext sec) {
        log.debug("REST request to save Product : {}", product);
        validateCost(product);
        var result = productService.createProduct(getLoggedUserName(sec), product);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /products} : Updates an existing product.
     *
     * @param product the product to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated product,
     * or with status {@code 400 (Bad Request)} if the product is not valid,
     * or with status {@code 500 (Internal Server Error)} if the product couldn't be updated.
     */
    @PUT
    @Transactional
    @RolesAllowed({SELLER})
    public Response updateProduct(@Valid @RequestBody Product product, @Context SecurityContext sec) {
        log.debug("REST request to update Product : {}", product);

        validateCost(product);

        var result = Product.update(product);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, product.id.toString()).forEach(response::header);
        return response.build();
    }

    public void validateCost(Product product) {
        if (product.getCost() == null ||
                product.getCost() % 5 != 0) {
            throw new BadRequestAlertException("Product's price must be multiple of 5 in order to give the change.", ENTITY_NAME, "wrongCost");
        }
    }

    /**
     * {@code DELETE  /products/:id} : delete the "id" product.
     *
     * @param id the id of the product to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    @RolesAllowed({SELLER})
    public Response deleteProduct(@PathParam("id") Long id, @Context SecurityContext sec) {
        log.debug("REST request to delete Product : {}", id);
        User loggedUser = User.findOneByUsername(getLoggedUserName(sec)).get();
        Product.<Product>findByIdOptional(id).ifPresent(product -> {
            if (!loggedUser.sellsProduct(product)) {
                throw new ForbiddenException(Response.status(FORBIDDEN).entity("Product can be deleted only by its seller.").build());
            }
            product.delete();
        });

        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /products} : get all the products.
     *
     * @return the {@link Response} with status {@code 200 (OK)} and the list of products in body.
     */
    @GET
    public List<Product> getAllProducts() {
        log.debug("REST request to get all Products");
        return Product.findAll().list();
    }

    /**
     * {@code GET  /products/:id} : get the "id" product.
     *
     * @param id the id of the product to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the product, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")
    public Response getProduct(@PathParam("id") Long id) {
        log.debug("REST request to get Product : {}", id);
        Optional<Product> product = Product.findByIdOptional(id);
        return ResponseUtil.wrapOrNotFound(product);
    }

}
