package com.katabdb.employee.onboarding.mngr.repository;

import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity getUserEntityById(Integer id);
    Boolean existsByEmail(String email);
    Optional<UserEntity> findByEmail(String email);
}
