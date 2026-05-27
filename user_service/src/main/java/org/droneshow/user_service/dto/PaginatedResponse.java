package org.droneshow.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long total;
    private int pages;
    private boolean last;
    private boolean first;
}

