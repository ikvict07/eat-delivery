package com.delivery.customer.controller;

import com.delivery.customer.DTO.RestaurantMenuItem;
import com.delivery.customer.model.Customer;
import com.delivery.customer.model.Order;
import com.delivery.customer.repository.CustomerRepository;
import com.delivery.customer.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/customer/order/")
public class OrderController {
    private OrderRepository orderRepository;
    private CustomerRepository customerRepository;

    @Autowired
    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/list")
    private ResponseEntity<List<Order>> getListOfOrders(Principal principal) {
        Optional<Customer> optionalCustomer = customerRepository.findCustomerByEmail(principal.getName());

        if (!optionalCustomer.isPresent()) {
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }

        Customer customer = optionalCustomer.get();
        List<Order> orders = orderRepository.findOrdersByCustomer(customer);

        return ResponseEntity.ok(orders);
    }

    @PostMapping("/create")
    private ResponseEntity<?> createOrder() {
        return null;
    }


    @GetMapping("/offers")
    public ResponseEntity<?> getOffers(@RequestParam("from") int start, @RequestParam("to") int stop) {
        //TODO: Send request to Restaurant API and get paginated list of Available products

        return ResponseEntity.ok(new ArrayList<>(Arrays.asList(
                new RestaurantMenuItem(),
                new RestaurantMenuItem(),
                new RestaurantMenuItem(),
                new RestaurantMenuItem()
        )));
    }

}
