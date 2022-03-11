package dev.yoon.jpa.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Enumeration;

@Component
@Slf4j
public class HeaderLoggingInterceptor implements HandlerInterceptor {

    // 들어온 요청에 대한 헤더를 조사
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // handler: 도달하기 위한 handler, 어떤 함수에 요청을 보낼 것인지에 대한 핸들러
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        log.info("start processing of {}", handlerMethod.getMethod().getName());
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.trace("{}: {}", headerName, request.getHeader(headerName));

        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            log.trace("{}: {}", headerName, response.getHeader(headerName));
        }


    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // ex: 실제로 응답이 돌아오기 전에 예외가 발생한 경우 예외가 자동 주입

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        log.info("end processing of {}", handlerMethod.getMethod().getName());

        if(ex != null) log.error("Exception occurred while processing");

    }
}
