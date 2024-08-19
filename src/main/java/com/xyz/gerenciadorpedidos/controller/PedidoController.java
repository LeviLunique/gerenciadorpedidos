package com.xyz.gerenciadorpedidos.controller;

import com.xyz.gerenciadorpedidos.entity.Pedido;
import com.xyz.gerenciadorpedidos.entity.ItemPedido;
import com.xyz.gerenciadorpedidos.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public List<Pedido> getAllPedidos() {
        logger.info("Recebendo solicitação para obter todos os pedidos.");
        return pedidoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedidoById(@PathVariable Long id) {
        logger.info("Recebendo solicitação para obter pedido com ID: {}", id);
        return pedidoService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("Pedido com ID: {} não encontrado.", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<Pedido> createPedido(@Valid @RequestBody Pedido pedido) {
        logger.info("Recebendo solicitação para criar um novo pedido: {}", pedido);
        for (ItemPedido item : pedido.getItens()) {
            if (item.getProduto() == null || item.getProduto().getId() == null) {
                logger.error("Produto inválido nos itens do pedido: {}", item);
                throw new RuntimeException("Produto inválido nos itens do pedido.");
            }
        }
        Pedido createdPedido = pedidoService.save(pedido);
        logger.info("Pedido criado com sucesso: {}", createdPedido);
        return ResponseEntity.created(URI.create("/api/pedidos/" + createdPedido.getId()))
                .body(createdPedido);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> updatePedido(@PathVariable Long id, @Valid @RequestBody Pedido pedido) {
        logger.info("Recebendo solicitação para atualizar pedido com ID: {}", id);
        Pedido updatedPedido = pedidoService.update(id, pedido);
        return updatedPedido != null
                ? ResponseEntity.ok(updatedPedido)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id) {
        logger.info("Recebendo solicitação para deletar pedido com ID: {}", id);
        return pedidoService.deleteById(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Pedido> updateStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest statusUpdateRequest) {
        logger.info("Recebendo solicitação para atualizar o status do pedido com ID: {} para: {}", id, statusUpdateRequest.getStatus());
        Pedido updatedPedido = pedidoService.updateStatus(id, statusUpdateRequest.getStatus());
        return updatedPedido != null
                ? ResponseEntity.ok(updatedPedido)
                : ResponseEntity.notFound().build();
    }

    public static class StatusUpdateRequest {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

}
