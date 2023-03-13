package com.nickfish.graphqlpoc.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nickfish.graphqlpoc.config.JWTUserDetails;
import com.nickfish.graphqlpoc.config.PasswordUtil;
import com.nickfish.graphqlpoc.config.SecurityProperties;
import com.nickfish.graphqlpoc.dao.CustomerDAO;
import com.nickfish.graphqlpoc.dto.CustomerRecord;
import com.nickfish.graphqlpoc.dto.LoginRecord;
import com.nickfish.graphqlpoc.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.*;


@Service
@Slf4j
public class CustomerService implements UserDetailsService {

    @Autowired
    private SecurityProperties properties;

    @Autowired
    private Algorithm algorithm;

    @Autowired
    private CustomerDAO dao;

    public List<Customer> getAll(){
        return dao.findAll();
    }

    @Transactional
    public Customer add(CustomerRecord customerRecord){

        Customer customer = Customer.builder()
                .firstName(customerRecord.firstName())
                .lastName(customerRecord.lastName())
                .email(customerRecord.email())
                .pwd(PasswordUtil.encoder().encode(customerRecord.pwd()))
                .build();

        return this.dao.save(customer);
    }

    @Transactional
    public Customer update(CustomerRecord customerRecord){

        Optional<Customer> c = this.dao.findCustomerByEmail(customerRecord.email());

        if(c.isPresent()){
            Customer customer = c.get();
            customer.setFirstName(customerRecord.firstName());
            customer.setLastName(customerRecord.lastName());

            return  this.dao.save(customer);
        }

        return null;
    }

    public Customer getById(Integer id){
        return this.dao.getReferenceById(id);
    }


    public Customer login(LoginRecord record){
        Optional<Customer> c = this.dao.findCustomerByEmail(record.email());

        if(c.isPresent()){
            Customer customer = c.get();
            if(PasswordUtil.encoder().matches(record.pwd(), customer.getPwd())){
                return customer;
            }else{
                log.warn("Incorrect Password");
            }
        }else{ log.warn("Account not found");}

        return null;
    }


    public JWTUserDetails getByEmail(String email){
        Optional<Customer> c = this.dao.findCustomerByEmail(email);

        if(c != null){
            Customer customer = c.get();

            List<SimpleGrantedAuthority> roles = new ArrayList<>();
            SimpleGrantedAuthority a = new SimpleGrantedAuthority("ADMIN");
            roles.add(a);

            JWTUserDetails user = new JWTUserDetails(customer.getEmail(), customer.getPwd(), roles, getToken(customer));

            return user;
        }else{ log.warn("Account not found");}

        return null;
    }

    @Transactional
    public String getToken(Customer customer) {
        Instant now = Instant.now();
        Instant expiry = Instant.now().plus(properties.getTokenExpiration());
        return JWT
                .create()
                .withIssuer(properties.getTokenIssuer())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry))
                .withSubject(customer.getEmail())
                .sign(algorithm);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> c = this.dao.findCustomerByEmail(username);

        if(c.isPresent()){
            Customer customer = c.get();

            List<SimpleGrantedAuthority> roles = new ArrayList<>();
            SimpleGrantedAuthority a = new SimpleGrantedAuthority("ADMIN");
            roles.add(a);

            JWTUserDetails user = new JWTUserDetails(customer.getEmail(), customer.getPwd(), roles, getToken(customer));

            return user;
        }else{ log.warn("Account not found");}

        return null;
    }


    @Transactional
    public Customer getCurrentUser() {
        return Optional
                .ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .flatMap(dao::findCustomerByEmail)
                .orElse(null);
    }
}
