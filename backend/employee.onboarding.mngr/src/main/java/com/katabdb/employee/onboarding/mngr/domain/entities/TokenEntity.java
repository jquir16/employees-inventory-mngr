package com.katabdb.employee.onboarding.mngr.domain.entities;

import com.katabdb.employee.onboarding.mngr.domain.enums.TokenStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokens")
public class TokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_status")
    private TokenStatus tokenStatus;

    @Column(nullable = false)
    private Date expiresAt;

    public Integer getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public UserEntity getUser() {
        return user;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(UserEntity user) {
        this.user = user;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
}
