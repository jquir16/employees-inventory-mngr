package com.katabdb.employee.onboarding.mngr.repository;

import com.katabdb.employee.onboarding.mngr.domain.entities.AccessRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAccessRequestRepository extends JpaRepository<AccessRequestEntity, Integer> {
    AccessRequestEntity getAccessRequestEntityById(Integer id);
    List<AccessRequestEntity> findAllByUserId(Integer userId);
}
