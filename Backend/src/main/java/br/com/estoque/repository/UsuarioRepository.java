package br.com.estoque.repository;

import br.com.estoque.model.Empresa;
import br.com.estoque.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByCpf(String cpf);
    long countByEmpresa(Empresa empresa);
    List<Usuario> findByEmpresaId(Long empresaId);

    boolean existsByEmpresaId(Long id);

    long countByEmpresaIdAndAtivoTrue(Long empresaId);

    Optional<Usuario> findByCpfAndEmpresa(String cpf, Empresa empresa);

    Optional<Usuario> findByCpfAndEmpresaId(String cpf, Long empresaId);

    Optional<Usuario> findById(Long id);
}

