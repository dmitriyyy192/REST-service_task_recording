package com.ownsecurity.repository;

import com.ownsecurity.entity.RoleEntity;
import com.ownsecurity.entity.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(ERole name);
    Optional<RoleEntity> findById(Long id);
}
