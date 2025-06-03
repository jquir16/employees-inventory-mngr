package com.katabdb.employee.onboarding.mngr.controller;

import com.katabdb.employee.onboarding.mngr.domain.entities.UserEntity;
import com.katabdb.employee.onboarding.mngr.repository.IAuthRepository;
import com.katabdb.employee.onboarding.mngr.repository.IUserRepository;
import com.katabdb.employee.onboarding.mngr.services.implementation.UserService;
import com.katabdb.employee.onboarding.mngr.services.implementation.security.JWTService;
import com.katabdb.employee.onboarding.mngr.validation.formats.JWTValidator;
import com.katabdb.employee.onboarding.mngr.validation.mappers.UserMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserResponseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JWTService jwtService;

    @MockBean
    private IAuthRepository authRepository;

    @MockBean
    private IUserRepository userRepository;

    @MockBean
    private JWTValidator jwtValidator;

    @Test
    void getUserById_returnsUser() throws Exception {
        UserEntity user = new UserEntity();
        Mockito.when(userService.getUserById(anyInt())).thenReturn(UserMapper.toResponse(user));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUsers_returnsList() throws Exception {
        UserEntity user1 = new UserEntity();
        UserEntity user2 = new UserEntity();
        var userResponses = Arrays.asList(
                UserMapper.toResponse(user1),
                UserMapper.toResponse(user2)
        );
        Mockito.when(userService.getAllUsers()).thenReturn(userResponses);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}