package com.shywind.hqblog.service.Impl;

import com.alibaba.fastjson.JSON;
import com.shywind.hqblog.Entity.Blog;
import com.shywind.hqblog.PO.RankPO;
import com.shywind.hqblog.PO.TagPO;
import com.shywind.hqblog.Result.Result;
import com.shywind.hqblog.Utils.RestHighLevelClientUtils;
import com.shywind.hqblog.VO.ListBlogVO;
import com.shywind.hqblog.VO.CreateBlogVO;
import com.shywind.hqblog.VO.RankVO;
import com.shywind.hqblog.mapper.BlogMapper;
import com.shywind.hqblog.mapper.GlobalMapper;
import com.shywind.hqblog.mapper.UserMapper;
import com.shywind.hqblog.service.GlobalService;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class GlobalServiceImpl implements GlobalService {
    @Autowired
    private GlobalMapper globalMapper;
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * @description TODO
     * @params
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/6/17 10:18
     */
    @Override
    public Result getRanks() {
        // redis中有
        String redisKey = "HQBlog:global:homeRankList";
        if (redisTemplate.hasKey(redisKey)) {
            List<RankVO> rankList = JSON.parseArray((String) redisTemplate.opsForValue().get(redisKey), RankVO.class);
            return Result.success("获取排行成功！", rankList);
        }

        // redis没有
        // 从数据库获取ranks
        List<RankPO> ranks = globalMapper.getRanks();

        // 返回类
        List<RankVO> returnRanks = new ArrayList<>();

        // 添加喜欢数
        for (int i = 0; i < ranks.size(); i++) {
            RankVO rank = new RankVO(ranks.get(i));
            rank.setBlogs(blogMapper.getBlogCnt(rank.getUid()));
            rank.setLikes(blogMapper.getBlogsLikes(rank.getUid()));
            rank.setViews(blogMapper.getBlogsViews(rank.getUid()));
            returnRanks.add(rank);
        }

        // 存入redis
        // 放入消息队列
        rabbitTemplate.convertAndSend("saveHomeRankListToRedis", "", JSON.toJSONString(returnRanks));

        // 返回
        return Result.success("获取排行成功！", returnRanks);
    }

    /**
     * @description TODO
     * @params rankListStr
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/9/23 16:47
     */
    @Override
    public void saveHomeRankListToRedis(String rankListStr) throws Exception {
        String redisKey = "HQBlog:global:homeRankList";
        try {
            redisTemplate.opsForValue().set(redisKey, rankListStr,1, TimeUnit.DAYS);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @description TODO
     * @params
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/6/17 14:56
     */
    @Override
    public Result getTags() {
        // 获取标签
        List<TagPO> tags = globalMapper.getTags();

        // 返回
        return Result.success("获取所有标签成功！", tags);
    }

    /**
     * @param tags
     * @return com.shywind.hqblog.Result.Result
     * @description TODO
     * @params
     * @author ShyWind
     * @date 2024/6/17 21:42
     */
    @Override
    public Result searchBlogsByTags(List<String> tags) {
        // 获取Blogs
        List<Blog> blogs = blogMapper.getAllBlogs("发布",false);
        // 返回对象
        List<ListBlogVO> blogVOs = new ArrayList<>();

        // 筛选
        for (Blog blog: blogs) {
            String[] blogTags1 = blogMapper.getTagsByBlogId(blog.getId());
            List<String> blogTags2 = Arrays.asList(blogTags1);

            boolean hasAlltags = true;
            for (String tag : tags) {
                if (!blogTags2.contains(tag)) {
                    hasAlltags = false;
                    break;
                }
            }

            if (hasAlltags) {
                ListBlogVO blogVO = new ListBlogVO(blog);
                blogVO.setTags(blogTags1);
                blogVO.setAuthor(userMapper.getUsernameByUid(blog.getUid()));
                blogVOs.add(blogVO);
            }
        }

        // 返回
        return Result.success("获取含有特定标签的博客成功！",blogVOs);
    }

    public List<Blog> searchBlogsFromES(String text) {

        RestHighLevelClientUtils clientUtils = new RestHighLevelClientUtils();
        List<Blog> list = clientUtils.searchBlogs(text);
        try{
            clientUtils.close();
            return list;
        } catch (Exception e) {
            System.out.println(e);
        }
        return  list;
    }

    /**
     * @description TODO
     * @params text
     * @return com.shywind.hqblog.Result.Result
     * @author ShyWind
     * @date 2024/6/18 10:49
     */
    @Override
    public Result searchBlogsByText(String text) {
        List<Blog> blogs = searchBlogsFromES(text);
        List<CreateBlogVO> blogVOs = new ArrayList<>();

        for (Blog blog : blogs) {
            CreateBlogVO blogVO = new CreateBlogVO(blog);
            blogVO.setTags(blogMapper.getTagsByBlogId(blog.getId()));
            blogVO.setAuthor(userMapper.getUsernameByUid(blog.getUid()));
            blogVOs.add(blogVO);
        }

        // 返回
        return Result.success("获取含有特定内容的博客成功！",blogVOs);
    }
}
