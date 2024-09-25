package com.shywind.hqblog.Controller;

import com.shywind.hqblog.Result.Result;
import com.shywind.hqblog.DTO.LoginCodeInfoDTO;
import com.shywind.hqblog.DTO.LoginPasswordInfoDTO;
import com.shywind.hqblog.DTO.RegisterInfoDTO;
import com.shywind.hqblog.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class LoginController {
    @Autowired
    private LoginService loginService;

    @GetMapping("/login/code")
    public Result loginByCode(String email){
        return loginService.loginByCode(email);
    }

    @PostMapping("/login/password")
    public Result loginByPassword(@RequestBody LoginPasswordInfoDTO loginPasswordInfo){return loginService.loginByPassword(loginPasswordInfo);}

    @GetMapping("/register/code")
    public Result sendRegisterEmail(String email){ return loginService.sendRegisterEmailCode(email);}

    @PostMapping("/login/code/submit")
    public Result codeLoginSubmit(@RequestBody LoginCodeInfoDTO loginCodeInfo) {
        return loginService.codeLoginSubmit(loginCodeInfo);
    }

    @PostMapping("/register/submit")
    public Result registerSubmit(@RequestBody RegisterInfoDTO registerInfo) {
        return loginService.registerSubmit(registerInfo);
    }

}
