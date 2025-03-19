package com.shywind.hqblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shywind.hqblog.Entity.Blog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BlogMapper extends BaseMapper<Blog> {
    // 获取总博客的数量
    @Select("SELECT id FROM blog ORDER BY id DESC LIMIT 1;")
    Integer getBlogMaxId();

    // 博客数量表是否有该用户今天的条目
    @Select("SELECT COUNT(id) = 1 FROM blogs_count WHERE uid = #{uid} AND date = #{today}")
    Boolean hasUserTodayBlogCnt(Integer uid, String today);

    // 插入博客数量表该用户今天的条目
    @Insert("INSERT INTO blogs_count(uid, date, cnt) VALUES (#{uid}, #{today}, #{cnt})")
    void insertUserTodayBlogCnt(Integer uid, String today, Integer cnt);

    // 增加博客数量表中该用户今天博客的数量
    @Update("UPDATE blogs_count SET cnt = cnt + 1 WHERE uid = #{uid} AND date = #{today}")
    void addUserTodayBlogCnt(Integer uid, String today);

    // 是否有对应tag
    @Select("SELECT COUNT(id) = 1 FROM tag WHERE name = #{tag}")
    Boolean hasTag(String tag);

    // 更新tag
    @Update("UPDATE tag SET cnt = cnt + 1 WHERE name = #{tag}")
    void addTagCnt(String tag);

    // 插入tag
    @Insert("INSERT INTO tag(name, cnt) VALUES (#{tag}, 1)")
    void insertTag(String tag);

    // 获取tag ID
    @Select("SELECT id FROM tag WHERE #{tag} = name")
    Integer getTagId(String tag);

    // 插入blog_tag_relation表
    @Insert("INSERT INTO blog_tag_relation(tag_id, blog_id, uid) VALUES (#{tagId}, #{blogId}, #{uid})")
    void insertBlogTagsRelation(Integer tagId, Integer blogId, Integer uid);

    // 获取blog id 对应的tags
    @Select("SELECT DISTINCT t.name\n" +
            "FROM blog_tag_relation btr INNER JOIN tag t on btr.tag_id = t.id\n" +
            "WHERE btr.blog_id = #{id} AND t.cnt > 0;")
    String[] getTagsByBlogId(Integer id);

    // 获取某一用户的对应状态的所有blog
    @Select("SELECT id,uid,title,summary,create_time,update_time,submit_time,state, " +
            "CASE WHEN #{needContent} THEN content " +
            "ELSE NULL " +
            "END AS content " +
            "FROM blog WHERE uid = #{uid} AND state = #{blogState}  ")
    List<Blog> getMyBlogs(Integer uid,String blogState, Boolean needContent);

    // 获取所有用户的对应状态的所有blog
    @Select("SELECT id,uid,title,summary,create_time,update_time,submit_time,state, " +
            "CASE WHEN #{needContent} THEN content " +
            "ELSE NULL " +
            "END AS content " +
            "FROM blog WHERE state = #{blogState}")
    List<Blog> getAllBlogs(String blogState, Boolean needContent);

    // 获取blog数量
    @Select("SELECT SUM(cnt) FROM blogs_count WHERE uid = #{uid}")
    Integer getBlogCnt(Integer uid);

    // 获取该blog likes数量
    @Select("SELECT COUNT(id) FROM blog_like_relation WHERE blog_id = #{id}")
    Integer getBlogLikes(Integer id);

    // 获取该blog views数量
    @Select("SELECT COUNT(id) FROM blog_view_relation WHERE blog_id = #{id}")
    Integer getBlogViews(Integer id);

    // 获取所有blog likes数量
    @Select("SELECT COUNT(*) AS likes\n" +
            "FROM blog INNER JOIN blog_like_relation blr on blog.id = blr.blog_id\n" +
            "WHERE blog.uid = #{uid}")
    Integer getBlogsLikes(Integer uid);


    // 获取所有blog views数量
    @Select("SELECT COUNT(*) AS views\n" +
            "FROM blog INNER JOIN blog_view_relation bvr on blog.id = bvr.blog_id\n" +
            "WHERE blog.uid = #{uid}")
    Integer getBlogsViews(Integer uid);

    // 获取用户是否喜欢某个博客
    @Select("SELECT COUNT(id) = 1 FROM blog_like_relation WHERE blog_id = #{id} AND uid = #{uid}")
    Boolean getIsILike(Integer id, Integer uid);

    // 删除blogs_count中的blog数量
    @Update("UPDATE blogs_count SET cnt = cnt - 1 WHERE uid = #{uid} AND date = #{submitday}")
    void deleteBlogCount(Integer uid, String submitday);


    // 减少tag表中对应标签的数量
    @Update("UPDATE tag SET cnt = cnt - 1 WHERE name = #{tagName}")
    void deleteTagCount(String tagName);

    // 删除blog_tag_relation表中对应id
    @Delete("DELETE FROM blog_tag_relation WHERE blog_id = #{blogId}")
    void deleteBlogTagRelation(Integer blogId);

    // 删除用户喜欢博客的记录
    @Delete("DELETE FROM blog_like_relation WHERE blog_id = #{id} AND uid = #{uid}")
    void deleteILike(Integer id, Integer uid);

    // 添加用户喜欢博客的记录
    @Insert("INSERT blog_like_relation(blog_id, uid) VALUES(#{id}, #{uid})")
    void addILike(Integer id, Integer uid);

    // 是否有看过的记录
//    @Select("SELECT COUNT(*) = 1 FROM blog_view_relation WHERE blog_id = #{id} AND uid = #{uid}")
//    Boolean hasView(Integer id, Integer uid);

    // 添加看过记录
    @Insert("INSERT INTO blog_view_relation(blog_id, uid, time) VALUES (#{id}, #{uid}, NOW())")
    void addView(Integer id, Integer uid);

    // 查看该blog-tag是否已经有了
    @Select("SELECT COUNT(*) > 0\n" +
            "FROM blog_tag_relation btr INNER JOIN tag t on btr.tag_id = t.id\n" +
            "WHERE t.name = #{tag} AND btr.blog_id = #{id} AND btr.uid = #{uid}")
    Boolean hasBTR(String tag, Integer id, Integer uid);
}
