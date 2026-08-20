package com.smartspend.dashboard.projection;

import java.math.BigDecimal;

public interface CategoryStatisticProjection {

    Long getCategoryId();

    String getCategoryName();

    BigDecimal getAmount();
}