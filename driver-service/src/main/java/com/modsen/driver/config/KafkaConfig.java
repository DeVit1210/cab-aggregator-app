package com.modsen.driver.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Value("${spring.kafka.producer-topic.name}")
    private String producerTopicName;

    @Bean
    public NewTopic newTopic() {
        return TopicBuilder
                .name(producerTopicName)
                .build();
    }
}
