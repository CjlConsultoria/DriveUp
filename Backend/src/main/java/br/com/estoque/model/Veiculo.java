package br.com.estoque.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Veiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String modelo;
    private String placa;
    private String marca;
    private Integer ano;
    private String cor;
    private Integer quilometragem;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
}


