package com.modsen.driver.integration;

import com.modsen.driver.constants.TestConstants;
import com.modsen.driver.dto.request.UpdateRideDriverRequest;
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
    @Value("${spring.kafka.bootstrap-servers}")
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
    public KafkaConsumer<String, UpdateRideDriverRequest> testKafkaConsumer() {
        return new KafkaConsumer<>(testConsumerConfig());
    }

    private Map<String, Object> testProducerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(TestConstants.KafkaConstants.TRUSTED_PACKAGES_KEY, TestConstants.KafkaConstants.TRUSTED_PACKAGES_VALUE);
        return props;
    }

    @Bean
    public ProducerFactory<String, UpdateRideDriverRequest> updateRideDriverRequestProducerFactory() {
        return new DefaultKafkaProducerFactory<>(testProducerConfigs());
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(testProducerConfigs());
    }

    @Bean
    public KafkaTemplate<String, UpdateRideDriverRequest> updateRideDriverRequestKafkaTemplate() {
        return new KafkaTemplate<>(updateRideDriverRequestProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
