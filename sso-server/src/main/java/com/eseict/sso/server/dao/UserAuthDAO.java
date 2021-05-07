package com.eseict.sso.server.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.eseict.sso.server.domain.CustomUserDetails;

@Repository("userAuthDAO")
public class UserAuthDAO {
    
	@PersistenceContext
    EntityManager em;
 
	private static Logger logger = LoggerFactory.getLogger(UserAuthDAO.class); 
	
    public CustomUserDetails loadUserByUsername(String username) {

    	CustomUserDetails find = null;
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append(" SELECT cud FROM CustomUserDetails AS cud");
    	sb.append(" WHERE 1=1 ");
    	
		sb.append(" AND username = :username ");
    	
    	TypedQuery<CustomUserDetails> query = em.createQuery(sb.toString(), CustomUserDetails.class);
		query.setParameter("username", username);
		
		try {
			find = query.getSingleResult();
		} catch (NoResultException e) {
			logger.error(e.getMessage());
		}
		
    	return find;
    }
    
 
}
