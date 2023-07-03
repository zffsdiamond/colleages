package com.zhengqing.demo.config;

import cn.hutool.json.JSONUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractJackson2MessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * <p> 自定义消息转换器 </p>
 *
 * @author zhengqingya
 * @description 可参考rabbitmq默认的消息转换器 {@link org.springframework.amqp.support.converter.SimpleMessageConverter}
 * @date 2023/7/3 15:28
 */
public class CustomMessageConverter implements MessageConverter {

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) {
        // 生产者发送消息转换
        String msg = "";
        if (object instanceof String) {
            msg = String.valueOf(object);
        } else {
            msg = JSONUtil.toJsonStr(object);
        }
        return new Message(msg.getBytes(), messageProperties);
    }

    @Override
    public Object fromMessage(Message message) {
        try {
            String msg = new String(message.getBody(), "UTF-8");
            String targetClassName = message.getMessageProperties().getInferredArgumentType().getTypeName();
            if (String.class.getName().equals(targetClassName)) {
                return msg;
            } else {
                // 返回反序列化后的对象
//                Class<?> targetClass = Class.forName(targetClassName);
//                return JSONUtil.toBean(msg, targetClass);
                /**
                 * 消费者配置使用 Jackson2JsonMessageConverter 转换器  =》 tips：可以有效解决由于实体类字段变更或类名修改等原因导致消费者序列化问题，很nice！！！
                 * {@link AbstractJackson2MessageConverter#fromMessage(Message, Object)}
                 */
                return new Jackson2JsonMessageConverter().fromMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
