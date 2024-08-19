package com.xyz.gerenciadorpedidos.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.CascadeType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.List;

@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "A data do pedido é obrigatória.")
    private Date dataPedido;

    @NotNull(message = "O status do pedido é obrigatório.")
    @Size(min = 3, max = 20, message = "O status deve ter entre 3 e 20 caracteres.")
    private String status;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotEmpty(message = "O pedido deve conter pelo menos um item.")
    private List<ItemPedido> itens;

    public Pedido () {
    };

    public Pedido(Date dataPedido, String status, List<ItemPedido> itens){
        this.dataPedido = dataPedido;
        this.status = status;
        this.itens = itens;
    };

    public Long getId() {
        return id;
    };

    public void setId(Long id) {
        this.id = id;
    };

    public Date getDataPedido() {
        return dataPedido;
    };

    public void setDataPedido(@NotNull Date dataPedido) {
        this.dataPedido = dataPedido;
    }

    public String getStatus() {
        return status;
    };

    public void setStatus(String status) {
        this.status = status;
    };

    public List<ItemPedido> getItens() {
        return itens;
    };

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
    };

    public void addItem(ItemPedido item) {
        this.itens.add(item);
        item.setPedido(this);
    };

    public void removeItem(ItemPedido item) {
        this.itens.remove(item);
        item.setPedido(null);
    };
}
