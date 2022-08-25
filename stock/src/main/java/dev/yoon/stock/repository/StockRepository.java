package dev.yoon.stock.repository;

import dev.yoon.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Lock(value = LockModeType.PESSIMISTIC_WRITE) // Spring Data JPA 에서는 @Lock 어노테이션을 통해 Pessimistic Lock 을 쉽게 구현할 수 있다.
    @Query("select s from Stock s where s.id =:id")
    Stock findByIdWithPessimisticLock(Long id);

    @Lock(value = LockModeType.OPTIMISTIC) // Spring Data JPA 에서는 @Lock 어노테이션을 통해 Optimistic Lock 을 쉽게 구현할 수 있다.
    @Query("select s from Stock s where s.id =:id")
    Stock findByIdWithOptimisticLock(Long id);
}
