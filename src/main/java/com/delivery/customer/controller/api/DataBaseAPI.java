package com.delivery.customer.controller.api;

import com.delivery.customer.DTO.SignInRequest;
import com.delivery.customer.jwt.JwtCore;
import com.delivery.customer.model.Customer;
import com.delivery.customer.repository.CustomerRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/customer-db/")
public class DataBaseAPI {
    private CustomerRepository customerRepository;
    private JwtCore jwtCore;
    private PasswordEncoder passwordEncoder;

    @Autowired
    private void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

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
        if (isRequestHavingPermission(request))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You don`t have permissions");

        Customer customerToRegister = new Customer();
        customerToRegister.setEmail(customer.getEmail());
        customerToRegister.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerToRegister.setName(customer.getName());
        customerToRegister.setPhone(customer.getPhone());

        customerRepository.save(customerToRegister);
        return ResponseEntity.ok("Customer was registered");
    }
    @PostMapping("/login-customer")
    private ResponseEntity<String> login(@RequestBody SignInRequest signInRequest, HttpServletRequest request) {
        if (!isRequestHavingPermission(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You don`t have permissions");
        }
        String jwt = resolveToken(request);
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You have to be authorized");
        }


        Optional<Customer> optionalCustomer = customerRepository.findCustomerByEmail(signInRequest.getEmail());
        if (optionalCustomer.isPresent() && passwordEncoder.matches(signInRequest.getPassword(), optionalCustomer.get().getPassword())) {

            if (optionalCustomer.get().getRole().equals("CUSTOMER")) {
                return ResponseEntity.ok().body("Success");
            } else {
                return ResponseEntity.badRequest().body("Customer can not be logged in with another role");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid username or password");
        }
    }

    private boolean isRequestHavingPermission(HttpServletRequest request) {
        String jwt = resolveToken(request);
        if (jwt == null) {
            return false;
        }
        return (Objects.equals(jwtCore.getKeyFromJwt(jwt), "Allowed"));
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
