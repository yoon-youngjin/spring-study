package dev.yoon.refactoring_board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket restAPI() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("dev.yoon"))
                /**
                 * paths : 어떤 식으로 시작하는 api 를 보여줄것인지?
                 * any는 그냥 전부다. 만약, member/ 라고 설정하면 member로 시작하는 api 만 볼 수 있게 설정 가능하다.
                 */
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring Boot REST API")
                .version("1.0.0")
                .description("swagger api 입니다.")
                .build();
    }

}
