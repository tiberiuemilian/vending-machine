package mvpmatch.vm.service;

import lombok.extern.slf4j.Slf4j;
import mvpmatch.vm.domain.User;
import mvpmatch.vm.security.BCryptPasswordHasher;
import mvpmatch.vm.web.rest.UsernameAlreadyUsedException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
//@Transactional
@Slf4j
public class UserService {

    final BCryptPasswordHasher passwordHasher;

    @Inject
    public UserService(BCryptPasswordHasher passwordHasher) {
        this.passwordHasher = passwordHasher;
    }

    public User registerUser(User user) {
        User.findOneByUsername(user.getUsername())
                .ifPresent(
                        existingUser -> {
                            throw new UsernameAlreadyUsedException();
                        }
                );

        user.setUsername(user.getUsername());
        // new user gets initially a generated password
        user.password = passwordHasher.hash(user.getPassword());
        user.setRole(user.getRole().toUpperCase());

        User.persist(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    public void deleteUserById(Long id) {
        User.findByIdOptional(id).ifPresent(user -> {
            user.delete();
            log.debug("Deleted User: {}", user);
        });
    }

    public Optional<User> getUserById(Long id) {
        return User.findByIdOptional(id);
    }

    public Optional<User> updateUser(User user) {
        return User.<User>findByIdOptional(user.getId())
                .map(
                        updatedUser -> {
                            updatedUser.setUsername(user.getUsername());
                            updatedUser.setRole(user.getRole().toUpperCase());
                            updatedUser.setDeposit(user.getDeposit());
                            return updatedUser;
                        }
                );
    }

}
