package br.com.estoque.dto;

import br.com.estoque.model.Usuario;
import br.com.estoque.model.enums.Role;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UsuarioResponse {

    private UUID id;
    private String nome;
    private String email;
    private Role role;

    public UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.role = usuario.getRole();
    }
}

