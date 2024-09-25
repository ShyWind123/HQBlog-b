package com.shywind.hqblog.PO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeatmapPO {
    private Integer id;
    private String date;
    private Integer cnt;
}
