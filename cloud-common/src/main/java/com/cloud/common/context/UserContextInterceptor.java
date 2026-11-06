package com.cloud.common.context;

import com.cloud.common.constant.Constants;
import com.cloud.common.constant.MdcKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 从请求头注入用户 ID 到 {@link UserContext}，并写入 MDC；请求结束强制清理。
 */
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uidHeader = request.getHeader(Constants.HEADER_LOGIN_USER_ID);
        if (StringUtils.hasText(uidHeader)) {
            try {
                long uid = Long.parseLong(uidHeader.trim());
                UserContext.setUid(uid);
                MDC.put(MdcKeys.USER_ID, uidHeader.trim());
            } catch (NumberFormatException ignored) {
                UserContext.clear();
            }
        } else {
            UserContext.clear();
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
        MDC.remove(MdcKeys.USER_ID);
    }
}
