package com.example.simplifyStorePrime.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppUserDTO {
    private Integer id;
    private String username;
    private String role;
    private boolean enabled;
}
