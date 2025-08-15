package br.com.estoque.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class LicencaDetalhadaDTO {
    private Long id;
    private Long empresaId;
    private String empresaNome;
    private String empresaCnpj;
    private String empresaTelefone;


    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Boolean ativa;

    private Long tipoLicencaId; // ID da tabela TipoLicenca
    private String tipoLicencaNome; // Nome legível da licença
    private Integer limiteUsuarios;

    private UUID responsavelId;
    private String responsavelNome;
    private String responsavelCpf;
    private String perfil;

    private Boolean validadeExpirada;
    private Long diasRestantes;

    private int qtdUsuarios; // NOVO CAMPO: total de usuários cadastrados na empresa
}
