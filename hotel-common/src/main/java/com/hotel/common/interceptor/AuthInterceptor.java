package com.hotel.common.interceptor;

import com.hotel.common.exception.BusinessException;
import com.hotel.common.result.ResultCode;
import com.hotel.common.utils.JwtUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true; // 放行跨域预检请求
        }

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token == null || !JwtUtils.verify(token)) {
            // response.setStatus(401);
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        Long userId = JwtUtils.getUserId(token);
        String role = JwtUtils.getRole(token);
        
        // 存入 request 方便后续获取
        request.setAttribute("userId", userId);
        request.setAttribute("role", role);

        return true;
    }
}
