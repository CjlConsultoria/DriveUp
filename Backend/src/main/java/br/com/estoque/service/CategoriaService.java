package br.com.estoque.service;

import br.com.estoque.model.Categoria;
import br.com.estoque.model.Produto;
import br.com.estoque.repository.CategoriaRepository;
import br.com.estoque.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;
    private final ProdutoRepository produtoRepository;


    public CategoriaService(CategoriaRepository repository, ProdutoRepository produtoRepository) {
        this.repository = repository;
        this.produtoRepository = produtoRepository;
    }

    public List<Categoria> listar() {
        return repository.findAll();
    }

    public Categoria salvar(Categoria categoria) {
        String nomeMaiusculo = categoria.getNome().toUpperCase();
        categoria.setNome(nomeMaiusculo);

        if (repository.existsByNomeIgnoreCase(nomeMaiusculo)) {
            throw new RuntimeException("Categoria já existe");
        }

        return repository.save(categoria);
    }


    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Categoria não encontrada");
        }
        List<Produto> produtosRelacionados = produtoRepository.findByCategoriaId(id);
        for (Produto produto : produtosRelacionados) {
            produto.setCategoria(null);
        }
        produtoRepository.saveAll(produtosRelacionados);
        repository.deleteById(id);
    }

}

