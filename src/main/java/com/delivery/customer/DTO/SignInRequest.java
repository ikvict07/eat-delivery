package com.delivery.customer.DTO;

import lombok.Data;

@Data
public class SignInRequest {
    private String email;
    private String password;
}
