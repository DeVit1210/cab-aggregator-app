package com.modsen.ride.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Value("${spring.kafka.ride-producer-topic.name}")
    private String rideProducerTopicName;
    @Value("${spring.kafka.status-producer-topic.name}")
    private String driverStatusProducerTopicName;

    @Bean
    public NewTopic rideProducerTopic() {
        return TopicBuilder
                .name(rideProducerTopicName)
                .build();
    }

    @Bean
    public NewTopic driverStatusProducerTopicName() {
        return TopicBuilder
                .name(driverStatusProducerTopicName)
                .build();
    }
}
