package com.acompany.asystem.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.acompany.asystem.domain.Response;
import com.acompany.asystem.domain.TokenRequestResult;
import com.acompany.asystem.domain.User;
import com.acompany.asystem.domain.UserInfoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OAuthServiceImpl implements OAuthService {
	//
	private static final Logger log = LoggerFactory.getLogger(OAuthServiceImpl.class);
	
	@Autowired
	private UserService userService;
	
	@Value("${systemName}")
	private String systemName;
	
	@Value("${server.port}")
	private int serverPort;
	
	private String authorizationRequestHeader;
	
	private String getOAuthClientId() {
		//
		return systemName + "_id";
	}
	
	private String getOAuthClientSecret() {
		//
		return systemName + "_secret";
	}
	
	@Override
	public TokenRequestResult requestAccessTokenToAuthServer(String code, HttpServletRequest request) {
		//
		TokenRequestResult tokenRequestResult = requestAccessTokenToAuthServer(code);
		log.debug("\n## tokenResult : '{}'\n", tokenRequestResult);
		
		if (tokenRequestResult.getError() != null) {
			//
			return tokenRequestResult;
		}
		
		UserInfoResponse userInfoResponse = requestUserInfoToAuthServer(tokenRequestResult.getAccessToken());
		if (userInfoResponse.getResult() == false) {
			//
			tokenRequestResult.setError(userInfoResponse.getMessage());
			return tokenRequestResult;
		}
		User user = userService.getUser(userInfoResponse.getUserName());
		request.getSession().setAttribute("user", user);
		String userName = userInfoResponse.getUserName();
		
		userService.updateTokenId(userName, extractTokenId(tokenRequestResult.getAccessToken()));
		
		return tokenRequestResult;
	}
	
	@Override
	public Response logout(String tokenId, String userName) {
		//
		Response response = new Response();
		
		log.debug("\n## logout {}", userName);
		User user = userService.getUser(userName);
		if (user == null || user.getTokenId() == null) {
			//
			return response;
		}
		
		String savedTokenId = user.getTokenId();
		log.debug("\n## in logout savedTokenId, tokenId : '{}', '{}'", savedTokenId, tokenId);
		if (tokenId.equals(savedTokenId) == false) {
			//
			return response;
		}
		userService.updateTokenId(userName, null);
		
		return response;
	}
	private TokenRequestResult requestAccessTokenToAuthServer(String code) {
		//
		String reqUrl = "http://10.10.1.214:22222/oauth/token";
		String authorizationHeader = getAuthorizationRequestHeader();
		
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("grant_type", "authorization_code");
		paramMap.put("redirect_uri", getOAuthRedirectUri());
		paramMap.put("code", code);
		
		HttpPost post = buildHttpPost(reqUrl, paramMap, authorizationHeader);
		
		TokenRequestResult result = executePostAndParseResult(post, TokenRequestResult.class);
			
		return result;
	}
	
	private String getOAuthRedirectUri() {
		//
		return String.format("http://10.10.0.198:%d/oauthCallback", serverPort);
	}
	
	private UserInfoResponse requestUserInfoToAuthServer(String token) {
		//
		String reqUrl = "http://10.10.1.214:22222/userInfo";
//		String authorizationHeader = getAuthorizationRequestHeader();
		String authorizationHeader = null;
		
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("token", token);
		paramMap.put("clientId", getOAuthClientId());
		
		HttpPost post = buildHttpPost(reqUrl, paramMap, authorizationHeader);
		
		UserInfoResponse result = executePostAndParseResult(post, UserInfoResponse.class);
		
		return result;
	}
	
	private <T> T executePostAndParseResult(HttpPost post, Class<T> clazz) {
		//
		T result = null;
		try {
			HttpClient client = HttpClientBuilder.create().build();
			
			HttpResponse response = client.execute(post);
			BufferedReader rd = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));
	
			StringBuffer resultBuffer = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				resultBuffer.append(line);
			}
			log.debug("\n## response body : '{}'", resultBuffer.toString());
			
			ObjectMapper mapper = new ObjectMapper();
			result = mapper.readValue(resultBuffer.toString(), clazz);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		
		return result;
	}

	private HttpPost buildHttpPost(String reqUrl, Map<String, String> paramMap, String authorizationHeader) {
		//
		log.debug("\n## in buildHttpPost() reqUrl : {}", reqUrl);
		HttpPost post = new HttpPost(reqUrl);
		if (authorizationHeader != null) {
			//
			post.addHeader("Authorization", authorizationHeader);
		}
		
		List<NameValuePair> urlParameters = new ArrayList<>();
		for (Map.Entry<String, String> entry : paramMap.entrySet()) {
			urlParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		
		try {
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
		}
		return post;
	}
	
	private String getAuthorizationRequestHeader() {
		//
		if (authorizationRequestHeader == null) {
			//
			setAuthroizationRequestHeader();
		}

		return authorizationRequestHeader;
	}
	
	private void setAuthroizationRequestHeader() {
		//
		Encoder encoder = Base64.getEncoder();

		try {
			String toEncodeString = String.format("%s:%s", getOAuthClientId(), getOAuthClientSecret());
			authorizationRequestHeader = "Basic " + encoder.encodeToString(toEncodeString.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	private String extractTokenId(String value) {
		//
		if (value == null) {
			//
			return null;
		}
		
		try {
			//
			MessageDigest digest = MessageDigest.getInstance("MD5");
			
			byte[] bytes = digest.digest(value.getBytes("UTF-8"));
			return String.format("%032x", new BigInteger(1, bytes));
		}
		catch (NoSuchAlgorithmException e) {
			//
			throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
		}
		catch (UnsupportedEncodingException e) {
			//
			throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
		}
	}
}
