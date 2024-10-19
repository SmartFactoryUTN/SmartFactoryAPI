package com.example.smartfactory.Security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
class SecurityConfig {

    @Value("\${auth0.audience}")
    private lateinit var audience: String

    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private lateinit var issuer: String

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuer) as NimbusJwtDecoder
        val audienceValidator: OAuth2TokenValidator<Jwt> = AudienceValidator(audience)
        val withIssuer: OAuth2TokenValidator<Jwt> = JwtValidators.createDefaultWithIssuer(issuer)
        val withAudience: OAuth2TokenValidator<Jwt> = DelegatingOAuth2TokenValidator(withIssuer, audienceValidator)
        jwtDecoder.setJwtValidator(withAudience)
        return jwtDecoder
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        AntPathRequestMatcher("/api/login"),
                        AntPathRequestMatcher("/api/logout"),
                        AntPathRequestMatcher("/api/signup"),
                        AntPathRequestMatcher("/swagger-ui/*"),
                        AntPathRequestMatcher("/v3/api-docs"),
                        AntPathRequestMatcher("/v3/api-docs/*")
                    ).permitAll()
                    .requestMatchers(
                        AntPathRequestMatcher("/api/tizada"),
                        AntPathRequestMatcher("/api/tizada/*"),
                        AntPathRequestMatcher("/api/molde"),
                        AntPathRequestMatcher("/api/molde/*")
                    ).authenticated()
            }
            .cors(withDefaults())
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt(withDefaults())
            }
        return http.build()
    }

}
