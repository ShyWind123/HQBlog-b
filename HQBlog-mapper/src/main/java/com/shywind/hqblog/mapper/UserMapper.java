package com.shywind.hqblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shywind.hqblog.Entity.UserInfo;
import com.shywind.hqblog.PO.HeatmapPO;
import com.shywind.hqblog.PO.RankPO;
import com.shywind.hqblog.PO.TagPO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper extends BaseMapper<UserInfo> {
    @Select("SELECT t.id, t.name, COUNT(btr.id) AS cnt\n" +
            "FROM blog_tag_relation btr INNER JOIN tag t ON btr.tag_id = t.id\n" +
            "WHERE #{uid} = btr.uid\n" +
            "GROUP BY t.id")
    List<TagPO> getUserTagsByUid(Integer uid);

    @Select("SELECT bc.id, bc.date, bc.cnt FROM blogs_count bc WHERE #{uid} = bc.uid")
    List<HeatmapPO> getUserHeatmapByUid(Integer uid);

    @Select("SELECT username FROM user WHERE uid = #{uid}")
    String getUsernameByUid(Integer uid);
}
