package com.katabdb.employee.onboarding.mngr.repository;

import com.katabdb.employee.onboarding.mngr.domain.entities.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IInventoryRepository extends JpaRepository<InventoryEntity, Integer> {
    InventoryEntity getInventoryEntityById(Integer id);
    List<InventoryEntity> findAllByUserId(Integer userId);
    Optional<InventoryEntity> findBySerialNumber(String serialNumber);
}
