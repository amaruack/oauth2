package com.eseict.sso.server.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * 
 * @author eseict
 *
 */
public class ConfFileInfo extends PropertyPlaceholderConfigurer {
	private static ConfFileInfo confFileInfo = null;
	private Properties mergedProperties = new Properties();

	public static ConfFileInfo getInstance(){
		return confFileInfo;
	}
	
	protected void loadProperties(Properties props){
		try {
			super.loadProperties(props);
			Object[] keySet =  props.keySet().toArray();
			for(Object key : keySet){
				String val = props.getProperty(key.toString());
				mergedProperties.setProperty(key.toString(), val);
			}
			ConfFileInfo.confFileInfo = this;
		} catch (IOException e) {
			//e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
	}
	
	public void reloadProperties() throws IOException {
		loadProperties(mergedProperties);
	}
	
	public static String getString(String key){
		if(ConfFileInfo.confFileInfo!= null) {
			return ConfFileInfo.confFileInfo.mergedProperties.getProperty(key);
		}
		return null;
	}
	
	public static String get(String key){
		return getString(key);
	}
	public static void clearProperties(){
        ConfFileInfo.confFileInfo.mergedProperties = new Properties(); 
    }
}
