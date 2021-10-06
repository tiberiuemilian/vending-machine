package mvpmatch.vm.service;

import mvpmatch.vm.domain.Product;
import mvpmatch.vm.domain.User;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductService {

    public Product createProduct(String userName, Product product) {
        User loggedUser = User.findOneByUsername(userName).get();
        product.setSeller(loggedUser);
        return Product.persistOrUpdate(product);
    }

}
