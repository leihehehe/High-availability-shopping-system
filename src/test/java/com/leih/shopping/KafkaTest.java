package com.leih.shopping;

import com.leih.shopping.service.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
public class KafkaTest {
    @Autowired
    KafkaProducerService kafkaProducerService;
    @Test
    public void sendMessageToTopic(){
        kafkaProducerService.sendMessage("order","test");
    }
}
