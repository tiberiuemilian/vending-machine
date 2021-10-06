package mvpmatch.vm.service.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import mvpmatch.vm.domain.Product;

import java.util.Map;

@RegisterForReflection
@Data
public class PurchaseStatus {

    private Integer totalSpent;

    private Product product;

    private Map<Integer, Integer> change;
}
