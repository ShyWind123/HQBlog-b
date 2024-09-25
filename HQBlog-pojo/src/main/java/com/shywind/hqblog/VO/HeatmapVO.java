package com.shywind.hqblog.VO;

import com.shywind.hqblog.PO.HeatmapPO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HeatmapVO {
    private List<HeatmapPO> heatmaps;
    private Integer totalCnt;
    private Integer maxCnt;
}
