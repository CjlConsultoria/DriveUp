package br.com.estoque.service;

import br.com.estoque.dto.ClienteDTO;
import br.com.estoque.dto.EnderecoDTO;
import br.com.estoque.model.Cliente;
import br.com.estoque.model.Endereco;
import br.com.estoque.model.specification.ClienteSpecification;
import br.com.estoque.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepo;

    public Page<ClienteDTO> listar(String nome, String email, String cpfCnpj, Pageable pageable) {
        Specification<Cliente> spec = ClienteSpecification.comFiltros(nome, email, cpfCnpj);

        return clienteRepo.findAll(spec, pageable)
                .map(cliente -> {
                    Endereco endereco = cliente.getEndereco();
                    EnderecoDTO enderecoDTO = null;

                    if (endereco != null) {
                        enderecoDTO = new EnderecoDTO(
                                endereco.getCep(),
                                endereco.getRua(),
                                endereco.getNumero(),
                                endereco.getComplemento(),
                                endereco.getBairro(),
                                endereco.getCidade(),
                                endereco.getEstado()
                        );
                    }

                    return new ClienteDTO(
                            cliente.getId(),
                            cliente.getNome(),
                            cliente.getCpfCnpj(),
                            cliente.getEmail(),
                            cliente.getTelefone(),
                            enderecoDTO
                    );
                });
    }

    public ClienteDTO salvar(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setCpfCnpj(dto.getCpfCnpj());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());

        if (dto.getEndereco() != null) {
            Endereco endereco = new Endereco();
            endereco.setCep(dto.getEndereco().getCep());
            endereco.setRua(dto.getEndereco().getRua());
            endereco.setComplemento(dto.getEndereco().getComplemento());
            endereco.setNumero(dto.getEndereco().getNumero());
            endereco.setBairro(dto.getEndereco().getBairro());
            endereco.setCidade(dto.getEndereco().getCidade());
            endereco.setEstado(dto.getEndereco().getEstado());
            cliente.setEndereco(endereco);
        }

        Cliente salvo = clienteRepo.save(cliente);

        Endereco enderecoSalvo = salvo.getEndereco();
        EnderecoDTO enderecoDTO = null;
        if (enderecoSalvo != null) {
            enderecoDTO = new EnderecoDTO(
                    enderecoSalvo.getCep(),
                    enderecoSalvo.getRua(),
                    enderecoSalvo.getNumero(),
                    enderecoSalvo.getComplemento(),
                    enderecoSalvo.getBairro(),
                    enderecoSalvo.getCidade(),
                    enderecoSalvo.getEstado()
            );
        }

        return new ClienteDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getCpfCnpj(),
                salvo.getEmail(),
                salvo.getTelefone(),
                enderecoDTO
        );
    }

    public void excluir(Long id) {
        clienteRepo.deleteById(id);
    }
}
