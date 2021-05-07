package com.eseict.sso.server.service;

import org.springframework.data.repository.CrudRepository;

import com.eseict.sso.server.domain.Client;

public interface ClientRepository extends CrudRepository<Client, String> {
	//
}
