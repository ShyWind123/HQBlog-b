package com.shywind.hqblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shywind.hqblog.PO.RankPO;
import com.shywind.hqblog.PO.TagPO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface GlobalMapper {
    @Select("SELECT b.uid AS uid, u.username as username\n" +
            "FROM blog b INNER JOIN user u ON b.uid = u.uid\n" +
            "GROUP BY b.uid\n")
//            "ORDER BY blogs DESC\n" +
//            "LIMIT 5")
    List<RankPO> getRanks();

    @Select("SELECT * FROM tag WHERE tag.cnt > 0")
    List<TagPO> getTags();
}
