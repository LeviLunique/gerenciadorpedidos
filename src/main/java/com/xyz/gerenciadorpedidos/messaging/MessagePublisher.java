package com.xyz.gerenciadorpedidos.messaging;

import com.xyz.gerenciadorpedidos.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessagePublisher {

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
        } catch (Exception e) {
        }
    }

}
