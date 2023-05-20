package com.example.springbatch.entity.product.backup;

import com.example.springbatch.entity.product.Product;
import com.example.springbatch.entity.product.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class StoreBackup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long originId;
    private String name;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<ProductBackup> products = new ArrayList<>();

    public StoreBackup(Store store) {
        this.name = store.getName();
        this.originId = store.getId();
//        this.addProducts(store.getProducts());
    }

    public void addProducts (List<Product> originProducts) {
        for (Product originProduct : originProducts) {
            addProduct(new ProductBackup(originProduct));
        }
    }

    public void addProduct(@NonNull ProductBackup product){
        product.changeStore(this);
        this.products.add(product);
    }

    public long sumProductsPrice(){
        return products.stream()
                .mapToLong(ProductBackup::getPrice)
                .sum();
    }

}