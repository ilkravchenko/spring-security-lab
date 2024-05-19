package ua.kpi.its.lab.security.repo

import org.springframework.data.jpa.repository.JpaRepository
import ua.kpi.its.lab.security.entity.Module
import ua.kpi.its.lab.security.entity.Product

interface SoftwareProductRepository : JpaRepository<Product, Long>

interface SoftwareModuleRepository : JpaRepository<Module, Long>