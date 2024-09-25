package com.shywind.hqblog.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterInfoDTO {
    private String username;
    private String email;
    private String password;
    private String code;
}
