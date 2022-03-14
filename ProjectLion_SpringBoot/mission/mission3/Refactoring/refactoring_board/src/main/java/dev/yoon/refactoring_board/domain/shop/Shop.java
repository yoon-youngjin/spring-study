package dev.yoon.refactoring_board.domain.shop;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Shop {
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public Long getId() {
        return id;
    }
}
