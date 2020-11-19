package fr.keyser.evolution.web;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.keyser.security.AuthenticatedPlayer;
import fr.keyser.security.AuthenticatedPlayerConverter;
import fr.keyser.security.AuthorizedClients;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthenticatedPlayerConverter converter;

	private final AuthorizedClients authorizedClients;

	private final CsrfTokenRepository csrfTokenRepository;

	public AuthController(AuthenticatedPlayerConverter converter, AuthorizedClients authorizedClients,
			CsrfTokenRepository csrfTokenRepository) {
		this.converter = converter;
		this.authorizedClients = authorizedClients;
		this.csrfTokenRepository = csrfTokenRepository;
	}

	@GetMapping("/principal")
	public ResponseEntity<AuthenticatedPlayer> principal(HttpServletRequest request, Principal principal) {
		BodyBuilder status = ResponseEntity.status(HttpStatus.OK);
		status.headers(h -> {
			h.add("Access-Control-Expose-Headers", "X-CSRF-TOKEN");
			h.add("X-CSRF-TOKEN", csrfTokenRepository.loadToken(request).getToken());
		});
		return status.body(converter.convert(principal));
	}

	@GetMapping("/login")
	public ResponseEntity<AuthorizedClients> login() {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(authorizedClients);

	}

}
