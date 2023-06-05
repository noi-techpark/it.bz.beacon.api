// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api;

import com.google.common.collect.Lists;
import it.bz.beacon.api.config.ApiInfoConfiguration;
import it.bz.beacon.api.config.BeaconSuedtirolConfiguration;
import it.bz.beacon.api.config.KontaktIOConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
@ComponentScan("it.bz.beacon.api")
@EnableJpaAuditing
@EnableScheduling
@EnableTransactionManagement
public class Application extends SpringBootServletInitializer {

    @Autowired
    private KontaktIOConfiguration kontaktIOConfiguration;

    @Autowired
    private BeaconSuedtirolConfiguration beaconSuedtirolConfiguration;

    @Autowired
    private ApiInfoConfiguration apiInfoConfiguration;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .host(apiInfoConfiguration.getHost())
                .select()
                .apis(RequestHandlerSelectors.basePackage("it.bz.beacon.api.controller"))
                .build()
                .apiInfo(apiInfo())
                .securityContexts(Lists.newArrayList(securityContext()))
                .securitySchemes(Lists.newArrayList(bearerApiKey(), basicApiKey()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                apiInfoConfiguration.getTitle(),
                apiInfoConfiguration.getDescription(),
                apiInfoConfiguration.getVersion(),
                apiInfoConfiguration.getTermsOfServiceUrl(),
                new Contact(apiInfoConfiguration.getContactName(), apiInfoConfiguration.getContactUrl(), apiInfoConfiguration.getContactEmail()),
                apiInfoConfiguration.getLicense(),
                apiInfoConfiguration.getLicenseUrl(),
                Collections.emptyList()
        );
    }

    private ApiKey bearerApiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }

    private BasicAuth basicApiKey() {
        return new BasicAuth("TrustedAuth");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/v1/admin/*"))
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(new SecurityReference("JWT", authorizationScopes));
    }

    @Bean
    public RestTemplate getRestClient(RestTemplateBuilder builder) {
        return builder
//                .additionalInterceptors(Lists.newArrayList(new LoggingRequestInterceptor()))
                .rootUri(kontaktIOConfiguration.getApiUrl())
                .build();
    }

    @Bean
    public HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", kontaktIOConfiguration.getAcceptHeader());
        headers.set("Api-Key", kontaktIOConfiguration.getApiKey());

        return headers;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/v1/**")
                        .allowedOrigins(beaconSuedtirolConfiguration.getAllowedOrigins().split(","))
                        .allowedMethods(
                                HttpMethod.GET.toString(),
                                HttpMethod.POST.toString(),
                                HttpMethod.PATCH.toString(),
                                HttpMethod.DELETE.toString(),
                                HttpMethod.PUT.toString(),
                                HttpMethod.OPTIONS.toString(),
                                HttpMethod.TRACE.toString(),
                                HttpMethod.HEAD.toString()
                        );
            }
        };
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {

            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
                Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);
                errorAttributes.remove("trace");
                return errorAttributes;
            }

        };
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
}
