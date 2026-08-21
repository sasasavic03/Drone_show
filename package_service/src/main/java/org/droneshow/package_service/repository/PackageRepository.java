package org.droneshow.package_service.repository;

import org.droneshow.package_service.model.Package;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PackageRepository extends JpaRepository<Package, Long> {

    Optional<Package> findByNameIgnoreCase(String name);
}
