package com.leih.shopping.common.util;

//import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Get User info
 */
public class AuthContextHolder {

    /**
     * get current user id
     * @param request
     * @return
     */
    public static String getUserId(HttpServletRequest request) {
        String userId = request.getHeader("userId");
        return StringUtils.isEmpty(userId) ? "" : userId;
    }

    /**
     * 获取当前未登录临时用户id
     * @param request
     * @return
     */
    public static String getUserTempId(HttpServletRequest request) {
        String userTempId = request.getHeader("userTempId");
        return StringUtils.isEmpty(userTempId) ? "" : userTempId;
    }
}
