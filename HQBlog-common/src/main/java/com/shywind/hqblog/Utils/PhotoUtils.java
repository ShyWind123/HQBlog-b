package com.shywind.hqblog.Utils;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class PhotoUtils implements WebMvcConfigurer {

    //获取本地图片地址

    private String bmpPath="/root/HQProj/HQBlog-b/avatars/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /**
         * 访问路径：http://localhost:2002/avatar/2022001.png
         * "/image/**" 为前端URL访问路径
         * "file:" + bmpPath 是本地磁盘映射
         */
        registry.addResourceHandler("/avatar/**").addResourceLocations("file:" + bmpPath);
    }
}
