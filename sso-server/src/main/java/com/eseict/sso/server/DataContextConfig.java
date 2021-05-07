package com.eseict.sso.server;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.cfg.Environment;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

import com.eseict.sso.server.util.ConfFileInfo;

/*****************************************************
 * spring-persistence.xml (db 접근 관련) 파일을 대체하는 설정
 * @pkg name 	com.eseict.oms.config
 * @file name 	DataContextConfig.java
 * @date		2016. 6. 8.
 * @author 		AhnCheolhwan
 *****************************************************/

/*******************************************************
 * 
 * TODO : Must remove Comment slash front of Annotations to use Database and
 * Hibernate ORM
 *
 */

@Configuration
@EnableAsync // @Async 어노테이션을 사용하기 위함
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableJpaRepositories(basePackages = "com.nextree", considerNestedRepositories = true)
@EnableAutoConfiguration
public class DataContextConfig {

	static final Logger logger = LoggerFactory.getLogger(DataContextConfig.class);

	/*****************************************************
	 * data source 설정
	 * 
	 * @date 2016. 6. 8.
	 * @Author AhnCheolhwan
	 *****************************************************/
	@Bean(name = "dataSource")
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(ConfFileInfo.get("db.driver"));
		dataSource.setUrl(ConfFileInfo.get("db.url"));
		dataSource.setUsername(ConfFileInfo.get("db.username"));
		dataSource.setPassword(ConfFileInfo.get("db.password"));
		return dataSource;
	}

	@Autowired
	@Bean("transactionManager")
	public JpaTransactionManager jpaTransactionManager(
			LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactoryBean.getObject());
		return transactionManager;
	}

	@Bean("entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		HibernateJpaVendorAdapter jpsAdapter = new HibernateJpaVendorAdapter();
		jpsAdapter.setShowSql(false);
		entityManagerFactoryBean.setJpaVendorAdapter(jpsAdapter);
		entityManagerFactoryBean.setDataSource(dataSource());
		entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		entityManagerFactoryBean.setPackagesToScan("com.nextree");
		entityManagerFactoryBean.setJpaProperties(jpaHibernateProperties());
		return entityManagerFactoryBean;
	}

	private Properties jpaHibernateProperties() {
		Properties props = new Properties();
		props.setProperty(Environment.DIALECT, ConfFileInfo.get("db.dialect"));
		props.setProperty(Environment.SHOW_SQL, "true");
		props.setProperty(Environment.FORMAT_SQL, "true");
		props.setProperty(Environment.HBM2DDL_AUTO, ConfFileInfo.get("db.ddl.auto"));
		props.setProperty(Environment.DEFAULT_SCHEMA, ConfFileInfo.get("db.default.schema"));
		props.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "true");
		props.setProperty(Environment.USE_QUERY_CACHE, "true");
		props.setProperty(Environment.CACHE_REGION_FACTORY, "org.hibernate.cache.ehcache.EhCacheRegionFactory");
		props.setProperty(Environment.C3P0_MIN_SIZE, "1");
		props.setProperty(Environment.C3P0_MAX_SIZE, "5");
		props.setProperty(Environment.C3P0_TIMEOUT, "10");
		props.setProperty(Environment.C3P0_IDLE_TEST_PERIOD, "60000");
		return props;
	}

}
