package com.smartspend.dashboard.dto.response;

import java.math.BigDecimal;

public record CategoryStatisticResponse(

        Long categoryId,

        String categoryName,

        BigDecimal amount,

        BigDecimal percentage

) {
}