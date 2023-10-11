package com.delivery.customer.DTO;

import lombok.Data;

@Data
public class RestaurantMenuItem {
    private Long id;
    private Long restaurant_id;
    private String name;
    private Long price;
    private String image_url;
    private String description;
}
