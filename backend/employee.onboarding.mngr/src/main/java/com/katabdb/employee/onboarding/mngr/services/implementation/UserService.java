package com.katabdb.employee.onboarding.mngr.services.implementation;

import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import com.katabdb.employee.onboarding.mngr.dto.user.UserResponse;
import com.katabdb.employee.onboarding.mngr.dto.user.UserUpdateRequest;
import com.katabdb.employee.onboarding.mngr.repository.IUserRepository;
import com.katabdb.employee.onboarding.mngr.services.implementation.security.PasswordService;
import com.katabdb.employee.onboarding.mngr.services.spec.IUserQueryService;
import com.katabdb.employee.onboarding.mngr.validation.mappers.UserMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserQueryService {

    private static final String USER_NOT_FOUND = "User not found with ID: %d";

    private final IUserRepository userRepository;
    private final PasswordService passwordService;

    public UserService(IUserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Integer id) {
        return userRepository.findById(id)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserMapper::toResponse);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Integer id, UserUpdateRequest userUpdate) {
        UserEntity userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, id)));

        updateUserFields(userToUpdate, userUpdate);
        userToUpdate.setUpdatedAt(new Date());

        return UserMapper.toResponse(userRepository.save(userToUpdate));
    }

    private void updateUserFields(UserEntity user, UserUpdateRequest updateRequest) {
        Optional.ofNullable(updateRequest.name()).ifPresent(user::setName);
        Optional.ofNullable(updateRequest.email()).ifPresent(user::setEmail);
        Optional.ofNullable(updateRequest.role()).ifPresent(user::setRole);
        Optional.ofNullable(updateRequest.status()).ifPresent(user::setStatus);

        Optional.ofNullable(updateRequest.password())
                .filter(password -> !password.isEmpty())
                .ifPresent(password -> user.setPassword(passwordService.encodePassword(password)));
    }
}