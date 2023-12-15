package io.nuvalence.web.portal.service.config;

import io.nuvalence.auth.token.TokenFilter;
import io.nuvalence.auth.token.TokenSignOutService;
import io.nuvalence.auth.token.firebase.FirebaseAuthenticationProvider;
import io.nuvalence.auth.token.firebase.FirebaseTokenSignOutHandler;
import io.nuvalence.logging.filter.LoggingContextFilter;
import io.nuvalence.web.portal.service.utils.JacocoIgnoreInGeneratedReport;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Configures TokenFilter and TokenSignOutService.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Profile("!test")
@JacocoIgnoreInGeneratedReport(
        reason =
                "Initialization has side effects making unit tests difficult. Tested in acceptance"
                        + " tests.")
public class WebSecurityConfig {
    @Value("${spring.cloud.gcp.project-id}")
    private String gcpProjectId;

    @Value("${management.endpoints.web.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Value("${management.endpoints.web.cors.allowed-methods}")
    private List<String> allowedMethods;

    @Value("${management.endpoints.web.cors.allowed-headers}")
    private List<String> allowedHeaders;

    @Value("${management.endpoints.web.cors.allow-credentials}")
    private boolean allowCredentials;

    /**
     * Allows unauthenticated access to API docs.
     *
     * @param http Spring HttpSecurity configuration.
     * @return Configured SecurityFilterChain
     * @throws Exception If any erroes occur during configuration
     */
    @Bean
    @Order(0)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.cors()
                .and()
                .csrf(csrf -> csrf.disable())
                .requestCache()
                .requestCache(new NullRequestCache())
                .and()
                .securityContext()
                .disable()
                .sessionManagement(
                        sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(
                                                "/",
                                                "/swagger-ui.html",
                                                "/swagger-ui/**",
                                                "/v3/api-docs/**",
                                                "/actuator/health")
                                        .permitAll().anyRequest().authenticated())
                .addFilterAfter(new LoggingContextFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(
                        new TokenFilter(new FirebaseAuthenticationProvider(gcpProjectId)),
                        LoggingContextFilter.class)
                .build();
    }

    /**
     * Provides configurer that sets up CORS.
     *
     * @return a configured configurer
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(allowedOrigins.toArray(String[]::new))
                        .allowedHeaders(allowedHeaders.toArray(String[]::new))
                        .allowedMethods(allowedMethods.toArray(String[]::new))
                        .allowCredentials(allowCredentials);
            }

        };
    }

    /**
     * Sets up the Token Sign Out Service.
     *
     * @return Configured TokenSignOutService
     */
    @Bean
    public TokenSignOutService getTokenSignOutService() {
        return new TokenSignOutService(new FirebaseTokenSignOutHandler(gcpProjectId));
    }
}
