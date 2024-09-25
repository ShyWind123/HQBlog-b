package com.shywind.hqblog.Consumer;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.shywind.hqblog.VO.EmailCodeVO;
import com.shywind.hqblog.service.LoginService;
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
public class SendCodeConsumer {
    @Autowired
    private LoginService loginService;

    @RabbitListener(bindings =
    @QueueBinding(
            value=@Queue,
            exchange = @Exchange(value = "sendEmailCodeAndSaveToRedis", type="fanout")
    ),
            ackMode = "MANUAL")
    public void sendCode(String emailCodeStr, @Header(AmqpHeaders.DELIVERY_TAG) long deliverTag, Channel channel) throws IOException, InterruptedException {
        EmailCodeVO emailCode = JSON.parseObject(emailCodeStr, EmailCodeVO.class);
        try {
            loginService.sendCode(emailCode.getEmail(), emailCode.getCode());
        } catch (Exception e) {
            //有异常 直接返回
            System.out.println("验证码邮件发送失败！");
            System.out.println(e);
        }

        channel.basicAck(deliverTag, true);
    }
}
