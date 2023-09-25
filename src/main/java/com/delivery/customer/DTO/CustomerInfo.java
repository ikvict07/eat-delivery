package com.delivery.customer.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerInfo {
    private String email;
    private String name;
    private String password;
    private final String role = "CUSTOMER";
}
