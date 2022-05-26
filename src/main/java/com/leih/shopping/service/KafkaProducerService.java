package com.leih.shopping.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    public void sendMessage(String topic, String body){
        kafkaTemplate.send(topic,body);
    }
}
