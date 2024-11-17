package com.sprk.commons.config;

import com.sprk.commons.lang.EnvProfile;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;



@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final EnvProfile envProfile;

	private final List<String> allowedOriginsProduction = List.of(
			"https://sprk.swapnilkhedekar.com"
//			"https://portal.sprktechnologies.in",
//			"https://sprktechnologies.in"
	);
	private final List<String> allowedOrigins = List.of(
			"*",
			"https://sprk.swapnilkhedekar.com",
//			"https://sprktechnologies.in",
//			"https://portal.sprktechnologies.in",
			"http://localhost:3000",
			"http://127.0.0.1:3000",
			"http://sprk.ddns.net:3000"
	);
	private final List<String> allowedHeaders = List.of(
			"Authorization",
			"Content-Type",
			"X-Requested-With",
			"Accept",
			"Origin",
			"Access-Control-Request-Method",
			"Access-Control-Request-Headers"
	);
	private final List<String> allowedExposedHeaders = List.of(
			"Access-Control-Allow-Origin",
			"Access-Control-Allow-Credentials",
			"Content-Disposition"
	);
	private final List<String> allowedMethods = List.of(
			"GET",
			"POST",
			"PUT",
			"PATCH",
			"DELETE",
			"OPTIONS"
	);



	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(envProfile.isProductionProfile() ? allowedOriginsProduction : allowedOrigins);
		configuration.setAllowedHeaders(allowedHeaders);
		configuration.setExposedHeaders(allowedExposedHeaders);
		configuration.setAllowedMethods(allowedMethods);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", configuration);
		source.registerCorsConfiguration("/certificate/**", configuration);
		return source;
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.headers(headers -> headers
						.frameOptions(frameOptions -> frameOptions.deny()) // Prevents iframe embedding
						.contentSecurityPolicy(csp -> csp.policyDirectives("frame-ancestors 'none'")) // CSP alternative
				)
                .build();
    }

    @Bean
    InMemoryUserDetailsManager userDetailsService() {
        return new InMemoryUserDetailsManager();
    }

}
