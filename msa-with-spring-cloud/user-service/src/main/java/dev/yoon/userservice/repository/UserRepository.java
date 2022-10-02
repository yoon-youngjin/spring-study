package dev.yoon.userservice.repository;

import dev.yoon.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUserId(String userId);

    Optional<UserEntity> findByEmail(String email);
}
