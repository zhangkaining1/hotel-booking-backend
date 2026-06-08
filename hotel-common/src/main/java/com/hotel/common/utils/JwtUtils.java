package com.hotel.common.utils;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSignerUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtUtils {

    private static final byte[] KEY = "hotel_booking_secret_key_2026".getBytes();

    public static String createToken(Long userId, String role) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("role", role);
        // expire after 24 hours
        payload.put("expire_time", System.currentTimeMillis() + 1000 * 60 * 60 * 24);

        return JWTUtil.createToken(payload, KEY);
    }

    public static boolean verify(String token) {
        try {
            boolean verifyKey = JWTUtil.verify(token, KEY);
            if (!verifyKey) return false;
            
            JWT jwt = JWTUtil.parseToken(token);
            Long expireTime = Long.parseLong(jwt.getPayload("expire_time").toString());
            return System.currentTimeMillis() <= expireTime;
        } catch (Exception e) {
            return false;
        }
    }

    public static Long getUserId(String token) {
        try {
            JWT jwt = JWTUtil.parseToken(token);
            return Long.parseLong(jwt.getPayload("userId").toString());
        } catch (Exception e) {
            return null;
        }
    }

    public static String getRole(String token) {
        try {
            JWT jwt = JWTUtil.parseToken(token);
            return jwt.getPayload("role").toString();
        } catch (Exception e) {
            return null;
        }
    }
}
