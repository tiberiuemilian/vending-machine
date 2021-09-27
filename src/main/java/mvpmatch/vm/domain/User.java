package mvpmatch.vm.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * An User.
 */
@Entity
@Table(name = "user")
@RegisterForReflection
@Data
public class User extends PanacheEntityBase implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
//    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 45)
    @Column(name = "username", length = 45, unique = true, nullable = false)
    public String username;

    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "password", length = 45, nullable = false)
    @JsonbTransient
    public String password;

    @NotNull
    @Min(0)
//    @Column(name = "deposit", precision = 21, scale = 2, nullable = false)
    @Column(name = "deposit", nullable = false)
//    public BigDecimal deposit;
    public Long deposit;

    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "role", length = 45, nullable = false)
    public String role;

    public static User update(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user can't be null");
        }
        var entity = User.<User>findById(user.id);
        if (entity != null) {
            entity.username = user.username;
            entity.password = user.password;
            entity.deposit = user.deposit;
            entity.role = user.role;
        }
        return entity;
    }

    public static User persistOrUpdate(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user can't be null");
        }
        if (user.id == null) {
            persist(user);
            return user;
        } else {
            return update(user);
        }
    }
    
    public static User findByUsername(String username) {
        return find("username", username).firstResult();
    }
}

