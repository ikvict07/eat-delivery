package com.delivery.customer.controller.api;

import com.delivery.customer.jwt.JwtCore;
import com.delivery.customer.model.Customer;
import com.delivery.customer.repository.CustomerRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/customer-db/")
public class DataBaseAPI {
    private CustomerRepository customerRepository;
    private JwtCore jwtCore;

    @Autowired
    private void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Autowired
    private void setJwtCore(JwtCore jwtCore) {
        this.jwtCore = jwtCore;
    }

    @GetMapping("/exists-by-email")
    private boolean existsByEmail(@RequestParam String email) {
        return customerRepository.existsByEmail(email);
    }

    @PostMapping("/register-customer")
    public ResponseEntity<?> register(@RequestBody Customer customer, HttpServletRequest request) {
        String jwt = resolveToken(request);
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You don`t have permissions");
        }
        if (!(Objects.equals(jwtCore.getKeyFromJwt(jwt), "Allowed"))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You don`t have permissions");
        }

        customerRepository.save(customer);
        return ResponseEntity.ok("Customer was registered");
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
}
