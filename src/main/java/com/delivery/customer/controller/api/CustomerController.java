package com.delivery.customer.controller.api;

import com.delivery.customer.DTO.CustomerInfo;
import com.delivery.customer.model.Customer;
import com.delivery.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/customer/")
public class CustomerController {
    private CustomerRepository customerRepository;

    @Autowired
    private void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/info")
    private ResponseEntity<?> userInfo(Principal principal) {
        Optional<Customer> optionalCustomer = customerRepository.findCustomerByEmail(principal.getName());
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            return ResponseEntity.ok().body(
                    new CustomerInfo(customer.getEmail(), customer.getName(), customer.getPhone()));
        }

        return ResponseEntity.notFound().build();
    }

}
