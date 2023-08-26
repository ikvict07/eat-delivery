package com.delivery.customer.controller;

import com.delivery.customer.model.Customer;
import com.delivery.customer.repository.CustomerRepository;
import com.delivery.customer.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    public void getSOme() {
        System.out.println(customerRepository.findCustomerByName(""));
        System.out.println(orderRepository.findOrdersByCustomer(null));
    }
}
