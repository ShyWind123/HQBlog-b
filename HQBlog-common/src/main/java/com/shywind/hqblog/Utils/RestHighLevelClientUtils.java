package com.shywind.hqblog.Utils;

import com.alibaba.fastjson.JSON;
import com.shywind.hqblog.Entity.Blog;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestHighLevelClientUtils {
    private RestHighLevelClient client;

    public RestHighLevelClientUtils(){
        client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://8.134.215.31:9200")
        ));
    }

    public void postBlog(Blog blog) {
        // 将blog中的content解压
        blog.setContent(blog.getContent());

        // 1.创建Request
        BulkRequest request = new BulkRequest();

        // 2.创建新增文档的Request对象
        request.add(new IndexRequest("blog")
                .id(blog.getId().toString())
                .source(JSON.toJSONString(blog), XContentType.JSON));

        // 3.发送请求
        try{
            client.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void deleteBlog(Integer id) {
        // 1.创建Request
        BulkRequest request = new BulkRequest();

        // 2.删除
        request.add(new DeleteRequest("blog")
                .id(id.toString()));

        // 3.发送请求
        try{
            client.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public List<Blog> searchBlogs(String text){
        // 结果列表
        List<Blog> list = new ArrayList<>();

        // 1.准备Request
        SearchRequest request = new SearchRequest("blog");
        // 2.准备DSL
        request.source()
                .query(QueryBuilders.multiMatchQuery(text,"title", "summary", "content"));

        try{
            // 3.发送请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            // 4.解析响应
            SearchHits searchHits = response.getHits();
            // 4.1.获取总条数
            long total = searchHits.getTotalHits().value;
            System.out.println("共搜索到" + total + "条数据");
            // 4.2.文档数组
            SearchHit[] hits = searchHits.getHits();
            // 4.3.遍历
            for (SearchHit hit : hits) {
                // 获取文档source
                String json = hit.getSourceAsString();
                // 反序列化
                Blog blog = JSON.parseObject(json, com.shywind.hqblog.Entity.Blog.class);
                list.add(blog);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return list;
    }

    public void close() throws IOException {
        client.close();
    }

//    @Override
//    protected void finalize() throws IOException {
//        client.close();
//    }
}
