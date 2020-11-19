package fr.keyser.security;

import java.util.Map;

public class AuthorizedClients {
	private final boolean form;

	private final String formLoginPage;

	private final Map<String, String> providers;

	public AuthorizedClients(boolean form, String formLoginPage, Map<String, String> providers) {
		this.form = form;
		this.formLoginPage = formLoginPage;
		this.providers = providers;
	}

	public boolean isForm() {
		return form;
	}

	public Map<String, String> getProviders() {
		return providers;
	}

	public String getFormLoginPage() {
		return formLoginPage;
	}
}
