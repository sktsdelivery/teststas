//package com.smartKrow;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//    
//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.smartKrow"))
//                .paths(PathSelectors.any())
//                .build();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests(requests -> requests
//                .requestMatchers("/v2/api-docs", "/swagger-resources", "/swagger-resources/**", 
//                                 "/configuration/ui", "/configuration/security", "/swagger-ui.html", 
//                                 "/webjars/**", "/swagger-ui/**", "/v1/entity/**")
//                .permitAll()
//                .anyRequest().authenticated());
//        
//        return http.build();
//    }
//    
//}
