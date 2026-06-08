package com.hotel.common.constants;

/**
 * 应用常量
 */
public final class AppConstants {

    private AppConstants() {
    }

    /** 订单支付超时时间（分钟） */
    public static final int ORDER_PAY_TIMEOUT_MINUTES = 30;

    /** 默认分页大小 */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /** 最大分页大小 */
    public static final int MAX_PAGE_SIZE = 100;

    /** 默认地图搜索半径（米） */
    public static final int DEFAULT_MAP_SEARCH_RADIUS = 5000;

    /** 文件上传最大大小（MB） */
    public static final int MAX_FILE_SIZE_MB = 10;

    /** 验证码有效期（分钟） */
    public static final int VERIFY_CODE_EXPIRE_MINUTES = 5;

    /** Token 名称 */
    public static final String TOKEN_NAME = "Authorization";

    /** 用户端 Token 前缀 */
    public static final String USER_TOKEN_PREFIX = "user:";

    /** 管理端 Token 前缀 */
    public static final String ADMIN_TOKEN_PREFIX = "admin:";
}
