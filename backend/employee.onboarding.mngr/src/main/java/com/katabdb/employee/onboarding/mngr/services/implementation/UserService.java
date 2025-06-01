package com.katabdb.employee.onboarding.mngr.services.implementation;

import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import com.katabdb.employee.onboarding.mngr.repository.IUserRepository;
import com.katabdb.employee.onboarding.mngr.services.interfaces.IUserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserQueryService {
    private final IUserRepository userRepository;

    @Autowired
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity getUserById(Integer id) {
        return userRepository.getUserEntityById(id);
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserEntity updateUser(Integer id, UserEntity userUpdate) {
        UserEntity userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist"));

        if (userUpdate.getName() != null) {
            userToUpdate.setName(userUpdate.getName());
        }
        if (userUpdate.getEmail() != null) {
            userToUpdate.setEmail(userUpdate.getEmail());
        }
        if (userUpdate.getRole() != null) {
            userToUpdate.setRole(userUpdate.getRole());
        }
        if (userUpdate.getPassword() != null && !userUpdate.getPassword().isEmpty()) {
            userToUpdate.setPassword(userUpdate.getPassword());
        }

        return userRepository.save(userToUpdate);
    }
}
