package com.shywind.hqblog.Controller;

import com.shywind.hqblog.Result.Result;
import com.shywind.hqblog.DTO.ChangePasswordDTO;
import com.shywind.hqblog.DTO.ChangeUsernameDTO;
import com.shywind.hqblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/user/info")
    public Result getUserInfo(int uid){
        return userService.getUserInfo(uid);
    }

    @PutMapping("/user/password")
    public Result changePassword(@RequestBody ChangePasswordDTO changePasswordDTO){
        return userService.changePassword(changePasswordDTO);
    }

    @PutMapping("/user/username")
    public Result changeUsername(@RequestBody ChangeUsernameDTO changeUsernameDTO){
        return userService.changeUsername(changeUsernameDTO);
    }

    @PutMapping("/user/avatar")
    public Result changeAvatar(@RequestParam String uid, @RequestParam MultipartFile avatar) {
        return userService.changeAvatar(uid, avatar);
    }

    @GetMapping("/user/tags")
    public Result getTags(Integer uid) {
        return userService.getTags(uid);
    }

    @GetMapping("/user/heatmap")
    public Result getHeatmap(Integer uid, String type) {return userService.getHeatmap(uid, type);}
}
