package com.sybd.znld.web.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;

@EnableSwagger2
@Configuration
public class SwaggerConfig {
    @Value("${auth-server}")
    private String authServer;
    @Value("${security.oauth2.client.client-id}")
    private String clientId;
    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo()).select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(Collections.singletonList(securityScheme()))
                .securityContexts(Collections.singletonList(securityContext()));
    }

    @Bean
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("智能路灯平台APIs").version("1.0.0").build();
    }

    @Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder()
                /*.clientId(clientId)
                .clientSecret(clientSecret)*/
                .scopeSeparator(" ")
                .build();
    }

    private SecurityScheme securityScheme() {
        var grantType = new ResourceOwnerPasswordCredentialsGrant(authServer+ "/com/sybd/model/znld/oauth/token");
        return new OAuthBuilder().name("spring_oauth").grantTypes(Collections.singletonList(grantType)).scopes(Arrays.asList(scopes())).build();
    }

    private AuthorizationScope[] scopes() {
        return new AuthorizationScope[]{new AuthorizationScope("read", "for read operations"),
                new AuthorizationScope("write", "for write operations"),
                new AuthorizationScope("execute", "for execute operations")};
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(Collections.singletonList(new SecurityReference("spring_oauth", scopes())))
                .forPaths(PathSelectors.regex("(?!/api/v1/user/login).*"))
                .build();
    }
}
