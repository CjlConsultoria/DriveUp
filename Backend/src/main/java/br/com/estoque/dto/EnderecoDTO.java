package br.com.estoque.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoDTO {
    private String cep;
    private String logradouro;
    private String numero; // Adicione este campo
    private String bairro;
    private String cidade;
    private String estado;
}

