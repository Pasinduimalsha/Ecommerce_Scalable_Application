package com.sysco.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewStatusDTO {

    private Long productId;

    private String status;

    @Size(max = 500, message = "Review comment cannot exceed 500 characters")
    private String reviewComment;

    private String reviewedBy;
}