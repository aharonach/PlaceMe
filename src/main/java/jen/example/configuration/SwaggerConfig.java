package jen.example.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI().info(new Info()
                        .title("PlaceMe API")
                        .description("PlaceMe Documentation")
                        .version("v0.0.1")
                );
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("placeMe-public")
                .pathsToMatch("/pupils/**")
                .pathsToMatch("/templates/**")
                .pathsToMatch("/groups/**")
                .pathsToMatch("/**")
                .build();
    }

//    @Bean
//    public GroupedOpenApi adminApi() {
//        return GroupedOpenApi.builder()
//                .group("placeMe-admin")
//                .pathsToMatch("/admin/**")
//                .build();
//    }

}
