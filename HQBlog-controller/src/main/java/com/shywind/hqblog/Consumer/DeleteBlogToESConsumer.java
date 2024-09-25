package com.shywind.hqblog.Consumer;

import com.rabbitmq.client.Channel;
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
public class DeleteBlogToESConsumer {
    @RabbitListener(bindings =
    @QueueBinding(
            value=@Queue,
            exchange = @Exchange(value = "ESDeleteBlog", type="fanout")
    ),
            ackMode = "MANUAL")
    public void esDeleteBlog(Integer id, @Header(AmqpHeaders.DELIVERY_TAG) long deliverTag, Channel channel) throws IOException, InterruptedException {
        try{
            RestHighLevelClientUtils clientUtils = new RestHighLevelClientUtils();
            clientUtils.deleteBlog(id);
            clientUtils.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        channel.basicAck(deliverTag, true);
    }
}
