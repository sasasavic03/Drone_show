package org.droneshow.package_service.service;

import org.droneshow.package_service.dto.*;

import java.util.List;

public interface PackageService {

    PackageResponse createPackage(CreatePackageRequest request);

    PackageResponse getPackageById(Long packageId);

    // List all packages (active and inactive) - used by frontend
    List<PackageResponse> listPackages();

    PackageResponse updatePackage(Long packageId, CreatePackageRequest request);

    void deletePackage(Long packageId);

    PackageResponse.PackageOptionResponse addOptionToPackage(CreatePackageOptionRequest request);

    PriceCalculationResponse calculatePrice(PriceCalculationRequest request);
}

