package org.droneshow.package_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceCalculationRequest {

    private Long packageId;
    private List<Long> optionIds;
}

