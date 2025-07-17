package br.com.estoque.service;

import br.com.estoque.dto.VeiculoDTO;
import br.com.estoque.model.Cliente;
import br.com.estoque.model.Veiculo;
import br.com.estoque.repository.ClienteRepository;
import br.com.estoque.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepo;

    @Autowired
    private ClienteRepository clienteRepo;

    private VeiculoDTO converterParaDTO(Veiculo v) {
        return new VeiculoDTO(
                v.getId(),
                v.getModelo(),
                v.getPlaca(),
                v.getMarca(),
                v.getAno(),
                v.getCor(),
                v.getQuilometragem(),
                v.getCliente() != null ? v.getCliente().getId() : null,
                v.getCliente() != null ? v.getCliente().getNome() : null,
                v.getCliente() != null ? v.getCliente().getCpfCnpj() : null,
                v.getCliente() != null ? v.getCliente().getEmail() : null,
                v.getCliente() != null ? v.getCliente().getTelefone() : null

        );
    }

    public Page<VeiculoDTO> listar(Pageable pageable) {
        return veiculoRepo.findAll(pageable)
                .map(this::converterParaDTO);
    }

    public Page<VeiculoDTO> listarComFiltro(String placa, String modelo, String cpfCnpj, Pageable pageable) {
        Specification<Veiculo> spec = Specification.where(null);

        if (placa != null && !placa.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("placa")), "%" + placa.toLowerCase() + "%"));
        }

        if (modelo != null && !modelo.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("modelo")), "%" + modelo.toLowerCase() + "%"));
        }

        if (cpfCnpj != null && !cpfCnpj.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.join("cliente").get("cpfCnpj")), "%" + cpfCnpj.toLowerCase() + "%"));
        }

        return veiculoRepo.findAll(spec, pageable).map(this::converterParaDTO);
    }

    public VeiculoDTO salvar(VeiculoDTO dto) {
        Cliente cliente = clienteRepo.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Veiculo v = new Veiculo();
        v.setId(dto.getId());
        v.setModelo(dto.getModelo());
        v.setPlaca(dto.getPlaca());
        v.setMarca(dto.getMarca());
        v.setAno(dto.getAno());
        v.setCor(dto.getCor());
        v.setQuilometragem(dto.getQuilometragem());
        v.setCliente(cliente);

        Veiculo salvo = veiculoRepo.save(v);

        return converterParaDTO(salvo);
    }

    public void excluir(Long id) {
        veiculoRepo.deleteById(id);
    }

}

