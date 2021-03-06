package mvpmatch.vm.web.rest;

import io.quarkus.security.Authenticated;
import lombok.extern.slf4j.Slf4j;
import mvpmatch.vm.domain.User;
import mvpmatch.vm.service.UserService;
import mvpmatch.vm.web.rest.errors.BadRequestAlertException;
import mvpmatch.vm.web.rest.errors.LoginAlreadyUsedException;
import mvpmatch.vm.web.rest.util.HeaderUtil;
import mvpmatch.vm.web.rest.util.ResponseUtil;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Optional;

import static javax.ws.rs.core.UriBuilder.fromPath;
import static mvpmatch.vm.domain.Role.isValidRole;

/**
 * REST controller for managing {@link mvpmatch.vm.domain.User}.
 */
@Slf4j
@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class UserResource {

    private static final String ENTITY_NAME = "user";

    @ConfigProperty(name = "application.name")
    String applicationName;

    final UserService userService;

    @Inject
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    /**
     * {@code POST  /users} : Create a new user.
     *
     * @param user the user to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the user has already an ID.
     */
    @POST
    @Transactional
    public Response createUser(@Valid User user, @Context UriInfo uriInfo) {
        log.debug("REST request to save User : {}", user);
        if (user.id != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (!isValidRole(user.getRole())) {
            throw new BadRequestAlertException("The role is not allowed. The user must be either a BUYER or a SELLER.", ENTITY_NAME, "wrongRole");
        }
        var result = userService.registerUser(user);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /users} : Updates an existing user.
     *
     * @param user the user to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated user,
     * or with status {@code 400 (Bad Request)} if the user is not valid,
     * or with status {@code 500 (Internal Server Error)} if the user couldn't be updated.
     */
    @PUT
    @Authenticated
    @Transactional
    public Response updateUser(@Valid User user) {
        log.debug("REST request to update User : {}", user);
        if (user.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!isValidRole(user.getRole())) {
            throw new BadRequestAlertException("The role is not allowed. The user must be either a BUYER or a SELLER.", ENTITY_NAME, "wrongRole");
        }

        Optional<User> existingUser = User.findOneByUsername(user.getUsername());
        if (existingUser.isPresent() && !(existingUser.get().getId().equals(user.getId()))) {
            throw new LoginAlreadyUsedException();
        }

        Optional<User> updatedUser = userService.updateUser(user);
        return ResponseUtil.wrapOrNotFound(updatedUser, HeaderUtil.createAlert(applicationName, "userManagement.updated", user.getId().toString()));
    }

    /**
     * {@code DELETE  /users/:id} : delete the "id" user.
     *
     * @param id the id of the user to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Authenticated
    @Path("/{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        log.debug("REST request to delete User : {}", id);
        userService.deleteUserById(id);
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /users} : get all the users.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of users in body.
     */
    @GET
    @Authenticated
    public List<User> getAllUsers() {
        log.debug("REST request to get all Users");
        return User.findAll().list();
    }

    /**
     * {@code GET  /users/:id} : get the "id" user.
     *
     * @param id the id of the user to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the user, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Authenticated
    @Path("/{id}")
    public Response getUser(@PathParam("id") Long id) {
        log.debug("REST request to get User : {}", id);
        return ResponseUtil.wrapOrNotFound(userService.getUserById(id));
    }
}
