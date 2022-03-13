package dev.yoon.jpa.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class TransactionLogFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        String requestUUID = UUID.randomUUID().toString().split("-")[0];
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        log.debug("[{}] start request: {} {}",
                requestUUID,
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI());
        // chain의 dofilter를 호출하기 전까지가 요청이 Spring으로 넘어가기 전

        // chain전에 ContentCachingRequestWrapper
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpServletRequest);
        // dofilter 호출 전과 후의 차이 -> status가 변함: 전달된 response가 spring app내에서 돌아다니면서 status code가 변함
        chain.doFilter(request, response);

        // 이미 읽혀진 httpServletRequest의 inputStream의 결과 내용을 다시 한번 불러올 수 있음
        requestWrapper.getContentAsByteArray();



        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        log.debug("[{}], send response",
                requestUUID, httpServletResponse.getStatus());

    }
}
