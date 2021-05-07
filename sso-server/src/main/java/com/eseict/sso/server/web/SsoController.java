package com.eseict.sso.server.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eseict.sso.server.domain.AccessToken;
import com.eseict.sso.server.domain.UserInfoResponse;
import com.eseict.sso.server.service.SsoService;

@Controller
public class SsoController {
	//
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SsoService ssoService;
	
	
//	public void addViewControllers(ViewControllerRegistry registry) {
//		//
//		registry.addViewController("/home").setViewName("home");
//		registry.addViewController("/loginForm").setViewName("loginForm");
//	}
//}
	
	@RequestMapping(value="/home")
	public String home(
			HttpServletRequest httpRequest
			, HttpServletRequest httpResponse) {
		log.info("home");
		return "home";
	}
	@RequestMapping(value="/loginForm")
	public String loginForm(
			HttpServletRequest httpRequest
			, HttpServletRequest httpResponse) {
		log.info("loginForm");
		return "login/index";
	}
	
	@RequestMapping(value="/userInfo", method=RequestMethod.POST)
	@ResponseBody
	public UserInfoResponse userInfo(@RequestParam(name="token") String token,
			@RequestParam(name="clientId") String clientId
			, HttpServletRequest httpRequest
			, HttpServletRequest httpResponse) {
		//
		log.debug("\n## in user(). token : {}, clientId : {}", token, clientId);
		AccessToken accessToken = ssoService.getAccessToken(token, clientId);
		
		log.debug("\n## accessToken : '{}'", accessToken);
		HttpSession session = httpRequest.getSession();
		UserInfoResponse response = new UserInfoResponse();
		if (accessToken == null) {
			//
			response.setResult(false);
			response.setMessage("사용자 정보를 조회할 수 없습니다.");
		}
		else {
			//
			response.setUserName(accessToken.getUserName());
		}
		
		return response;
	}
	
	@RequestMapping(value="/userLogout", method=RequestMethod.GET)
	public String userLogout(
			@RequestParam(name="clientId") String clientId,
			HttpServletRequest request) {
		//
		String userName = request.getRemoteUser();
		log.debug("\n## userLogout() : {}, {}, {}", clientId, userName);
		String baseUri = ssoService.logoutAllClients(clientId, userName);
		
		request.getSession().invalidate();
		
		return "redirect:" + baseUri;
	}
	
}
