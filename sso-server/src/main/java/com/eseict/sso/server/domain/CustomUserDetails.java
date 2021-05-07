package com.eseict.sso.server.domain;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name="oms_user_info", schema="oms")
public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 3781268471513471948L;
	
	@Id
	@Column(name="user_id", length=22)
	String userId;
	
//	user_login_id character varying(25) COLLATE pg_catalog."default",
//	user_login_pswd character varying(64) COLLATE pg_catalog."default",
	
	@Column(name="user_login_id", length=25)
	String username;
	@Column(name="user_login_pswd", length=64)
	String password;

//	user_nm character varying(30) COLLATE pg_catalog."default",
	@Column(name="user_nm", length=30)
	String userNm;
	@Column(name="cp_no", length=15)
	String cpNo;
	@Column(name="email", length=30)
	String email;
	@Column(name="phone", length=15)
	String phone;
	@Column(name="fist_reg_dtm", length=17)
	String fistRegDtm;
	@Column(name="lst_login_dtm", length=17)
	String lstLoginDtm;
	
	@Column(name="use_yn", length=1)
	String useYn;
	
//	String dptId;
//	String roleId;
	

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled() {
		if ( "Y".equals(getUseYn())){
			return true;
		} else {
			return false;
		}
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserNm() {
		return userNm;
	}

	public void setUserNm(String userNm) {
		this.userNm = userNm;
	}

	public String getCpNo() {
		return cpNo;
	}

	public void setCpNo(String cpNo) {
		this.cpNo = cpNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFistRegDtm() {
		return fistRegDtm;
	}

	public void setFistRegDtm(String fistRegDtm) {
		this.fistRegDtm = fistRegDtm;
	}

	public String getLstLoginDtm() {
		return lstLoginDtm;
	}

	public void setLstLoginDtm(String lstLoginDtm) {
		this.lstLoginDtm = lstLoginDtm;
	}

	public String getUseYn() {
		return useYn;
	}

	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	

}
