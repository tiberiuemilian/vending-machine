package mvpmatch.vm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A Product.
 */
@Entity
@Table(name = "product")
@RegisterForReflection
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
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
    public Integer amountAvailable;

    @NotNull
    @Min(1)
    @Column(name = "cost", nullable = false)
    public Integer cost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties({"username", "password", "deposit", "role"})
    public User seller;

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

