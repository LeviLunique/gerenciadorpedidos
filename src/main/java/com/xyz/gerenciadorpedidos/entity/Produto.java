package com.xyz.gerenciadorpedidos.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O nome do produto é obrigatório")
    @Size(min = 1, max = 100, message = "O nome do produto deve ter entre 1 e 100 caracteres.")
    private String nome;

    @Size(max = 255, message = "A descrição do produto não pode exceder 255 caracteres.")
    private String descricao;

    @NotNull(message = "O preço do produto é obrigatório.")
    @Positive(message = "O preço do produto deve ser um valor positivo.")
    private Double preco;

    public Produto() {
    };

    public Produto(String nome, String descricao, Double preco) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
    };

    public Long getId() {
      return id;
    };

    public void setId(Long id) {
      this.id = id;
    };

    public String getNome() {
      return nome;
    };

    public void setNome(String nome) {
      this.nome = nome;
    };

    public String getDescricao() {
      return descricao;
    };

    public void setDescricao(String descricao) {
      this.descricao = descricao;
    };

    public Double getPreco() {
      return preco;
    };

    public void setPreco(Double preco) {
      this.preco = preco;
    };
}
