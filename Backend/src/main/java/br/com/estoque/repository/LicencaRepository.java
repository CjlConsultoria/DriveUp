package br.com.estoque.repository;

import br.com.estoque.model.Empresa;
import br.com.estoque.model.Licenca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.Optional;

public interface LicencaRepository extends JpaRepository<Licenca, Long>, JpaSpecificationExecutor<Licenca> {
    Optional<Licenca> findByEmpresaId(Long empresaId);

    Optional<Object> findByEmpresa(Empresa empresa);

    Optional<Licenca> findTopByEmpresaIdAndAtivaTrueOrderByDataFimDesc(Long empresaId);

    Optional<Licenca> findByEmpresaCnpj(String empresaCnpj);

    Collection<Licenca> findByEmpresaIdAndAtivaTrue(Long empresaId);
}


