package com.xyz.gerenciadorpedidos.messaging;

import com.xyz.gerenciadorpedidos.config.RabbitMQConfig;
import com.xyz.gerenciadorpedidos.service.PedidoService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;


@Component
public class MessageListener implements ChannelAwareMessageListener {

    private final PedidoService pedidoService;

    @Autowired
    public MessageListener(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Override
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME, containerFactory = "rabbitListenerContainerFactory")
    public void onMessage(Message message, Channel channel) throws Exception {
        String messageBody = new String(message.getBody());
        try {

            String[] parts = messageBody.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid message format: " + messageBody);
            }

            Long pedidoId = Long.parseLong(parts[0]);
            String newStatus = parts[1];
            pedidoService.updateStatus(pedidoId, newStatus);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}
