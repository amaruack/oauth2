package com.eseict.sso.server.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
	protected final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

	private Gson gson = new GsonBuilder().create();
	
	private String defaultFailureUrl;
	private boolean forwardToDestination = false;
	private boolean allowSessionCreation = true;
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	public CustomAuthenticationFailureHandler() {
	}

	public CustomAuthenticationFailureHandler(String defaultFailureUrl) {
		setDefaultFailureUrl(defaultFailureUrl);
	}

	/**
	 * Performs the redirect or forward to the {@code defaultFailureUrl} if set,
	 * otherwise returns a 401 error code.
	 * <p>
	 * If redirecting or forwarding, {@code saveException} will be called to cache
	 * the exception for use in the target view.
	 */
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		logger.debug("No failure URL set, sending 401 Unauthorized error");

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		
		JsonObject responseObject = new JsonObject();
		if (exception.getClass().isAssignableFrom(UsernameNotFoundException.class)) {
			responseObject.addProperty("responseCode", "4010");
			responseObject.addProperty("responseMessage", "Not matched Login ID");
		} else if (exception.getClass().isAssignableFrom(BadCredentialsException.class)) {
			responseObject.addProperty("responseCode", "4011");
			responseObject.addProperty("responseMessage", "Not matched Login Password");
		} else {
			responseObject.addProperty("responseCode", "4019");
			responseObject.addProperty("responseMessage", "unauthorized");
		}
		
		PrintWriter writer = response.getWriter();
		writer.append(gson.toJson(responseObject));
		
	}

	/**
	 * Caches the {@code AuthenticationException} for use in view rendering.
	 * <p>
	 * If {@code forwardToDestination} is set to true, request scope will be used,
	 * otherwise it will attempt to store the exception in the session. If there is
	 * no session and {@code allowSessionCreation} is {@code true} a session will be
	 * created. Otherwise the exception will not be stored.
	 */
	protected final void saveException(HttpServletRequest request, AuthenticationException exception) {
		if (forwardToDestination) {
			request.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception);
		} else {
			HttpSession session = request.getSession(false);

			if (session != null || allowSessionCreation) {
				request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception);
			}
		}
	}

	/**
	 * The URL which will be used as the failure destination.
	 *
	 * @param defaultFailureUrl
	 *            the failure URL, for example "/loginFailed.jsp".
	 */
	public void setDefaultFailureUrl(String defaultFailureUrl) {
		Assert.isTrue(UrlUtils.isValidRedirectUrl(defaultFailureUrl),
				"'" + defaultFailureUrl + "' is not a valid redirect URL");
		this.defaultFailureUrl = defaultFailureUrl;
	}

	protected boolean isUseForward() {
		return forwardToDestination;
	}

	/**
	 * If set to <tt>true</tt>, performs a forward to the failure destination URL
	 * instead of a redirect. Defaults to <tt>false</tt>.
	 */
	public void setUseForward(boolean forwardToDestination) {
		this.forwardToDestination = forwardToDestination;
	}

	/**
	 * Allows overriding of the behaviour when redirecting to a target URL.
	 */
	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}

	protected RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}

	protected boolean isAllowSessionCreation() {
		return allowSessionCreation;
	}

	public void setAllowSessionCreation(boolean allowSessionCreation) {
		this.allowSessionCreation = allowSessionCreation;
	}
}
