package org.droneshow.package_service.controller;

import jakarta.validation.Valid;
import org.droneshow.package_service.dto.*;
import org.droneshow.package_service.service.PackageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/packages")
public class PackageController {

    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PackageResponse>> createPackage(
            @Valid @RequestBody CreatePackageRequest request) {

        PackageResponse response = packageService.createPackage(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Package created", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PackageResponse>> getPackageById(
            @PathVariable Long id) {

        PackageResponse response = packageService.getPackageById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Package retrieved", response)
        );
    }

    // NEW
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<PackageResponse>> getPackageByName(
            @PathVariable String name) {

        PackageResponse response = packageService.getPackageByName(name);

        return ResponseEntity.ok(
                ApiResponse.success("Package retrieved", response)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PackageResponse>> updatePackage(
            @PathVariable Long id,
            @Valid @RequestBody CreatePackageRequest request) {

        PackageResponse response = packageService.updatePackage(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Package updated", response)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePackage(
            @PathVariable Long id) {

        packageService.deletePackage(id);

        return ResponseEntity.ok(
                ApiResponse.success("Package deleted")
        );
    }

    @PostMapping("/options")
    public ResponseEntity<ApiResponse<PackageResponse.PackageOptionResponse>> addOption(
            @Valid @RequestBody CreatePackageOptionRequest request) {

        PackageResponse.PackageOptionResponse response =
                packageService.addOptionToPackage(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Option added", response));
    }

    @PostMapping("/calculate-price")
    public ResponseEntity<ApiResponse<PriceCalculationResponse>> calculatePrice(
            @Valid @RequestBody PriceCalculationRequest request) {

        PriceCalculationResponse response =
                packageService.calculatePrice(request);

        return ResponseEntity.ok(
                ApiResponse.success("Price calculated", response)
        );
    }
}
