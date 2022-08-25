package dev.yoon.stock.repository;

import dev.yoon.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// 편의성을 위해 Stock Entity를 사용한다.
// 실무에서는 별도의 JDBC를 사용하거나 등등의 방식을 사용해야한다.
public interface LockRepository extends JpaRepository<Stock, Long> {

    @Query(value = "select get_lock(:key, 3000)", nativeQuery = true)
    void getLock(String key);

    @Query(value = "select release_lock(:key)", nativeQuery = true)
    void releaseLock(String key);


}
