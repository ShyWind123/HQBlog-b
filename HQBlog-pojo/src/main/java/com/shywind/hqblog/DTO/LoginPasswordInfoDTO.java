package com.shywind.hqblog.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginPasswordInfoDTO {
    private String email;
    private String password;
}
