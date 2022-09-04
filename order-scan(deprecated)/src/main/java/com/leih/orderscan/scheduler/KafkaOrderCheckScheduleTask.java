package com.leih.orderscan.scheduler;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Configuration
public class KafkaOrderCheckScheduleTask implements ApplicationRunner {
    private volatile Boolean exit = false;
    private final Object lock = new Object();
    @Value("${spring.kafka.bootstrap-servers}")
    private String servers;
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;
    private KafkaConsumer<String,String> consumer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //initialize consumer
        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "check_order");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "5000");
        consumer = new KafkaConsumer<>(props, new StringDeserializer(), new StringDeserializer());

        String topic = "checkOrder_15_minutes";
        List<String> topics = Collections.singletonList(topic);
        consumer.subscribe(topics);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (lock) {
                    consumer.resume(consumer.paused());
                    lock.notify();
                }
            }
            //1 second per check
        }, 0, 1000);
        do {
            synchronized (lock) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(200));
                if (consumerRecords.isEmpty()) {
                    lock.wait();
                    continue;
                }

                boolean timed = false;
                for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                    long timestamp = consumerRecord.timestamp();
                    TopicPartition topicPartition = new TopicPartition(consumerRecord.topic(), consumerRecord.partition());
                    //15 minutes check
                    if (timestamp + 60 * 1000 *15 < System.currentTimeMillis()) {
                        String value = consumerRecord.value();
                        //when it comes to 15 minutes, send new messages
                        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("checkOrder_15_minutes_final", value);
                        try {
                            kafkaTemplate.send(producerRecord).get();
                            // success. commit message, move offset
                            OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(consumerRecord.offset() + 1);
                            HashMap<TopicPartition, OffsetAndMetadata> metadataHashMap = new HashMap<>();
                            metadataHashMap.put(topicPartition, offsetAndMetadata);
                            consumer.commitSync(metadataHashMap);
                        } catch (ExecutionException e) {
                            consumer.pause(Collections.singletonList(topicPartition));
                            consumer.seek(topicPartition, consumerRecord.offset());
                            timed = true;
                            break;
                        }
                    } else {
                        consumer.pause(Collections.singletonList(topicPartition));
                        consumer.seek(topicPartition, consumerRecord.offset());
                        timed = true;
                        break;
                    }
                }
                if (timed) {
                    lock.wait();
                }
            }
        } while (!exit);
    }
}
