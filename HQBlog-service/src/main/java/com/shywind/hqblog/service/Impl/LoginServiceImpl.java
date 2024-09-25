package com.shywind.hqblog.service.Impl;

import com.alibaba.fastjson.JSON;
import com.shywind.hqblog.Tools.MyTool;
import com.shywind.hqblog.VO.LoginSuccessVO;
import com.shywind.hqblog.Entity.UserInfo;
import com.shywind.hqblog.VO.EmailCodeVO;
import com.shywind.hqblog.Result.Result;
import com.shywind.hqblog.Utils.JWTUtils;
import com.shywind.hqblog.DTO.LoginCodeInfoDTO;
import com.shywind.hqblog.DTO.LoginPasswordInfoDTO;
import com.shywind.hqblog.DTO.RegisterInfoDTO;
import com.shywind.hqblog.mapper.LoginMapper;
import com.shywind.hqblog.service.LoginService;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private LoginMapper loginMapper;

    @Value("${spring.mail.username}")
    private String fromEmail;
    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;

    private String mailTitle = "HQBLOG";

    /**
     * @description TODO
     * @params email
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/5/29 10:46
     */
    Result sendEmailCode (String email) {
        // 设置refis key
        String redisKey = String.format("HQBlog:user:mailCode:%s", email);
        // 查看是否在一分钟内发送过验证码
        if(redisTemplate.hasKey(redisKey) && redisTemplate.getExpire(redisKey) > 500) {
            return Result.error("请勿在1分钟之内请求发送验证码！");
        }

        // 生成验证码
        // 随机数类
        Random random = new Random();
        // 生成验证码
        String code = String.valueOf(random.nextInt(899999) + 100000);
        /*// 发送验证码
        // 设置邮件信息
        String mailContent = "【HQBLOG】您好，您的验证码为："+code+",请在10分钟内使用，若非本人操作，可以忽视本条信息。";
        // 初始化
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        //设置发件邮箱
        simpleMailMessage.setFrom(fromEmail);
        //收件人邮箱
        simpleMailMessage.setTo(email);
        //主题标题
        simpleMailMessage.setSubject(mailTitle);
        //信息内容
        simpleMailMessage.setText(mailContent);
        //执行发送
        try {//发送可能失败
            javaMailSender.send(simpleMailMessage);
            //没有异常返回true，表示发送成功
        } catch (Exception e) {
            //有异常 直接返回
            System.out.println("验证码邮件发送失败！");
            System.out.println(e);
            return Result.error("验证码邮件发送失败！");
        }
        */
        /*// 存进redis
        try{

         } catch (Exception e) {
            System.out.println("存入redis失败");
            System.out.println(e);
           return Result.error("未知错误");
         }
        */

        // 实体化
        EmailCodeVO emailCode = new EmailCodeVO(email,code);

        // 存入消息队列
        rabbitTemplate.convertAndSend("sendEmailCodeAndSaveToRedis", "", JSON.toJSONString(emailCode));

        // 返回验证码
        return new Result(1, "验证码发送成功！请在10分钟内填写。", emailCode);
    }

    // 存入redis
    public void saveToRedis(String email, String code) throws Exception {
        try {
            // 设置refis key
            String redisKey = String.format("HQBlog:user:mailCode:%s", email);
            redisTemplate.opsForValue().set(redisKey, code,10, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw e;
        }
    }

    // 发送验证码
    public void sendCode(String email, String code) throws Exception {
        // 发送验证码
        // 设置邮件信息
        String mailContent = "【HQBLOG】您好，您的验证码为："+code+",请在10分钟内使用，若非本人操作，可以忽视本条信息。";
        // 初始化
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        //设置发件邮箱
        simpleMailMessage.setFrom(fromEmail);
        //收件人邮箱
        simpleMailMessage.setTo(email);
        //主题标题
        simpleMailMessage.setSubject(mailTitle);
        //信息内容
        simpleMailMessage.setText(mailContent);
        //执行发送
        javaMailSender.send(simpleMailMessage);
    }

    /**
     * @description TODO 
     * @params email       
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/5/14 17:49
     */
    public Result loginByCode(String email) {
        // 查询用户是否注册过
        List<UserInfo> userList = loginMapper.getUserByEmail(email);
        if (userList.isEmpty()) {
            return Result.error("该用户未注册，请先注册！");
        }

        // 发送验证码
        return sendEmailCode(email);
    }

    /**
     * @description TODO
     * @params loginInfo
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/5/29 11:36
     */
    public Result loginByPassword(LoginPasswordInfoDTO loginPasswordInfo) {
        // 获取用户列表
        List<UserInfo> userList = loginMapper.getUserByEmail(loginPasswordInfo.getEmail());
        // 不存在用户
        if (userList.isEmpty()) {
            return Result.error("该用户不存在！");
        }

        UserInfo user = userList.get(0);
        // 密码错误
        if (!user.getPassword().equals(MyTool.getHashString(loginPasswordInfo.getPassword()))) {
            return Result.error("密码错误！");
        }

        // 删除redis中的key
        String redisKey = String.format("HQBlog:user:mailCode:%s", loginPasswordInfo.getEmail());
        redisTemplate.delete(redisKey);

        // 获取token
        String token = getJWTToken(loginPasswordInfo.getEmail());
        LoginSuccessVO lsDTO = new LoginSuccessVO(user.getUid(),token);

        // 成功登录
        return Result.success("登录成功！", lsDTO);
    }

    /**
     * @description TODO
     * @params email
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/5/29 10:46
     */
    public Result sendRegisterEmailCode (String email) {
        // 查询用户是否注册过
        List<UserInfo> userList = loginMapper.getUserByEmail(email);
        if (!userList.isEmpty()) {
            return Result.error("该用户已注册，请勿重复注册！");
        }

        // 发送验证码
        return sendEmailCode(email);
    }
    
    /**
     * @description TODO 
     * @params loginPasswordInfo       
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/5/29 11:51
     */
    public Result codeLoginSubmit(LoginCodeInfoDTO loginCodeInfo) {
        // 获取正确验证码
        String redisKey = String.format("HQBlog:user:mailCode:%s", loginCodeInfo.getEmail());

        // 验证码过期
        if (!redisTemplate.hasKey(redisKey)) {
            return Result.error("验证码失效！");
        }

        // 验证码错误
        if (!redisTemplate.opsForValue().get(redisKey).equals(loginCodeInfo.getCode())){
            return Result.error("验证码错误");
        }

        // 删除redis中的key
        redisTemplate.delete(redisKey);

        // 获取用户uid
        List<UserInfo> userList =  loginMapper.getUserByEmail(loginCodeInfo.getEmail());
        UserInfo user =  userList.get(0);

        // 获取token
        String token = getJWTToken(loginCodeInfo.getEmail());
        LoginSuccessVO lsDTO = new LoginSuccessVO(user.getUid(),token);

        // 成功登录
        return Result.success("登录成功！", lsDTO);
    }
    
    /**
     * @description TODO 
     * @params registerInfo       
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/5/28 14:29
     */
    public Result registerSubmit (RegisterInfoDTO registerInfo) {
        // 设置redis key
        String redisKey = String.format("HQBlog:user:mailCode:%s", registerInfo.getEmail());

        // 验证码过期
        if (!redisTemplate.hasKey(redisKey)) {
            return Result.error("验证码失效！");
        }

        // 比对验证码
        if (!redisTemplate.opsForValue().get(redisKey).equals(registerInfo.getCode())) {
            return Result.error("验证码错误！");
        }

//        // 创建对象
//        UserInfo userInfo = new UserInfo();
//        userInfo.setUsername(registerInfo.getUsername());
//        userInfo.setEmail(registerInfo.getEmail());
//        userInfo.setPassword(registerInfo.getPassword());
        // 写入数据库
        loginMapper.insertUser(registerInfo.getUsername(), registerInfo.getEmail(), MyTool.getHashString(registerInfo.getPassword()));

        // 获取用户uid
        List<UserInfo> userList =  loginMapper.getUserByEmail(registerInfo.getEmail());
        UserInfo user =  userList.get(0);

        // 删除redis中的key
        redisTemplate.delete(redisKey);

        // 获取token
        String token = getJWTToken(registerInfo.getEmail());
        LoginSuccessVO lsDTO = new LoginSuccessVO(user.getUid(),token);

        // 返回
        return Result.success("注册成功！", lsDTO);
    }

    public String getJWTToken(String email) {
        // 获取用户信息
        List<UserInfo> userList = loginMapper.getUserByEmail(email);
        UserInfo user = userList.get(0);

        // 创建payload
        Map<String, String> payload = new HashMap<>();
        payload.put("uid", String.valueOf(user.getUid()));
        payload.put("email",user.getEmail());
        payload.put("username",user.getUsername());

        // 返回token
        return JWTUtils.getToken(payload);
    }
}
