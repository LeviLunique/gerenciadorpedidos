package com.xyz.gerenciadorpedidos.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.persistence.Column;

@Entity
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonIgnore
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @NotNull(message = "A quantidade é obrigatória.")
    @Positive(message = "A quantidade deve ser um valor positivo.")
    private Integer quantidade;

    @NotNull(message = "O preço é obrigatório.")
    @Positive(message = "O preço deve ser um valor positivo.")
    @Column(nullable = false)
    private Double preco;

    public ItemPedido() {
    };

    public ItemPedido(Produto produto, Pedido pedido, Integer quantidade) {
        this.produto = produto;
        this.pedido = pedido;
        this.quantidade = quantidade;
        this.preco = produto.getPreco();
    };

    public Long getId() {
      return id;
    };

    public void setId(Long id) {
      this.id = id;
    };

    public Pedido getPedido() {
      return pedido;
    };

    public void setPedido(Pedido pedido) {
      this.pedido = pedido;
    };

    public Produto getProduto() {
        return produto;
    };

    public void setProduto(Produto produto) {
        this.produto = produto;
    };

    public Integer getQuantidade() {
        return quantidade;
    };

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    };

    public Double getPreco() {
        return preco;
    };

    public void setPreco(Double preco) {
        this.preco = preco;
    };

}
