package org.droneshow.package_service.config;

import org.droneshow.package_service.model.Package;
import org.droneshow.package_service.repository.PackageRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class PackageDataInitializer {

    @Bean
    CommandLineRunner initializePackages(PackageRepository packageRepository) {

        return args -> {

            createIfMissing(
                    packageRepository,
                    "silver",
                    "Silver drone show package",
                    BigDecimal.valueOf(1500),
                    10,
                    80
            );

            createIfMissing(
                    packageRepository,
                    "gold",
                    "Gold drone show package",
                    BigDecimal.valueOf(2800),
                    20,
                    150
            );

            createIfMissing(
                    packageRepository,
                    "platinum",
                    "Platinum drone show package",
                    BigDecimal.valueOf(4900),
                    25,
                    300
            );
        };
    }

    private void createIfMissing(
            PackageRepository repository,
            String name,
            String description,
            BigDecimal price,
            Integer durationMinutes,
            Integer droneCount) {

        if (repository.findByNameIgnoreCase(name).isEmpty()) {

            Package pkg = Package.builder()
                    .name(name)
                    .description(description)
                    .basePrice(price)
                    .durationMinutes(durationMinutes)
                    .droneCount(droneCount)
                    .isActive(true)
                    .options(List.of())
                    .build();

            repository.save(pkg);
        }
    }
}
