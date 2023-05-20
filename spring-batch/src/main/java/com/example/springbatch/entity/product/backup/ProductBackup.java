package com.example.springbatch.entity.product.backup;

import com.example.springbatch.entity.product.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
@Table(indexes = {
        @Index(name = "idx_product_backup_1", columnList = "store_id")
})
public class ProductBackup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long originId;

    private String name;
    private long price;
    private LocalDate createDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "store_id")
    private StoreBackup store;

    @Builder
    public ProductBackup(Product product) {
        this.originId = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.createDate = product.getCreateDate();
    }

    public void changeStore(StoreBackup store) {
        this.store = store;
    }
}