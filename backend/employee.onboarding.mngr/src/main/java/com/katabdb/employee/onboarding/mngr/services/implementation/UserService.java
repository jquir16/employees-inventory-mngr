package com.katabdb.employee.onboarding.mngr.services.implementation;

import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import com.katabdb.employee.onboarding.mngr.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
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


}
