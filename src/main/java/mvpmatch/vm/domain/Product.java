package mvpmatch.vm.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A Product.
 */
@Entity
@Table(name = "product")
@RegisterForReflection
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @NotNull
    @Size(max = 45)
    @Column(name = "product_name", length = 45, nullable = false)
    public String productName;

    @NotNull
    @Min(0)
    @Column(name = "amount_available", nullable = false)
    public Long amountAvailable;

    @NotNull
    @Min(1)
    @Column(name = "cost", precision = 21, scale = 2, nullable = false)
//    public BigDecimal cost;
    public Long cost;

//    @OneToOne
//    @JoinColumn(unique = true)
//    public User seller;
    public Long sellerId;

    public static Product update(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("product can't be null");
        }
        var entity = Product.<Product>findById(product.id);
        if (entity != null) {
            entity.productName = product.productName;
            entity.amountAvailable = product.amountAvailable;
            entity.cost = product.cost;
//            entity.sellerId = product.sellerId;
        }
        return entity;
    }

    public static Product persistOrUpdate(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("product can't be null");
        }
        if (product.id == null) {
            persist(product);
            return product;
        } else {
            return update(product);
        }
    }


}

