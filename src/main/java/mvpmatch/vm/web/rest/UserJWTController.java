package mvpmatch.vm.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import mvpmatch.vm.security.jwt.TokenProvider;
import mvpmatch.vm.service.AuthenticationService;
import mvpmatch.vm.web.rest.vm.LoginVM;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Controller to authenticate users.
 */
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserJWTController {

    final AuthenticationService authenticationService;

    final TokenProvider tokenProvider;

    @Inject
    public UserJWTController(AuthenticationService authenticationService, TokenProvider tokenProvider) {
        this.authenticationService = authenticationService;
        this.tokenProvider = tokenProvider;
    }

    @POST
    @Path("/authenticate")
    @PermitAll
    public Response authorize(@Valid LoginVM loginVM) {
        try {
            QuarkusSecurityIdentity identity = authenticationService.authenticate(loginVM.getUsername(), loginVM.getPassword());
            String jwt = tokenProvider.createToken(identity);
            return Response.ok().entity(new JWTToken(jwt)).header("Authorization", "Bearer " + jwt).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    @RegisterForReflection
    public static class JWTToken {
        @JsonProperty("id_token")
        public String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
