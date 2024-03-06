package com.modsen.ride.integration;

import com.modsen.ride.constants.TestConstants;
import com.modsen.ride.dto.request.ChangeDriverStatusRequest;
import com.modsen.ride.dto.request.FindDriverRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TestKafkaConfig {
    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    private Map<String, Object> testConsumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        props.put(TestConstants.KafkaConstants.TRUSTED_PACKAGES_KEY, TestConstants.KafkaConstants.TRUSTED_PACKAGES_VALUE);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, TestConstants.KafkaConstants.AUTO_COMMIT_RESET);
        return props;
    }

    @Bean
    public KafkaConsumer<String, Object> testKafkaConsumer() {
        return new KafkaConsumer<>(testConsumerConfig());
    }

    @Bean
    public ProducerFactory<String, FindDriverRequest> findDriverRequestProducerFactory() {
        return new DefaultKafkaProducerFactory<>(testProducerConfigs());
    }

    @Bean
    public ProducerFactory<String, ChangeDriverStatusRequest> changeDriverStatusRequestProducerFactory() {
        return new DefaultKafkaProducerFactory<>(testProducerConfigs());
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(testConsumerConfig());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, FindDriverRequest> findDriverRequestKafkaTemplate() {
        return new KafkaTemplate<>(findDriverRequestProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, ChangeDriverStatusRequest> changeDriverStatusRequestKafkaTemplate() {
        return new KafkaTemplate<>(changeDriverStatusRequestProducerFactory());
    }

    private Map<String, Object> testProducerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(TestConstants.KafkaConstants.TRUSTED_PACKAGES_KEY, TestConstants.KafkaConstants.TRUSTED_PACKAGES_VALUE);
        return props;
    }
}
