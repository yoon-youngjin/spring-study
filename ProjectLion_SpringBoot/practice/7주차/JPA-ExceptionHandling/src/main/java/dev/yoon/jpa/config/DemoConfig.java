package dev.yoon.jpa.config;

import com.google.gson.Gson;
import dev.yoon.jpa.interceptor.HeaderLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
// @Configuration은 Spring boot에서 제공하지 않고, 자신이 만들지 않은 라이브러리 클래스를 bean으로 제공하여
// 설정에 필요한 클래스들을 객체화하여 ioc 컨테이너에 전달하는 역할
public class DemoConfig implements WebMvcConfigurer {

    private final HeaderLoggingInterceptor headerLoggingInterceptor;

    // interceptor 등록
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(headerLoggingInterceptor)
                .addPathPatterns("/post/**")
                .excludePathPatterns("/except/**");

    }

    //    @PostConstruct
//    public void init() {
//        log.info("custom property: {}", customProperty);
//
//        for (String commaItem : customCommaList) {
//            log.info("comma list item:{}", commaItem);
//        }
//
//        log.info(propertyDefault);
//    }
//
//    // 값을 넣어줄 수도 있다. -> 상황에 따라서 설정 내용을 바꿀 수 있다.
//    @Value("${custom.property.default:default-property}")
//    private String propertyDefault;
//
//    @Value("${custom.property.single}")
//    // yml파일의 해당 주소로 찾아가서 변수에 값을 주입
//    // bean이 생성되고 나서 값 들어감
//    private String customProperty;
//
//    @Value("${custom.property.comlist}")
//    private List<String> customCommaList;
//
//    @Bean
//    // 함수의 결과인 Gson이 ioc컨테이너의 관리에 들어감
//    // => @Autowired를 통해 gson을 받아올 수 있는 상태가됨
//    public Gson gson() {
//        return new Gson();
//    }
}
