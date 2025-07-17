package br.com.estoque.dto;

import lombok.Data;


@Data
public class VeiculoDTO {
    private Long id;
    private String modelo;
    private String placa;
    private String marca;
    private Integer ano;
    private String cor;
    private Integer quilometragem;

    private Long clienteId;
    private String clienteNome;
    private String clienteCpfCnpj;
    private String clienteEmail;
    private String clienteTelefone;

    public VeiculoDTO() {}

    public VeiculoDTO(Long id, String modelo, String placa, String marca, Integer ano, String cor, Integer quilometragem,
                      Long clienteId, String clienteNome, String clienteCpfCnpj, String clienteEmail, String clienteTelefone) {
        this.id = id;
        this.modelo = modelo;
        this.placa = placa;
        this.marca = marca;
        this.ano = ano;
        this.cor = cor;
        this.quilometragem = quilometragem;
        this.clienteId = clienteId;
        this.clienteNome = clienteNome;
        this.clienteCpfCnpj = clienteCpfCnpj;
        this.clienteEmail = clienteEmail;
        this.clienteTelefone = clienteTelefone;

    }
}

