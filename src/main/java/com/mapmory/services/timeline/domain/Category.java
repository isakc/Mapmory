package com.mapmory.services.timeline.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private int categoryNo;
    private String categoryName;
    private String categoryImoji;

    private String categoryImojiByte;
}
