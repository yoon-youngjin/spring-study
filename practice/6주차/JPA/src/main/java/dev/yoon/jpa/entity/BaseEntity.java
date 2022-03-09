package dev.yoon.jpa.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.Instant;

// BaseEntity ? 만약에 모든 엔티티들이 가져야할 속성을 정의하고 싶은 경우 사용
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
public abstract class BaseEntity {
    @CreatedDate
    @Column(updatable = false)
    // 테이블이 생성된 시간
    private Instant createdAt;

    @LastModifiedDate
    @Column(updatable = true)
    // 테이블이 마지막으로 업데이트된 시간
    private Instant updatedAt;


}
