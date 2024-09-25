package com.shywind.hqblog.Result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <E> Result<E> success(E data){
        return new Result(1,"success",data);
    }
    public static Result success(String msg){
        System.out.println(msg);
        return new Result(1,msg,null);
    }
    public static <E> Result<E> success(String msg, E data){
        System.out.println(msg);
        return new Result(1,msg,data);
    }
    public static Result success(){
        return new Result(1,"success",null);
    }
    public static Result error(String msg){
        System.out.println(msg);
        return new Result(0,msg,null);
    }


    @Override
    public String toString(){
        return "Result{"+"code="+code+", msg='"+msg+'\''+", data="+data+'}';
    }
}
