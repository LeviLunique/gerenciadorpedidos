package com.xyz.gerenciadorpedidos.service;

import com.xyz.gerenciadorpedidos.entity.Produto;
import com.xyz.gerenciadorpedidos.repository.ProdutoRepository;
import com.xyz.gerenciadorpedidos.entity.Pedido;
import com.xyz.gerenciadorpedidos.entity.ItemPedido;
import com.xyz.gerenciadorpedidos.repository.PedidoRepository;
import com.xyz.gerenciadorpedidos.messaging.MessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

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
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> findById(Long id) {
        return pedidoRepository.findById(id);
    }

    public Pedido save(Pedido pedido) {
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> {
                        return new RuntimeException("Produto não encontrado com id " + item.getProduto().getId());
                    });
            item.setProduto(produto);
            item.setPreco(produto.getPreco());
            item.setPedido(pedido);
        }
        Pedido savedPedido = pedidoRepository.save(pedido);
        return savedPedido;
    }

    public Pedido update(Long id, Pedido pedidoAtualizado) {
        return pedidoRepository.findById(id).map(pedido -> {
            pedido.setDataPedido(pedidoAtualizado.getDataPedido());
            pedido.setStatus(pedidoAtualizado.getStatus());
            pedido.getItens().clear();
            pedido.getItens().addAll(pedidoAtualizado.getItens());
            for (ItemPedido item : pedido.getItens()) {
                item.setPedido(pedido);
            }
            Pedido updatedPedido = pedidoRepository.save(pedido);
            return updatedPedido;
        }).orElseThrow(() -> {
            return new RuntimeException("Pedido não encontrado");
        });
    }

    public boolean deleteById(Long id) {
        if (pedidoRepository.existsById(id)) {
            pedidoRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public Pedido updateStatus(Long id, String newStatus) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> {
            return new RuntimeException("Pedido não encontrado");
        });
        pedido.setStatus(newStatus);
        Pedido updatedPedido = pedidoRepository.save(pedido);
        messagePublisher.publish(id + ":" + newStatus);
        return updatedPedido;
    }
}
