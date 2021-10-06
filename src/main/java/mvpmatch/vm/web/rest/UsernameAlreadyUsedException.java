package mvpmatch.vm.web.rest;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

public class UsernameAlreadyUsedException extends BadRequestException {

    public UsernameAlreadyUsedException() {
        super(Response.status(BAD_REQUEST).entity("Login name already used!").build());
    }
}
