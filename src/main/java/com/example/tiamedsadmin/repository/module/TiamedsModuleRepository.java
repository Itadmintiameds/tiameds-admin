package com.example.tiamedsadmin.repository.module;

import com.example.tiamedsadmin.entity.module.TiamedsModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TiamedsModuleRepository extends JpaRepository<TiamedsModule, Long> {

    Optional<TiamedsModule> findByModuleNameIgnoreCase(String moduleName);
}