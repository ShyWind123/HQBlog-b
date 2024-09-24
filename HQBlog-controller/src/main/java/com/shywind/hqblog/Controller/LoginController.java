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
    /**
     * @description TODO 
     * @params email       
     * @return java.lang.String
     * @author ShyWind
     * @date 2024/5/13 20:52
     */
    @GetMapping("/login/code")
    public Result loginByCode(String email){
        return loginService.loginByCode(email);
    }


    /**
     * @description TODO 
     * @params loginInfo
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/5/29 11:35
     */
    @PostMapping("/login/password")
    public Result loginByPassword(@RequestBody LoginPasswordInfoDTO loginPasswordInfo){return loginService.loginByPassword(loginPasswordInfo);}

    /**
     * @description TODO
     * @params email
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/5/29 10:45
     */
    @GetMapping("/register/code")
    public Result sendRegisterEmail(String email){ return loginService.sendRegisterEmailCode(email);}

    
    /**
     * @description TODO 
     * @params loginPasswordInfo       
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/5/29 11:49
     */
    @PostMapping("/login/code/submit")
    public Result codeLoginSubmit(@RequestBody LoginCodeInfoDTO loginCodeInfo) {
        return loginService.codeLoginSubmit(loginCodeInfo);
    }


    /**
     * @description TODO
     * @params registerInfo
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/5/21 14:01
     */
    @PostMapping("/register/submit")
    public Result registerSubmit(@RequestBody RegisterInfoDTO registerInfo) {
        return loginService.registerSubmit(registerInfo);
    }

}
