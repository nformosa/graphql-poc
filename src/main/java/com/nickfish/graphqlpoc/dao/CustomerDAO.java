package com.nickfish.graphqlpoc.dao;

import com.nickfish.graphqlpoc.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerDAO extends JpaRepository<Customer, Integer> {


    Optional<Customer> findCustomerByEmail(String email);

}
