package br.com.estoque.service;

import br.com.estoque.dto.LicencaDTO;
import br.com.estoque.dto.LicencaDetalhadaDTO;
import br.com.estoque.dto.LicencaStatusDTO;
import br.com.estoque.model.Empresa;
import br.com.estoque.model.Licenca;
import br.com.estoque.model.TipoLicenca;
import br.com.estoque.model.Usuario;
import br.com.estoque.model.specification.LicencaSpecification;
import br.com.estoque.repository.EmpresaRepository;
import br.com.estoque.repository.LicencaRepository;
import br.com.estoque.repository.TipoLicencaRepository;
import br.com.estoque.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class LicencaService {

    @Autowired
    private LicencaRepository licencaRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private UsuarioRepository userRepository;

    @Autowired
    private TipoLicencaRepository tipoLicencaRepository;

    public LicencaDTO criarOuAtualizar(LicencaDTO dto) {
        Empresa empresa;

        if (dto.getEmpresaId() != null) {
            empresa = empresaRepository.findById(dto.getEmpresaId())
                    .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

            // Atualiza os dados da empresa
            empresa.setName(dto.getEmpresaNome());
            empresa.setCnpj(dto.getEmpresaCnpj());
            empresa.setTelefone(dto.getTelefone());
            empresaRepository.save(empresa);

        } else {
            // Se não existe empresaId, cria nova empresa
            empresa = new Empresa();
            empresa.setName(dto.getEmpresaNome() != null ? dto.getEmpresaNome() : "");
            empresa.setCnpj(dto.getEmpresaCnpj() != null ? dto.getEmpresaCnpj() : "");
            empresa.setTelefone(dto.getTelefone() != null ? dto.getTelefone() : "");
            empresa = empresaRepository.save(empresa);
            dto.setEmpresaId(empresa.getId()); // retorna o id da nova empresa
        }

        // Busca o TipoLicenca
        TipoLicenca tipoLicenca = tipoLicencaRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Tipo de licença não encontrado"));

        if (dto.getDataInicio() == null || dto.getDataFim() == null) {
            throw new RuntimeException("Datas de início e fim são obrigatórias");
        }
        if (dto.getDataFim().isBefore(dto.getDataInicio())) {
            throw new RuntimeException("Data final não pode ser antes da data inicial");
        }

        Optional<Licenca> licencaExistente = licencaRepository.findByEmpresaCnpj(dto.getEmpresaCnpj());
        Licenca licenca = licencaExistente.orElse(new Licenca());
        licenca.setEmpresa(empresa);
        licenca.setTipoLicenca(tipoLicenca);
        licenca.setDataInicio(dto.getDataInicio());
        licenca.setDataFim(dto.getDataFim());
        licenca.setAtiva(dto.getAtiva() != null ? dto.getAtiva() : true);

        licenca = licencaRepository.save(licenca);
        dto.setId(licenca.getId());
        return dto;
    }


    public void desativarLicenca(Long id) {
        Licenca licenca = licencaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Licença não encontrada"));
        licenca.setAtiva(false);
        licencaRepository.save(licenca);
    }

    public boolean isLicencaValida(Long empresaId) {
        return licencaRepository.findTopByEmpresaIdAndAtivaTrueOrderByDataFimDesc(empresaId)
                .map(Licenca::isValida)
                .orElse(false);
    }

    public boolean excedeuLimiteUsuarios(Long empresaId) {
        Licenca licenca = licencaRepository.findTopByEmpresaIdAndAtivaTrueOrderByDataFimDesc(empresaId)
                .orElseThrow(() -> new RuntimeException("Licença não encontrada"));

        long usuariosAtivos = userRepository.countByEmpresaIdAndAtivoTrue(empresaId);
        return usuariosAtivos > licenca.getTipoLicenca().getLimiteUsuarios();
    }

    public LicencaStatusDTO getStatusLicenca(Long empresaId) {
        Licenca licenca = licencaRepository.findTopByEmpresaIdAndAtivaTrueOrderByDataFimDesc(empresaId)
                .orElseThrow(() -> new RuntimeException("Licença não encontrada"));

        LocalDate hoje = LocalDate.now();
        boolean valida = licenca.isValida();
        boolean expirada = hoje.isAfter(licenca.getDataFim());
        long diasRestantes = ChronoUnit.DAYS.between(hoje, licenca.getDataFim());
        boolean excedeu = excedeuLimiteUsuarios(empresaId);

        return new LicencaStatusDTO(valida, expirada, diasRestantes, excedeu);
    }

    public Page<LicencaDetalhadaDTO> buscarLicencasPaginadas(String empresaNome, String usuarioNome, String cpf, Pageable pageable) {
        Specification<Licenca> spec = LicencaSpecification.filter(empresaNome, usuarioNome, cpf);
        Page<Licenca> page = licencaRepository.findAll(spec, pageable);

        return page.map(licenca -> {
            LicencaDetalhadaDTO dto = new LicencaDetalhadaDTO();
            dto.setId(licenca.getId());
            dto.setDataInicio(licenca.getDataInicio());
            dto.setDataFim(licenca.getDataFim());
            dto.setTipoLicencaId(licenca.getTipoLicenca().getId());
            dto.setTipoLicencaNome(licenca.getTipoLicenca().getNome());
            dto.setLimiteUsuarios(licenca.getTipoLicenca().getLimiteUsuarios());
            dto.setAtiva(licenca.getAtiva());

            Empresa empresa = licenca.getEmpresa();
            dto.setEmpresaId(empresa.getId());
            dto.setEmpresaNome(empresa.getName());
            dto.setEmpresaCnpj(empresa.getCnpj());
            dto.setEmpresaTelefone(empresa.getTelefone());

            if (empresa.getUsuarioResponsavel() != null) {
                Usuario user = empresa.getUsuarioResponsavel();
                dto.setResponsavelId(user.getId());
                dto.setResponsavelNome(user.getNome());
                dto.setResponsavelCpf(user.getCpf());
                dto.setPerfil(user.getRole().getName());
            }
            dto.setQtdUsuarios((int) userRepository.countByEmpresa(empresa));

            dto.setValidadeExpirada(licenca.getDataFim().isBefore(LocalDate.now()));
            dto.setDiasRestantes(ChronoUnit.DAYS.between(LocalDate.now(), licenca.getDataFim()));

            return dto;
        });
    }

    public List<TipoLicenca> buscarTipos() {
        return tipoLicencaRepository.findAll();
    }
}
