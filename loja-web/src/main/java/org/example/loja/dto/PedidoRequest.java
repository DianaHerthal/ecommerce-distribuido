package org.example.loja.dto;

import java.util.List;

public class PedidoRequest {
    private String nomeCliente;
    private String emailCliente;
    private String cep;
    private String metodoPagamento;
    private List<ItemPedido> itens;

    public static class ItemPedido {
        private Long id;
        private String nome;
        private Double preco;
        private Integer quantidade;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public Double getPreco() { return preco; }
        public void setPreco(Double preco) { this.preco = preco; }
        public Integer getQuantidade() { return quantidade; }
        public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }
    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }
    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }
}