package com.eseict.sso.server.service;

import com.eseict.sso.server.domain.AccessToken;

public interface SsoService {
	//
	AccessToken getAccessToken(String token, String clientId);
	
	String logoutAllClients(String clientId, String userName);
}
