package com.shywind.hqblog.Consumer;

import com.rabbitmq.client.Channel;
import com.shywind.hqblog.service.BlogService;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SaveHomeBlogListToRedisConsumer {
    @Autowired
    private BlogService blogService;

    @RabbitListener(bindings =
    @QueueBinding(
            value=@Queue,
            exchange = @Exchange(value = "saveHomeBlogListToRedis", type="fanout")
    ),
            ackMode = "MANUAL")
    public void saveHomeBlogListToRedis(String blogListStr, @Header(AmqpHeaders.DELIVERY_TAG) long deliverTag, Channel channel) throws IOException, InterruptedException {
        try {
            blogService.saveHomeBlogListToRedis(blogListStr);
        } catch (Exception e) {
            System.out.println("存入redis失败:" + e);
            System.out.println(e);
        }

        channel.basicAck(deliverTag, true);
    }
}
