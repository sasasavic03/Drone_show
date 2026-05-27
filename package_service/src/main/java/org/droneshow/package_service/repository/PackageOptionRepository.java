package org.droneshow.package_service.repository;

import org.droneshow.package_service.model.PackageOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageOptionRepository extends JpaRepository<PackageOption, Long> {

    List<PackageOption> findByPackageId(Long packageId);
}

