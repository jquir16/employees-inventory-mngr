package com.katabdb.employee.onboarding.mngr.services.spec;

import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import com.katabdb.employee.onboarding.mngr.dto.user.UserResponse;
import com.katabdb.employee.onboarding.mngr.dto.user.UserUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface IUserQueryService {
    UserResponse getUserById(Integer id);
    List<UserResponse> getAllUsers();
    Boolean existsByEmail(String email);
    Optional<UserResponse> findByEmail(String email);
    UserResponse updateUser(Integer id, UserUpdateRequest user);
}
