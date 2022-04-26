package dev.aquashdw.community.repository;

import dev.aquashdw.community.entity.AreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AreaRepository extends JpaRepository<AreaEntity, Long> {

    @Query(
            "select a " +
                    "from AreaEntity a " +
                    "order by " +
                    "(a.latitude - :latitude) * (a.latitude - :latitude)" +
                    " + (a.longitude - :longtitude) * (a.longitude - :longtitude)"
    )
    List<AreaEntity> findByCloseRange(Double latitude, Double longtitude);

    /**
     * nativeQuery = true: 실제 데이터베이스에서 사용하는 SQL문을 그대로 사용함
     */
    @Query(
            nativeQuery = true,
            value =
                    "select top 1 * " +
                            "from area a " +
                            "order by " +
                            "  (a.latitude - :latitude) * (a.latitude - :latitude)" +
                            " + (a.longitude - :longitude) * (a.longitude - :longitude)"
    )
    AreaEntity findTop1ByClosest(Double latitude, Double longitude);
}
