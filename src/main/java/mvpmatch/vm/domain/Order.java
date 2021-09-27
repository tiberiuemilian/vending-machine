package mvpmatch.vm.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

/**
 * An User.
 */
@Entity
@Table(name = "Order")
@RegisterForReflection
@Data
public class Order extends PanacheEntityBase implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @OneToOne
    @JoinColumn(unique = true)
    public User customer;

    @OneToMany
    public Set<OrderItem> items;

    @Column(name = "date")
    public Timestamp date;
}
