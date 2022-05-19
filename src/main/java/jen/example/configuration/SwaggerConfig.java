package jen.example.configuration;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("placeMe-public")
                .pathsToMatch("/pupils/**")
                .pathsToMatch("/templates/**")
                .pathsToMatch("/**")
                .build();
    }

//    @Bean
//    public GroupedOpenApi adminApi() {
//        return GroupedOpenApi.builder()
//                .group("springshop-admin")
//                .pathsToMatch("/admin/**")
//                //.addMethodFilter(method -> method.isAnnotationPresent(KafkaProperties.Admin.class))
//                .build();
//    }

}
