package com.xyz.gerenciadorpedidos.messaging;

import com.xyz.gerenciadorpedidos.config.RabbitMQConfig;
import com.xyz.gerenciadorpedidos.service.PedidoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    private final PedidoService pedidoService;

    @Autowired
    public MessageListener(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receive(String message) {
        String[] parts = message.split(" ");
        Long pedidoId = Long.parseLong(parts[0]);
        String newStatus = parts[1];
        pedidoService.updateStatus(pedidoId, newStatus);
    }
}
