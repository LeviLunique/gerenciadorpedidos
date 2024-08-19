package com.xyz.gerenciadorpedidos.controller;

import com.xyz.gerenciadorpedidos.entity.Produto;
import com.xyz.gerenciadorpedidos.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private static final Logger logger = LoggerFactory.getLogger(ProdutoController.class);

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public List<Produto> getAllProdutos() {
        logger.info("Recebendo solicitação para listar todos os produtos");
        return produtoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> getProdutoById(@PathVariable Long id) {
        logger.info("Recebendo solicitação para buscar o produto com ID: {}", id);
        return produtoService.findById(id)
                .map(produto -> {
                    logger.info("Produto encontrado: {}", produto);
                    return ResponseEntity.ok(produto);
                })
                .orElseGet(() -> {
                    logger.warn("Produto com ID: {} não encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<Produto> createProduto(@RequestBody Produto produto) {
        logger.info("Recebendo solicitação para criar um novo produto: {}", produto);
        Produto createdProduto = produtoService.save(produto);
        logger.info("Produto criado com sucesso: {}", createdProduto);
        return ResponseEntity.created(URI.create("/api/produtos/" + createdProduto.getId()))
                .body(createdProduto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> updateProduto(@PathVariable Long id, @RequestBody Produto produto) {
        logger.info("Recebendo solicitação para atualizar o produto com ID: {}", id);
        Produto updatedProduto = produtoService.update(id, produto);
        if (updatedProduto != null) {
            logger.info("Produto atualizado com sucesso: {}", updatedProduto);
            return ResponseEntity.ok(updatedProduto);
        } else {
            logger.warn("Produto com ID: {} não encontrado para atualização", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduto(@PathVariable Long id) {
        logger.info("Recebendo solicitação para deletar o produto com ID: {}", id);
        boolean deleted = produtoService.deleteById(id);
        if (deleted) {
            logger.info("Produto com ID: {} deletado com sucesso", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("Produto com ID: {} não encontrado para deleção", id);
            return ResponseEntity.notFound().build();
        }
    }
}
