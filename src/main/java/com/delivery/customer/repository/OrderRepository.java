package com.delivery.customer.repository;

import com.delivery.customer.model.Customer;
import com.delivery.customer.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findOrdersByCustomer(Customer customer);
}
