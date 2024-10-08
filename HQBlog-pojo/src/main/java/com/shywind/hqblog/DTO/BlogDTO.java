package com.shywind.hqblog.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogDTO {
    private Integer uid;
    private Integer id;
    private String title;
    private String summary;
    private String content;
    private String[] tags;
    private String state;
}
