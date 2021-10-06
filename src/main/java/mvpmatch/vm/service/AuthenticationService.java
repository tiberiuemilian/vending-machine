package mvpmatch.vm.service;

import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import lombok.extern.slf4j.Slf4j;
import mvpmatch.vm.domain.User;
import mvpmatch.vm.security.BCryptPasswordHasher;
import mvpmatch.vm.security.UsernameNotFoundException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@ApplicationScoped
@Slf4j
public class AuthenticationService {

    final BCryptPasswordHasher passwordHasher;

    @Inject
    public AuthenticationService(BCryptPasswordHasher passwordHasher) {
        this.passwordHasher = passwordHasher;
    }

    public QuarkusSecurityIdentity authenticate(String username, String password) {
        User user = loadByUsername(username);

        if (passwordHasher.checkPassword(password, user.password)) {
            return createQuarkusSecurityIdentity(user);
        }

        log.debug("Authentication failed: password does not match stored value");
        throw new AuthenticationFailedException("Authentication failed: password does not match stored value");
    }

    private User loadByUsername(String username) {
        log.debug("Authenticating {}", username);

        return User
                .findOneByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " was not found in the database"));
    }

    private QuarkusSecurityIdentity createQuarkusSecurityIdentity(User user) {
        QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder();
        builder.setPrincipal(new QuarkusPrincipal(user.getUsername()));
        builder.addCredential(new io.quarkus.security.credential.PasswordCredential(user.password.toCharArray()));
        builder.addRole(user.getRole());
        return builder.build();
    }

    public static String getLoggedUserName(SecurityContext sec) {
        Principal user = sec.getUserPrincipal();
        if (user == null) {
            throw new WebApplicationException("Invalid user", BAD_REQUEST);
        }
        String userName = user.getName();
        return userName;
    }

}
