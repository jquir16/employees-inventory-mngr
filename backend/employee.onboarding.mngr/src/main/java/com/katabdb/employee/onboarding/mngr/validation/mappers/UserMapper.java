package com.katabdb.employee.onboarding.mngr.validation.mappers;

import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import com.katabdb.employee.onboarding.mngr.dto.user.UserResponse;

public class UserMapper {

    public static UserResponse toResponse(UserEntity entity) {
        if (entity == null) return null;
        return UserResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .status(entity.getStatus())
                .password(entity.getPassword())
                .build();
    }
}
