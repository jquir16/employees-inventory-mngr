package com.katabdb.employee.onboarding.mngr.repository;

import com.katabdb.employee.onboarding.mngr.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Integer> {
    UserEntity getUserEntityById(Integer id);
}
