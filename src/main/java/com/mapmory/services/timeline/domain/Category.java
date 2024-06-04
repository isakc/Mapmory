package com.mapmory.services.timeline.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Category {
    private int categoryNo;
    private String categoryName;
    private String categoryImoji;
}
