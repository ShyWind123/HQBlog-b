package com.shywind.hqblog.Consumer;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.shywind.hqblog.Entity.Blog;
import com.shywind.hqblog.Utils.RestHighLevelClientUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class InsertBlogToESConsumer {
    @RabbitListener(bindings =
    @QueueBinding(
            value=@Queue,
            exchange = @Exchange(value = "ESInsertBlog", type="fanout")
    ),
            ackMode = "MANUAL")
    public void esInsertBlog(String blogStr, @Header(AmqpHeaders.DELIVERY_TAG) long deliverTag, Channel channel) throws IOException, InterruptedException {
        Blog blog = JSON.parseObject(blogStr, Blog.class);

        blog.removeContentPic();
        try {
            RestHighLevelClientUtils clientUtils = new RestHighLevelClientUtils();
            clientUtils.postBlog(blog);
            clientUtils.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        channel.basicAck(deliverTag, true);
    }
}
