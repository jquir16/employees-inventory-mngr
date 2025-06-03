package com.katabdb.employee.onboarding.mngr.domain.entities;

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
@Table(name = "access_requests")
public class AccessRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private UserEntity user;

    @Column(name = "systems", columnDefinition = "text[]", nullable = false)
    private String[] systems;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "requested_at")
    private Date requestedAt;
}
