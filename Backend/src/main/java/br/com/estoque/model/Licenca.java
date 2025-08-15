package br.com.estoque.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "TB_LICENCA", schema = "drive_up")
public class Licenca {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "licenca_seq")
    @SequenceGenerator(name = "licenca_seq", sequenceName = "drive_up.TB_LICENCA_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPRESA_ID", nullable = false)
    private Empresa empresa;

    @Column(name = "DATA_INICIO", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "DATA_FIM", nullable = false)
    private LocalDate dataFim;

    @Column(name = "ATIVA", nullable = false)
    private Boolean ativa;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tipo_licenca_id", nullable = false)
    private TipoLicenca tipoLicenca;

    public boolean isValida() {
        LocalDate hoje = LocalDate.now();
        return ativa && (hoje.isEqual(dataInicio) || hoje.isAfter(dataInicio)) && (hoje.isBefore(dataFim) || hoje.isEqual(dataFim));
    }
}

