package com.xyz.gerenciadorpedidos.service;

import com.xyz.gerenciadorpedidos.entity.Produto;
import com.xyz.gerenciadorpedidos.repository.ProdutoRepository;
import com.xyz.gerenciadorpedidos.entity.Pedido;
import com.xyz.gerenciadorpedidos.entity.ItemPedido;
import com.xyz.gerenciadorpedidos.repository.PedidoRepository;
import com.xyz.gerenciadorpedidos.messaging.MessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final MessagePublisher messagePublisher;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository, MessagePublisher messagePublisher) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.messagePublisher = messagePublisher;
    }

    public List<Pedido> findAll() {
        logger.info("Buscando todos os pedidos.");
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> findById(Long id) {
        logger.info("Buscando pedido com ID: {}", id);
        return pedidoRepository.findById(id);
    }

    public Pedido save(Pedido pedido) {
        logger.info("Salvando novo pedido: {}", pedido);
        for (ItemPedido item : pedido.getItens()) {
            logger.debug("Processando item do pedido: {}", item);
            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> {
                        logger.error("Produto não encontrado com id {}", item.getProduto().getId());
                        return new RuntimeException("Produto não encontrado com id " + item.getProduto().getId());
                    });
            item.setProduto(produto);
            item.setPreco(produto.getPreco());
            item.setPedido(pedido);
        }
        Pedido savedPedido = pedidoRepository.save(pedido);
        logger.info("Pedido salvo com sucesso: {}", savedPedido);
        return savedPedido;
    }

    public Pedido update(Long id, Pedido pedidoAtualizado) {
        logger.info("Atualizando pedido com ID: {}", id);
        return pedidoRepository.findById(id).map(pedido -> {
            pedido.setDataPedido(pedidoAtualizado.getDataPedido());
            pedido.setStatus(pedidoAtualizado.getStatus());
            pedido.getItens().clear();
            pedido.getItens().addAll(pedidoAtualizado.getItens());
            for (ItemPedido item : pedido.getItens()) {
                item.setPedido(pedido);
            }
            Pedido updatedPedido = pedidoRepository.save(pedido);
            logger.info("Pedido atualizado com sucesso: {}", updatedPedido);
            return updatedPedido;
        }).orElseThrow(() -> {
            logger.error("Pedido com ID: {} não encontrado.", id);
            return new RuntimeException("Pedido não encontrado");
        });
    }

    public boolean deleteById(Long id) {
        logger.info("Deletando pedido com ID: {}", id);
        if (pedidoRepository.existsById(id)) {
            pedidoRepository.deleteById(id);
            logger.info("Pedido com ID: {} deletado com sucesso.", id);
            return true;
        } else {
            logger.warn("Pedido com ID: {} não encontrado para deletar.", id);
            return false;
        }
    }

    public Pedido updateStatus(Long id, String newStatus) {
        logger.info("Atualizando status do pedido com ID: {} para: {}", id, newStatus);
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> {
            logger.error("Pedido com ID: {} não encontrado.", id);
            return new RuntimeException("Pedido não encontrado");
        });
        pedido.setStatus(newStatus);
        Pedido updatedPedido = pedidoRepository.save(pedido);
        logger.info("Status do pedido com ID: {} atualizado para: {}", id, newStatus);
        messagePublisher.publish(id + ":" + newStatus);
        return updatedPedido;
    }
}
