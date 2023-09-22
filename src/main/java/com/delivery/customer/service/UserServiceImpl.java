package com.delivery.customer.service;

import com.delivery.customer.model.Customer;
import com.delivery.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserDetailsService {
    private CustomerRepository customerRepository;

    @Autowired
    private void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findCustomerByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                "No user was found with this email"));
        return UserDetailsImpl.build(customer);
    }

}
