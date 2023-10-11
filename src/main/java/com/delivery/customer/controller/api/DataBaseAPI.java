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

    /**
     * Checks if a customer exists with the given email address.
     *
     * @param email The email address of the customer to check.
     * @return true if a customer exists with the given email address, false otherwise.
     */
    @GetMapping("/exists-by-email")
    private boolean existsByEmail(@RequestParam String email) {
        return customerRepository.existsByEmail(email);
    }

    /**
     * Registers a new customer.
     *
     * @param customer The customer details to register.
     * @param request  The HTTP request object.
     * @return A ResponseEntity object with status 200 if the customer was registered
     * successfully, or status 401 with an error message if the user does not have
     * the required permissions.
     */
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
    /**
     * Logs in a customer.
     *
     * @param signInRequest The sign in request with the customer's email and password.
     * @param request       The HTTP request object.
     * @return A ResponseEntity object with status 200 and a success message if the customer
     *         successfully logs in, or status 401 with an error message if the user does not
     *         have the required permissions or is not authorized, or status 400 with an error
     *         message if the username or password is invalid or the customer can not be logged
     *         in with another role.
     */
    @PostMapping("/login-customer")
    private ResponseEntity<String> loginCustomer(@RequestBody SignInRequest signInRequest, HttpServletRequest request) {
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

    /**
     * Checks if the request has the required permission.
     *
     * @param request The HTTP request object.
     * @return true if the request has the required permission, false otherwise.
     */
    private boolean isRequestHavingPermission(HttpServletRequest request) {
        String jwt = resolveToken(request);
        if (jwt == null) {
            return false;
        }
        return (Objects.equals(jwtCore.getKeyFromJwt(jwt), "Allowed"));
    }

    /**
     * Resolves the bearer token from the Authorization header of the HTTP request.
     *
     * @param request The HTTP request object.
     * @return The bearer token if found, null otherwise.
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
