package fr.keyser.evolution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Bean
	public CsrfTokenRepository csrfTokenRepository() {
		return new HttpSessionCsrfTokenRepository();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().csrfTokenRepository(csrfTokenRepository());
		http.authorizeRequests() //
				.antMatchers(HttpMethod.GET, "/auth/clients").permitAll() //
				.antMatchers("/logout").permitAll() //
				.anyRequest().authenticated();
		http.oauth2Login().loginPage("/auth/clients");
		http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")).logoutSuccessUrl("/");
		http.cors().configurationSource(permitAllCors());
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/**", "/js/**", "/fonts/**", "favicon.ico", "/", "/h2-console/**");
	}

	@Bean
	public CorsConfigurationSource permitAllCors() {
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedHeader(CorsConfiguration.ALL);
		config.addAllowedMethod(CorsConfiguration.ALL);
		config.addAllowedOrigin(CorsConfiguration.ALL);
		config.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}
