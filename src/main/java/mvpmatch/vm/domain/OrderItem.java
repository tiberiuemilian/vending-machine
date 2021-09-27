package mvpmatch.vm.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * An OrderItem.
 */
@Entity
@Table(name = "order_item")
@RegisterForReflection
@Data
public class OrderItem extends PanacheEntityBase implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @Min(1)
    @Column(name = "cost", nullable = false)
    public Long cost;

    @NotNull
    @Min(0)
    @Column(name = "quantity", nullable = false)
    public Long quantity;

    @OneToOne
    @JoinColumn(unique = true)
    public Product product;

}
