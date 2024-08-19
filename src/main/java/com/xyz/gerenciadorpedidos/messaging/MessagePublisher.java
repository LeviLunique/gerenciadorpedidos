package com.xyz.gerenciadorpedidos.messaging;

import com.xyz.gerenciadorpedidos.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MessagePublisher {

    private static final Logger logger = LoggerFactory.getLogger(MessagePublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String queueName;

    @Autowired
    public MessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = RabbitMQConfig.QUEUE_NAME;
    }

    public void publish(String message) {
        try {
            rabbitTemplate.convertAndSend(queueName, message);
            logger.info("Message published to queue {}: {}", queueName, message);
        } catch (Exception e) {
            logger.error("Failed to publish message to queue {}: {}", queueName, message, e);
        }
    }

}
