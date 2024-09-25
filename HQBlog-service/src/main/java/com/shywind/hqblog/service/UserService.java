package com.shywind.hqblog.service;

import com.shywind.hqblog.Result.Result;
import com.shywind.hqblog.DTO.ChangePasswordDTO;
import com.shywind.hqblog.DTO.ChangeUsernameDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.security.NoSuchAlgorithmException;

@Component
public interface UserService {
    Result changePassword(ChangePasswordDTO changePasswordDTO);

    Result getUserInfo(int uid);

    Result changeUsername(ChangeUsernameDTO changeUsernameDTO);

    Result changeAvatar(String uid, MultipartFile avatar);

    Result getTags(Integer uid);

    Result getHeatmap(Integer uid, String type);

    void hashingPassword() throws NoSuchAlgorithmException;
}
