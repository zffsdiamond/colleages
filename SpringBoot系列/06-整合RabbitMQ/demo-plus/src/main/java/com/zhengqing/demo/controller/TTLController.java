package com.zhengqing.demo.controller;

import com.zhengqing.demo.rabbitmq.ttl.msg.TtlMsgProducer;
import com.zhengqing.demo.rabbitmq.ttl.queue.TtlQueueProducer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "测试mq-延时消息|队列")
@RestController
@RequestMapping("/api/mq")
@RequiredArgsConstructor
public class TTLController {

    private final TtlMsgProducer ttlMsgProducer;

    private final TtlQueueProducer ttlQueueProducer;

    @ApiOperation("ttlMsg")
    @PostMapping("ttlMsg")
    public String ttlMsg() {
        this.ttlMsgProducer.send();
        return "SUCCESS";
    }

    @ApiOperation("ttlQueue")
    @PostMapping("ttlQueue")
    public String ttlQueue() {
        this.ttlQueueProducer.send();
        return "SUCCESS";
    }

}
