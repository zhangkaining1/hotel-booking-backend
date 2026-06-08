package com.hotel.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 酒店状态枚举
 */
@Getter
@AllArgsConstructor
public enum HotelStatus {

    ACTIVE(1, "上架"),
    INACTIVE(0, "下架"),
    REVIEWING(2, "审核中"),
    DISABLED(3, "停用");

    private final int code;
    private final String description;
}
