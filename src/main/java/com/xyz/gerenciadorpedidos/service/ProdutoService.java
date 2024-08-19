package com.xyz.gerenciadorpedidos.service;

import com.xyz.gerenciadorpedidos.entity.Produto;
import com.xyz.gerenciadorpedidos.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private static final Logger logger = LoggerFactory.getLogger(ProdutoService.class);

    private final ProdutoRepository produtoRepository;

    @Autowired
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> findAll() {
        logger.info("Buscando todos os produtos no banco de dados");
        return produtoRepository.findAll();
    }

    public Optional<Produto> findById(Long id) {
        logger.info("Buscando produto com ID: {} no banco de dados", id);
        return produtoRepository.findById(id);
    }

    public Produto save(Produto produto) {
        logger.info("Salvando novo produto no banco de dados: {}", produto);
        Produto savedProduto = produtoRepository.save(produto);
        logger.info("Produto salvo com sucesso: {}", savedProduto);
        return savedProduto;
    }

    public Produto update(Long id, Produto produtoAtualizado) {
        logger.info("Atualizando produto com ID: {}", id);
        return produtoRepository.findById(id).map(produto -> {
            logger.info("Produto encontrado: {}. Atualizando com novos valores: {}", produto, produtoAtualizado);
            produto.setNome(produtoAtualizado.getNome());
            produto.setDescricao(produtoAtualizado.getDescricao());
            produto.setPreco(produtoAtualizado.getPreco());
            Produto updatedProduto = produtoRepository.save(produto);
            logger.info("Produto atualizado com sucesso: {}", updatedProduto);
            return updatedProduto;
        }).orElseThrow(() -> {
            logger.error("Produto com ID: {} não encontrado", id);
            return new RuntimeException("Produto não encontrado");
        });
    }

    public boolean deleteById(Long id) {
        logger.info("Deletando produto com ID: {}", id);
        if (produtoRepository.existsById(id)) {
            produtoRepository.deleteById(id);
            logger.info("Produto com ID: {} deletado com sucesso", id);
            return true;
        } else {
            logger.warn("Produto com ID: {} não encontrado para deleção", id);
            return false;
        }
    }

}
