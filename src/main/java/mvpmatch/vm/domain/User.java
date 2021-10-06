package mvpmatch.vm.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;

import static java.util.Map.entry;

/**
 * An User.
 */
@Entity
@Table(name = "user")
@RegisterForReflection
@Data()
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends PanacheEntityBase implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "username", length = 45, unique = true, nullable = false)
    public String username;

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "password", length = 100, nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String password;

    @NotNull
    @ElementCollection
    @CollectionTable(name = "deposit")
    @MapKeyColumn(name = "denomination")
    @Column(name = "nrOfCoins")
    @org.hibernate.annotations.OrderBy(clause = "denomination DESC")
    public Map<Integer, Integer> deposit = new HashMap<>();

    public static Map<Integer, Integer> emptyDeposit() {
        return new TreeMap<>(
                Map.ofEntries(
                        entry(100, 0),
                        entry(50, 0),
                        entry(20, 0),
                        entry(10, 0),
                        entry(5, 0)
                )
        ).descendingMap();
    }

    public static class ReverseComparator implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            return o2.compareTo( o1 );
        }
    }

    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "role", length = 45, nullable = false)
    public String role;
    
    public static Optional<User> findOneByUsername(String username) {
        return find("username", username).firstResultOptional();
    }

    public boolean sellsProduct(Product product) {
        return (product != null) && getId().equals(product.getSeller().getId());
    }
}

