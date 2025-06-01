package com.katabdb.employee.onboarding.mngr.repository;

import com.katabdb.employee.onboarding.mngr.domain.entities.TokenEntity;
import com.katabdb.employee.onboarding.mngr.domain.enums.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IAuthRepository extends JpaRepository<TokenEntity, Integer> {
    Optional<TokenEntity> findByToken(String token);
    List<TokenEntity> findAllByTokenStatus(TokenStatus tokenStatus);
}
