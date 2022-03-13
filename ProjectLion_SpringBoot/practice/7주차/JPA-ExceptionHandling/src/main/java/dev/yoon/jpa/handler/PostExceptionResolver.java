package dev.yoon.jpa.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.yoon.jpa.exception.BaseException;
import dev.yoon.jpa.exception.ErrorResponseDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class PostExceptionResolver extends AbstractHandlerExceptionResolver {
    // 모든 예외가 들어옴
    @Override
    protected ModelAndView doResolveException(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {
        // return null: 핸들러가 에러를 처리하지 못함
        logger.debug(ex.getClass());
//        if(ex instanceof BaseException) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            try {
//                // json으로 작성 -> ObjectMapper
//                response.getOutputStream().print(
//                        new ObjectMapper().writeValueAsString(
//                                new ErrorResponseDto("in resolver, message: " + ex.getMessage())
//                        )
//                );
//                // 해당 형태를 추가해야함
//                response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
//                return new ModelAndView();
//            }catch (IOException e) {
//                logger.warn("Handling exception caused exception:{}",e);
//            }
//        }
        return null;
    }
}
