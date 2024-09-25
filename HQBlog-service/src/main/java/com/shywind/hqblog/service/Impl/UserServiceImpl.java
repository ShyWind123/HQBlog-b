package com.shywind.hqblog.service.Impl;

import com.shywind.hqblog.PO.HeatmapPO;
import com.shywind.hqblog.PO.RankPO;
import com.shywind.hqblog.PO.TagPO;
import com.shywind.hqblog.VO.HeatmapVO;
import com.shywind.hqblog.VO.RankVO;
import com.shywind.hqblog.VO.UserInfoVO;
import com.shywind.hqblog.Entity.UserInfo;
import com.shywind.hqblog.Result.Result;
import com.shywind.hqblog.DTO.ChangePasswordDTO;
import com.shywind.hqblog.DTO.ChangeUsernameDTO;
import com.shywind.hqblog.mapper.BlogMapper;
import com.shywind.hqblog.mapper.UserMapper;
import com.shywind.hqblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * @description TODO
     * @params uid
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/6/3 11:19
     */
    @Override
    public Result getUserInfo(int uid) {
        // 获取用户
        UserInfo user = userMapper.selectById(uid);

        //用户不存在
        if (user == null) {
            return Result.error("该用户不存在！");
        }

        // 返回DTO
        UserInfoVO userInfo = new UserInfoVO(user);

        Integer blogs = blogMapper.getBlogCnt(uid);
        Integer likes = blogMapper.getBlogsLikes(uid);
        Integer views = blogMapper.getBlogsViews(uid);

        userInfo.setBlogs(blogs == null ? 0 : blogs);
        userInfo.setLikes(likes == null ? 0 : likes);
        userInfo.setViews(views == null ? 0 : views);

        // 返回用户信息
        return Result.success("获取信息成功！", userInfo);
    }

    
    /**
     * @description TODO 
     * @params changeUsernameVO       
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/6/3 11:54
     */
    @Override
    public Result changeUsername(ChangeUsernameDTO changeUsernameDTO) {
        // 获取用户
        UserInfo user = userMapper.selectById(changeUsernameDTO.getUid());

        // 用户不存在
        if (user == null) {
            return Result.error("该用户不存在！");
        }

        // 更新用户名
        user.setUsername(changeUsernameDTO.getUsername());
        userMapper.updateById(user);

        // 删除redis的缓存
        redisTemplate.delete("HQBlog:global:homeRankList");

        // 返回成功信息
        return Result.success("修改用户名成功！", changeUsernameDTO.getUsername());
    }

    /**
     * @description TODO
     * @params uid
    multipartFile
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/6/3 14:30
     */
    @Override
    public Result changeAvatar(String uid, MultipartFile avatar) {
        // 获取用户
        UserInfo user =  userMapper.selectById(uid);

        //用户不存在
        if (user == null) {
            return Result.error("用户不存在！");
        }

        // 文件夹路径 返回url  头像名字
        String avatarPath = "/root/HQProj/HQBlog-b/avatars/";
        String baseUrl = "http://8.134.215.31:2002/avatar/";
        String avatarName = uid + "_" +  String.valueOf(LocalDate.now()) + ".jpg";

        //保存头像
        try{
            OutputStream out = new FileOutputStream(avatarPath + avatarName);
            out.write(avatar.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            System.out.println(e);
            return Result.error("上传头像出错！");
        }

        // 拼接url
        String avatarUrl = baseUrl + avatarName;

        // 更新数据库
        user.setAvatar(avatarUrl);
        userMapper.updateById(user);

        //返回url
        return Result.success("修改头像成功！", avatarUrl);
    }

    /**
     * @description TODO 
     * @params changePasswordVO       
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/6/3 11:19
     */
    @Override
    public Result changePassword(ChangePasswordDTO changePasswordDTO) {
        // 根据id获取用户
        UserInfo user =  userMapper.selectById(changePasswordDTO.getUid());

        // 用户不存在
        if (user == null) {
            return Result.error("用户不存在！");
        }

        // 原密码错误
        if (!user.getPassword().equals(changePasswordDTO.getOldPassword())){
            return Result.error("原密码错误！");
        }

        // 修改成功
        user.setPassword(changePasswordDTO.getNewPassword());
        userMapper.updateById(user);
        return Result.success("修改密码成功！");
    }

    /**
     * @description TODO 
     * @params uid       
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/6/4 15:46
     */
    @Override
    public Result getTags(Integer uid) {
        // 检查用户是否存在
        UserInfo user = userMapper.selectById(uid);
        if (user == null) {
            return Result.error("该用户不存在！");
        }

        // 获取tags
        List<TagPO> tags = userMapper.getUserTagsByUid(uid);

        // 返回tags
        return Result.success("获取标签成功！", tags);
    }

    /**
     * @description TODO
     * @params uid
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/6/5 10:34
     */
    @Override
    public Result getHeatmap(Integer uid, String type) {
        // 查看用户是否存在
        UserInfo user = userMapper.selectById(uid);
        if (user == null) {
            return Result.error("该用户不存在！");
        }

        // 查询heatmap数据
        List<HeatmapPO> heatmaps = userMapper.getUserHeatmapByUid(uid);

        // type == year, 查询过一年博客数量
        if (type.equals("year")){
            // 获得现在和过去一年的时间
            String startDay = LocalDate.now().minus(1, ChronoUnit.YEARS).toString();
            // 筛选
            heatmaps = heatmaps.stream().filter(heatmap->heatmap.getDate().compareTo(startDay) >= 0 && heatmap.getCnt()>0).collect(Collectors.toList());;
        } else if (type.equals("month")) {
            // type == year, 查询过一年博客数量
            // 获取当月时间
            String startDay = LocalDate.now().withDayOfMonth(1).toString();
            // 筛选
            heatmaps = heatmaps.stream().filter(heatmap->heatmap.getDate().compareTo(startDay) >= 0 && heatmap.getCnt()>0).collect(Collectors.toList());;
        }

        int totalCnt = 0;
        int maxCnt = 0;
        for (HeatmapPO heatmap : heatmaps) {
            totalCnt += heatmap.getCnt();
            maxCnt = Math.max(maxCnt, heatmap.getCnt());
        }

        // 返回
        return Result.success("获取热力图数据成功！", new HeatmapVO(heatmaps, totalCnt, maxCnt));
    }
}
