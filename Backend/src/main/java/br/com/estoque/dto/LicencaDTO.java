package br.com.estoque.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LicencaDTO {
    private Long id; // para edição da licença
    private Long empresaId; // se não enviar, é nova empresa
    private String empresaNome; // necessário se criar nova empresa
    private String empresaCnpj;
    private String telefone;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Boolean ativa;
}


