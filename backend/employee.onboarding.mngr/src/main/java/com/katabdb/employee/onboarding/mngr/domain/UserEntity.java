package com.katabdb.employee.onboarding.mngr.domain;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    private Integer id;
    private String name;
    private String email;
    private String role;

    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;
    private Date created_at;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public enum EmployeeStatus {
        PENDING, APPROVED, REJECTED
    }
}
