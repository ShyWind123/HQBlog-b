package com.shywind.hqblog.PO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagPO {
    private Integer id;
    private String name;
    private Integer cnt;
}
