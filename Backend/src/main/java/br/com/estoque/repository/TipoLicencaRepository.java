package br.com.estoque.repository;

import br.com.estoque.model.TipoLicenca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface TipoLicencaRepository extends JpaRepository<TipoLicenca, Long> {
    Optional<TipoLicenca> findByNome(TipoLicenca nome);
}
