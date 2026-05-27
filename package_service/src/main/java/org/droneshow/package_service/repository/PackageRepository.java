package org.droneshow.package_service.repository;

import org.droneshow.package_service.model.Package;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {

    Page<Package> findByIsActive(Boolean isActive, Pageable pageable);
}

