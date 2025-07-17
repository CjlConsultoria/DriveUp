package br.com.estoque.service;

import br.com.estoque.exception.NegocioException;
import br.com.estoque.model.MovimentoEstoque;
import br.com.estoque.model.Produto;
import br.com.estoque.model.enums.TipoMovimentacao;
import br.com.estoque.model.enums.TipoProduto;
import br.com.estoque.model.specification.ProdutoSpecification;
import br.com.estoque.repository.MovimentoEstoqueRepository;
import br.com.estoque.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepo;
    private final MovimentoEstoqueRepository movimentoRepo;

    public ProdutoService(ProdutoRepository produtoRepo, MovimentoEstoqueRepository movimentoRepo) {
        this.produtoRepo = produtoRepo;
        this.movimentoRepo = movimentoRepo;
    }

    public Produto salvar(Produto produto) {
        produto.setDescricao(capitalizarDescricao(produto.getDescricao()));
        Produto produtoSalvo = produtoRepo.save(produto);

        MovimentoEstoque movimentoEstoque = new MovimentoEstoque();
        movimentoEstoque.setProduto(produtoSalvo);
        movimentoEstoque.setTipo(TipoMovimentacao.CRIADO);
        movimentoEstoque.setQuantidade(produtoSalvo.getQuantidadeEstoque());
        movimentoEstoque.setDataVenda(LocalDateTime.now());
        movimentoEstoque.setDescricao("Criação de novo produto.");

        movimentoRepo.save(movimentoEstoque);

        return produtoSalvo;
    }


    public Page<Produto> listarTodosPaginado(Pageable pageable) {
        return produtoRepo.findAll(pageable);
    }


    public Page<Produto> buscarFiltrado(String codigo, String descricao, String categoria, Pageable pageable) {
        Specification<Produto> spec = ProdutoSpecification.filtrar(codigo, descricao, categoria);
        return produtoRepo.findAll(spec, pageable);
    }


    public Produto buscarPorId(Long id) {
        return produtoRepo.findById(id)
                .orElseThrow(() -> new NegocioException("Produto não encontrado"));
    }

    public Produto atualizar(Long id, Produto produtoAtualizado) {
        Produto produtoExistente = buscarPorId(id);

        StringBuilder descricao = new StringBuilder("Atualização em " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + ": ");

        if (!Objects.equals(produtoExistente.getCodigo(), produtoAtualizado.getCodigo())) {
            descricao.append("Código alterado de ")
                    .append(produtoExistente.getCodigo()).append(" para ")
                    .append(produtoAtualizado.getCodigo()).append(". ");
        }

        if (!Objects.equals(produtoExistente.getDescricao(), produtoAtualizado.getDescricao())) {
            descricao.append("Descrição alterada de ")
                    .append(produtoExistente.getDescricao()).append(" para ")
                    .append(produtoAtualizado.getDescricao()).append(". ");
        }

        if (!Objects.equals(produtoExistente.getCategoria(), produtoAtualizado.getCategoria())) {
            String nomeCategoriaAntes = produtoExistente.getCategoria() != null
                    ? produtoExistente.getCategoria().getNome()
                    : "Sem categoria";
            String nomeCategoriaDepois = produtoAtualizado.getCategoria() != null
                    ? produtoAtualizado.getCategoria().getNome()
                    : "Sem categoria";

            descricao.append("Categoria alterada de ")
                    .append(nomeCategoriaAntes).append(" para ")
                    .append(nomeCategoriaDepois).append(". ");
        }


        if (produtoExistente.getValorFornecedor() != null && !produtoExistente.getValorFornecedor().equals(produtoAtualizado.getValorFornecedor())) {
            descricao.append("Valor do fornecedor alterado de R$ ")
                    .append(produtoExistente.getValorFornecedor()).append(" para R$ ")
                    .append(produtoAtualizado.getValorFornecedor()).append(". ");
        }

        if (produtoExistente.getQuantidadeEstoque() != produtoAtualizado.getQuantidadeEstoque()) {
            descricao.append("Quantidade em estoque alterada de ")
                    .append(produtoExistente.getQuantidadeEstoque()).append(" para ")
                    .append(produtoAtualizado.getQuantidadeEstoque()).append(". ");
        }

        produtoExistente.setCodigo(produtoAtualizado.getCodigo());
        produtoExistente.setDescricao(produtoAtualizado.getDescricao());
        produtoExistente.setCategoria(produtoAtualizado.getCategoria());
        produtoExistente.setValorFornecedor(produtoAtualizado.getValorFornecedor());
        produtoExistente.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());

        MovimentoEstoque movimentacaoExclusao = new MovimentoEstoque();
        movimentacaoExclusao.setProduto(produtoExistente);
        movimentacaoExclusao.setTipo(TipoMovimentacao.EDITADO);
        movimentacaoExclusao.setQuantidade(produtoAtualizado.getQuantidadeEstoque());
        movimentacaoExclusao.setDataVenda(LocalDateTime.now());
        movimentacaoExclusao.setDescricao(descricao.toString().trim());

        movimentoRepo.save(movimentacaoExclusao);

        return produtoRepo.save(produtoExistente);
    }


    @Transactional
    public void deletar(Long id) {
        Produto produto = produtoRepo.findById(id)
                .orElseThrow(() -> new NegocioException("Produto não encontrado"));

        if (!produto.isAtivo()) {
            throw new NegocioException("Produto já está inativo");
        }


        produto.setAtivo(false);
        produtoRepo.save(produto);


        MovimentoEstoque movimentacaoExclusao = new MovimentoEstoque();
        movimentacaoExclusao.setProduto(produto);
        movimentacaoExclusao.setTipo(TipoMovimentacao.EXCLUSAO);
        movimentacaoExclusao.setQuantidade(produto.getQuantidadeEstoque());
        movimentacaoExclusao.setDataVenda(LocalDateTime.now());
        movimentacaoExclusao.setDescricao("Produto (" + produto.getCodigo() + ") - " + produto.getDescricao() + " foi desativado.");
        movimentacaoExclusao.setValorVenda(null);

        movimentoRepo.save(movimentacaoExclusao);
    }


    public String gerarNovoCodigo() {

        Produto produto = produtoRepo.findTopByOrderByCodigoDesc();

        if (produto == null || produto.getCodigo() == null) {
            return "P001";
        }

        String codigoAtual = produto.getCodigo();

        int numero = Integer.parseInt(codigoAtual.substring(1));

        int novoNumero = numero + 1;


        return String.format("P%03d", novoNumero);
    }

    public List<Produto> buscarPorCategoria(String nomeCategoria) {
        return produtoRepo.findByCategoriaNomeAndAtivoTrue(nomeCategoria);
    }


    public List<Produto> listarTodos() {
        return produtoRepo.findAll();
    }
    public String capitalizarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            return descricao;
        }

        String[] palavras = descricao.trim().toLowerCase().split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (String palavra : palavras) {
            if (palavra.length() > 0) {
                resultado.append(Character.toUpperCase(palavra.charAt(0)));
                if (palavra.length() > 1) {
                    resultado.append(palavra.substring(1));
                }
                resultado.append(" ");
            }
        }

        return resultado.toString().trim();
    }

    public Produto reativarProduto(Long id) {
        Produto produto = produtoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (produto.isAtivo()) {
            throw new RuntimeException("Produto já está ativo");
        }

        produto.setAtivo(true);
        return produtoRepo.save(produto);
    }


    public Page<Produto> listarDesativadosComFiltro(String descricao, Long categoriaId, Pageable pageable) {
        var spec = ProdutoSpecification.desativadosComFiltro(descricao, categoriaId);
        return produtoRepo.findAll(spec, pageable);
    }


}
