package org.droneshow.package_service.service;

import org.droneshow.package_service.dto.*;

public interface PackageService {

    PackageResponse createPackage(CreatePackageRequest request);

    PackageResponse getPackageById(Long packageId);

    PackageResponse updatePackage(Long packageId, CreatePackageRequest request);

    void deletePackage(Long packageId);

    PackageResponse.PackageOptionResponse addOptionToPackage(CreatePackageOptionRequest request);

    PriceCalculationResponse calculatePrice(PriceCalculationRequest request);
}

