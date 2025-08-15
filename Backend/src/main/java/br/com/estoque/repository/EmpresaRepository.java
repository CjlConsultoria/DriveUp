package br.com.estoque.repository;

import br.com.estoque.model.Empresa;
import br.com.estoque.model.enums.EmpresaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long>, JpaSpecificationExecutor<Empresa> {
    Optional<Empresa> findByName(String name);
    Optional<Empresa> findByName(EmpresaType name);
    Empresa findByCodigoPublico(String codigoPublico);

    boolean existsByCnpj(String cnpj);

    Optional<Object> findById(UUID empresaId);
}
