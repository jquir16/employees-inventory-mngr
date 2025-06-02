package com.katabdb.employee.onboarding.mngr.services.implementation;

import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import com.katabdb.employee.onboarding.mngr.dto.user.UserResponse;
import com.katabdb.employee.onboarding.mngr.dto.user.UserUpdateRequest;
import com.katabdb.employee.onboarding.mngr.repository.IUserRepository;
import com.katabdb.employee.onboarding.mngr.services.implementation.security.PasswordService;
import com.katabdb.employee.onboarding.mngr.services.spec.IUserQueryService;
import com.katabdb.employee.onboarding.mngr.validation.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserQueryService {
    private final IUserRepository userRepository;
    private final PasswordService passwordService;

    @Autowired
    public UserService(IUserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public UserResponse getUserById(Integer id) {
        return UserMapper.toResponse(userRepository.getUserEntityById(id));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<UserResponse> findByEmail(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist"));
        return Optional.ofNullable(UserMapper.toResponse(user));
    }

    @Override
    public UserResponse updateUser(Integer id, UserUpdateRequest userUpdate) {
        UserEntity userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist"));

        if (userUpdate.name() != null) {
            userToUpdate.setName(userUpdate.name());
        }
        if (userUpdate.email() != null) {
            userToUpdate.setEmail(userUpdate.email());
        }
        if (userUpdate.role() != null) {
            userToUpdate.setRole(userUpdate.role());
        }
        if (userUpdate.status() != null) {
            userToUpdate.setStatus(userUpdate.status());
        }
        if (userUpdate.password() != null && !userUpdate.password().isEmpty()) {
            userToUpdate.setPassword(passwordService.encodePassword(userUpdate.password()));
        }

        userToUpdate.setUpdatedAt(new Date());
        return UserMapper.toResponse(userRepository.save(userToUpdate));
    }
}
