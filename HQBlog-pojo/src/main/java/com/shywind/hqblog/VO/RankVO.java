package com.shywind.hqblog.VO;

import com.shywind.hqblog.PO.RankPO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankVO {
    Integer uid;
    String username;
    Integer blogs;
    Integer likes;
    Integer views;

    public RankVO(RankPO rankPO){
        uid = rankPO.getUid();
        username = rankPO.getUsername();
    }
}
