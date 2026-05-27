package org.droneshow.package_service.service;

import org.droneshow.package_service.dto.*;
import org.droneshow.package_service.exception.ResourceNotFoundException;
import org.droneshow.package_service.model.Package;
import org.droneshow.package_service.model.PackageOption;
import org.droneshow.package_service.repository.PackageOptionRepository;
import org.droneshow.package_service.repository.PackageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PackageServiceImpl implements PackageService {

    private final PackageRepository packageRepository;
    private final PackageOptionRepository optionRepository;

    public PackageServiceImpl(PackageRepository packageRepository, PackageOptionRepository optionRepository) {
        this.packageRepository = packageRepository;
        this.optionRepository = optionRepository;
    }

    @Override
    public PackageResponse createPackage(CreatePackageRequest request) {
        Package pkg = Package.builder()
                .name(request.getName())
                .description(request.getDescription())
                .basePrice(request.getBasePrice())
                .durationMinutes(request.getDurationMinutes())
                .droneCount(request.getDroneCount())
                .isActive(true)
                .options(List.of())
                .build();

        pkg = packageRepository.save(pkg);
        return mapToResponse(pkg);
    }

    @Override
    public PackageResponse getPackageById(Long packageId) {
        Package pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found"));
        return mapToResponse(pkg);
    }

    @Override
    public PackageResponse updatePackage(Long packageId, CreatePackageRequest request) {
        Package pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found"));

        pkg.setName(request.getName());
        pkg.setDescription(request.getDescription());
        pkg.setBasePrice(request.getBasePrice());
        pkg.setDurationMinutes(request.getDurationMinutes());
        pkg.setDroneCount(request.getDroneCount());

        pkg = packageRepository.save(pkg);
        return mapToResponse(pkg);
    }

    @Override
    public void deletePackage(Long packageId) {
        Package pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found"));

        pkg.setIsActive(false);
        packageRepository.save(pkg);
    }

    @Override
    public PackageResponse.PackageOptionResponse addOptionToPackage(CreatePackageOptionRequest request) {
        // Verify package exists
        packageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new ResourceNotFoundException("Package not found"));

        PackageOption option = PackageOption.builder()
                .packageId(request.getPackageId())
                .name(request.getName())
                .description(request.getDescription())
                .extraPrice(request.getExtraPrice())
                .optionType(request.getOptionType())
                .build();

        option = optionRepository.save(option);

        return PackageResponse.PackageOptionResponse.builder()
                .id(option.getId())
                .name(option.getName())
                .description(option.getDescription())
                .extraPrice(option.getExtraPrice())
                .optionType(option.getOptionType())
                .build();
    }

    @Override
    public PriceCalculationResponse calculatePrice(PriceCalculationRequest request) {
        Package pkg = packageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new ResourceNotFoundException("Package not found"));

        BigDecimal basePrice = pkg.getBasePrice();
        BigDecimal optionsPrice = BigDecimal.ZERO;

        if (request.getOptionIds() != null && !request.getOptionIds().isEmpty()) {
            List<PackageOption> options = optionRepository.findAllById(request.getOptionIds());
            optionsPrice = options.stream()
                    .map(PackageOption::getExtraPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        BigDecimal totalPrice = basePrice.add(optionsPrice);

        return PriceCalculationResponse.builder()
                .basePrice(basePrice)
                .optionsPrice(optionsPrice)
                .totalPrice(totalPrice)
                .build();
    }

    private PackageResponse mapToResponse(Package pkg) {
        List<PackageOption> options = optionRepository.findByPackageId(pkg.getId());
        List<PackageResponse.PackageOptionResponse> optionResponses = options.stream()
                .map(o -> PackageResponse.PackageOptionResponse.builder()
                        .id(o.getId())
                        .name(o.getName())
                        .description(o.getDescription())
                        .extraPrice(o.getExtraPrice())
                        .optionType(o.getOptionType())
                        .build())
                .collect(Collectors.toList());

        return PackageResponse.builder()
                .id(pkg.getId())
                .name(pkg.getName())
                .description(pkg.getDescription())
                .basePrice(pkg.getBasePrice())
                .durationMinutes(pkg.getDurationMinutes())
                .droneCount(pkg.getDroneCount())
                .isActive(pkg.getIsActive())
                .options(optionResponses)
                .build();
    }
}

