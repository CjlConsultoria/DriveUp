package br.com.estoque.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDTO {
    private Long id;
    private String nome;
    private String cpfCnpj;
    private String email;
    private String telefone;
    private EnderecoDTO endereco; // <- isso aqui precisa estar declarado
}



