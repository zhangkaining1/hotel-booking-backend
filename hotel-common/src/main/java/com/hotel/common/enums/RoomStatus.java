package com.hotel.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 房间状态枚举
 */
@Getter
@AllArgsConstructor
public enum RoomStatus {

    AVAILABLE(0, "空闲"),
    RESERVED(1, "已预订"),
    OCCUPIED(2, "已入住"),
    MAINTENANCE(3, "维修"),
    DISABLED(4, "停用");

    private final int code;
    private final String description;
}
