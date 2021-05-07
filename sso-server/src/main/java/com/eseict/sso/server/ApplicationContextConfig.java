package com.eseict.sso.server;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.google.common.base.Strings;
import com.eseict.sso.server.util.ConfFileInfo;

/**
 * root-context.xml 대치용 java config 파일
 * @author eseict
 *
 */
@Configuration
public class ApplicationContextConfig {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationContextConfig.class);
	private static final String CONFIG_FILE_NAME = "system.properties";

	/**
	 * Propoerty 로딩 클래스 빈 생성 설정<br>
	 * @return
	 */
	@Bean
	public PropertyPlaceholderConfigurer confFileInfo(){
		PropertyPlaceholderConfigurer config = new ConfFileInfo();
		Resource location = null;
        String homePath = System.getenv("AUTH_HOME");
        if(Strings.isNullOrEmpty(homePath)){
        	logger.error("Can not find AUTH_HOME");
        } else {
            StringBuilder sb = new StringBuilder(homePath);
            sb.append(File.separatorChar);
            sb.append(ApplicationContextConfig.CONFIG_FILE_NAME);
            location = new FileSystemResource(sb.toString());	//디플로이시 HOME 패스에서 컨피그 로딩
        }
		config.setLocation(location);
		return config;
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
