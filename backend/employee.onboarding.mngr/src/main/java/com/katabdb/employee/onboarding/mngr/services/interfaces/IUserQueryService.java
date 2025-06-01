package com.katabdb.employee.onboarding.mngr.services.interfaces;

import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface IUserQueryService {
    UserEntity getUserById(Integer id);
    List<UserEntity> getAllUsers();
    Boolean existsByEmail(String email);
    Optional<UserEntity> findByEmail(String email);
    UserEntity updateUser(Integer id, UserEntity user);
}
