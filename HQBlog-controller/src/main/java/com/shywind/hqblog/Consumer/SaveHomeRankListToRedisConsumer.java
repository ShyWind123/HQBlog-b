package com.shywind.hqblog.Consumer;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.shywind.hqblog.VO.EmailCodeVO;
import com.shywind.hqblog.service.GlobalService;
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
public class SaveHomeRankListToRedisConsumer {
    @Autowired
    private GlobalService globalService;

    @RabbitListener(bindings =
    @QueueBinding(
            value=@Queue,
            exchange = @Exchange(value = "saveHomeRankListToRedis", type="fanout")
    ),
            ackMode = "MANUAL")
    public void saveHomeRankListToRedis(String rankListStr, @Header(AmqpHeaders.DELIVERY_TAG) long deliverTag, Channel channel) throws IOException, InterruptedException {
        try {
            globalService.saveHomeRankListToRedis(rankListStr);
        } catch (Exception e) {
            System.out.println("存入redis失败:" + e);
            System.out.println(e);
        }

        channel.basicAck(deliverTag, true);
    }
}
