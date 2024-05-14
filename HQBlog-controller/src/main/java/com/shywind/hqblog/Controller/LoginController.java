package com.shywind.hqblog.Controller;

import com.shywind.hqblog.Result.Result;
import com.shywind.hqblog.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;
    /**
     * @description TODO 
     * @params email       
     * @return java.lang.String
     * @author ShyWind
     * @date 2024/5/13 20:52
     */
    @GetMapping("/login/code")
    public Result email(String email){
        return loginService.sendEmailCode(email);
    }
}
