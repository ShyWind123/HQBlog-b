package com.shywind.hqblog.service;

import com.shywind.hqblog.Result.Result;
import com.shywind.hqblog.DTO.LoginCodeInfoDTO;
import com.shywind.hqblog.DTO.LoginPasswordInfoDTO;
import com.shywind.hqblog.DTO.RegisterInfoDTO;
import com.shywind.hqblog.VO.EmailCodeVO;
import org.springframework.stereotype.Component;

@Component
public interface LoginService {
    Result loginByCode(String email);
    Result loginByPassword(LoginPasswordInfoDTO loginPasswordInfo);
    Result sendRegisterEmailCode(String email);
    Result codeLoginSubmit(LoginCodeInfoDTO loginCodeInfo);
    Result registerSubmit(RegisterInfoDTO registerInfo);
    void sendCode(String email, String code) throws Exception;
    void saveToRedis(String email, String code) throws Exception;

}
