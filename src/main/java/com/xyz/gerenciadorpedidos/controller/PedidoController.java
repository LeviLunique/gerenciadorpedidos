package com.xyz.gerenciadorpedidos.controller;

import com.xyz.gerenciadorpedidos.entity.Pedido;
import com.xyz.gerenciadorpedidos.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public List<Pedido> getAllPedidos() {
        return pedidoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedidoById(@PathVariable Long id) {
        return pedidoService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Pedido> createPedido(@RequestBody Pedido pedido) {
        Pedido createdPedido = pedidoService.save(pedido);
        return ResponseEntity.created(URI.create("/api/pedidos/" + createdPedido.getId()))
                .body(createdPedido);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> updatePedido(@PathVariable Long id, @RequestBody Pedido pedido) {
        Pedido updatedPedido = pedidoService.update(id, pedido);
        return updatedPedido != null
                ? ResponseEntity.ok(updatedPedido)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id) {
        return pedidoService.deleteById(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Pedido> updateStatus(@PathVariable Long id, @RequestBody String status) {
        Pedido updatedPedido = pedidoService.updateStatus(id, status);
        return updatedPedido != null
                ? ResponseEntity.ok(updatedPedido)
                : ResponseEntity.notFound().build();
    }

}
