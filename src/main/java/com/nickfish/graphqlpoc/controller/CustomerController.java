package com.nickfish.graphqlpoc.controller;

import com.nickfish.graphqlpoc.dto.CustomerRecord;
import com.nickfish.graphqlpoc.dto.LoginRecord;
import com.nickfish.graphqlpoc.model.Customer;
import com.nickfish.graphqlpoc.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AuthenticationProvider authenticationProvider;


    @SchemaMapping(typeName = "Query", value = "customers")
    public List<Customer> customers() {
        List<Customer> customers = this.customerService.getAll();

        return customers;
    }

    @SchemaMapping(typeName = "Query", value = "customer")
    public Customer customer(@Argument Integer id) {
        Customer customer = this.customerService.getById(id);

        return customer;
    }


    @PreAuthorize("isAnonymous()")
    @MutationMapping(value = "login")
    public Customer login(@Argument(value = "record") LoginRecord record) {

        UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(record.email(), record.pwd());
        try {
            SecurityContextHolder.getContext().setAuthentication(authenticationProvider.authenticate(credentials));
            return this.customerService.getCurrentUser();
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException(record.email());
        }
    }

    @PreAuthorize("isAnonymous()")
    @MutationMapping(value = "addCustomer")
    public Customer add(
            @Argument("customer") CustomerRecord customerRecord
    ) {
        return this.customerService.add(customerRecord);
    }

    @PreAuthorize("isAuthenticated()")
    @MutationMapping(value = "updateCustomer")
    public Customer update(
            @Argument("customer") CustomerRecord customerRecord
    ) {
        return this.customerService.update(customerRecord);
    }
}
