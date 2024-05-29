package com.shywind.hqblog.Controller;

import com.shywind.hqblog.Result.Result;
import com.shywind.hqblog.VO.LoginCodeInfoVO;
import com.shywind.hqblog.VO.LoginPasswordInfoVO;
import com.shywind.hqblog.VO.RegisterInfoVO;
import com.shywind.hqblog.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public Result loginByPassword(@RequestBody LoginPasswordInfoVO loginPasswordInfo){return loginService.loginByPassword(loginPasswordInfo);}

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
    public Result codeLoginSubmit(@RequestBody LoginCodeInfoVO loginCodeInfo) {
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
    public Result registerSubmit(@RequestBody RegisterInfoVO registerInfo) {
        return loginService.registerSubmit(registerInfo);
    }

    @GetMapping("/test")
    public String test(String a) {
        return a;
    }
}
