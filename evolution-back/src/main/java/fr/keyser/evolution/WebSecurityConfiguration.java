package fr.keyser.evolution;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import fr.keyser.security.AuthorizedClients;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	private static final String LOGIN_PAGE = "/auth/login";
	private static final String LOGINPOST_PAGE = "/auth/loginpost";
	private final Iterable<ClientRegistration> clientRegistrations;

	public WebSecurityConfiguration(@Autowired(required = false) Iterable<ClientRegistration> clientRegistrations) {
		this.clientRegistrations = Optional.ofNullable(clientRegistrations).orElseGet(Collections::emptyList);
	}

	@Bean
	public AuthorizedClients authorizedClients() {
		Map<String, String> out = new LinkedHashMap<>();
		clientRegistrations.forEach(c -> out.put(c.getClientName(), "oauth2/authorization/" + c.getRegistrationId()));
		return new AuthorizedClients(out.isEmpty(), LOGINPOST_PAGE, out);
	}

	@Bean
	public CsrfTokenRepository csrfTokenRepository() {
		return new HttpSessionCsrfTokenRepository();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		AuthorizedClients ac = authorizedClients();
		if (ac.isForm()) {
			PasswordEncoder encoder = passwordEncoder();
			auth.userDetailsService(username -> {
				return User.withUsername(username).password(encoder.encode("evolution")).authorities("ROLE_USER")
						.build();
			});
		}
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(4);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().csrfTokenRepository(csrfTokenRepository());
		http.authorizeRequests() //
				.antMatchers(LOGIN_PAGE).permitAll() //
				.antMatchers("/logout").permitAll() //
				.anyRequest().authenticated();

		AuthorizedClients ac = authorizedClients();
		if (ac.isForm()) {
			http.csrf().ignoringAntMatchers(LOGINPOST_PAGE);
			
			http.formLogin()
					.loginPage(LOGIN_PAGE)
					.loginProcessingUrl(LOGINPOST_PAGE)					
					.defaultSuccessUrl("/auth/principal", true);
		} else {
			
			http.oauth2Login()
					.loginPage(LOGIN_PAGE);
		}
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
