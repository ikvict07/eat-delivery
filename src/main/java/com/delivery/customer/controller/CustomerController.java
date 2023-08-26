package com.delivery.customer.controller;

import com.delivery.customer.model.Customer;
import com.delivery.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/customer/")
public class CustomerController {
    private CustomerRepository customerRepository;

    @Autowired
    private void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @PostMapping("/get-by-email")
    private Customer getCustomerByEmail(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        return customerRepository.findCustomerByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer not found"));
    }

    @PostMapping("/")
    private void createCustomer() {
        Customer customer = new Customer();
        customer.setEmail("email2");
        customer.setPassword(new BCryptPasswordEncoder().encode("password"));

        customerRepository.save(customer);
    }
}
