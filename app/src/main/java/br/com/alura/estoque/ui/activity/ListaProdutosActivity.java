package br.com.alura.estoque.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import br.com.alura.estoque.R;
import br.com.alura.estoque.callback.CallBackSemRetorno;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.repository.ProdutoRepository;
import br.com.alura.estoque.ui.dialog.EditaProdutoDialog;
import br.com.alura.estoque.ui.dialog.SalvaProdutoDialog;
import br.com.alura.estoque.ui.recyclerview.adapter.ListaProdutosAdapter;

public class ListaProdutosActivity extends AppCompatActivity {

    private static final String TITULO_APPBAR = "Lista de produtos";
    public static final String MENSAGEM_ERRO_REMOCAO_PRODUTO = "Não foi possível remover o produto: ";
    public static final String MENSAGEM_ERRO_BUSCAR_PRODUTOS = "Não foi possível buscar os produtos: ";
    public static final String MENSAGEM_ERRO_ATUALIZAR_PRODUTO = "Não foi possível atualizar o produto: ";
    public static final String MENSAGEM_ERRO_SALVAR_PRODUTO = "Não foi possível salvar o produto: ";
    private ProdutoRepository repository;
    private ListaProdutosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_lista_produtos );
        setTitle ( TITULO_APPBAR );

        configuraListaProdutos ();
        configuraFabSalvaProduto ();

        repository = new ProdutoRepository ( this );

        buscarProdutos ();
    }

    private void buscarProdutos() {
        repository.buscaProdutosInternos ( new ProdutoRepository.DadosCarregadosCallback<List<Produto>> () {
            @Override
            public void quandoSucesso(List<Produto> resultado) {
                adapter.atualiza ( resultado );
            }

            @Override
            public void quandoFalha(String erro) {
                exibeMensagem ( MENSAGEM_ERRO_BUSCAR_PRODUTOS + erro );
            }
        } );
    }

    private void exibeMensagem(String mensagem) {
        Toast.makeText ( ListaProdutosActivity.this, mensagem, Toast.LENGTH_SHORT ).show ();
    }

    private void remove(int posicao, Produto produtoRemovido) {
        repository.remove ( produtoRemovido, new CallBackSemRetorno.RespostaCallBack () {
            public void quandoSucesso() {
                adapter.remove ( posicao );
            }

            public void quandoFalha(String erro) {
                exibeMensagem ( MENSAGEM_ERRO_REMOCAO_PRODUTO + erro );
            }
        } );
    }

    private void abreFormularioSalvaProduto() {
        new SalvaProdutoDialog ( this, produto -> {
            repository.salva ( produto, new ProdutoRepository.DadosCarregadosCallback<Produto> () {
                @Override
                public void quandoSucesso(Produto resultado) {
                    adapter.adiciona ( resultado );
                }

                @Override
                public void quandoFalha(String erro) {
                    exibeMensagem ( MENSAGEM_ERRO_SALVAR_PRODUTO + erro );
                }
            } );
        } ).mostra ();
    }

    private void abreFormularioEditaProduto(int posicao, Produto produto) {
        new EditaProdutoDialog ( this, produto,
                produtoEditado -> repository.edita ( produtoEditado, new ProdutoRepository.DadosCarregadosCallback<Produto> () {
                    @Override
                    public void quandoSucesso(Produto resultado) {
                        adapter.edita ( posicao, resultado );
                    }

                    @Override
                    public void quandoFalha(String erro) {
                        exibeMensagem ( MENSAGEM_ERRO_ATUALIZAR_PRODUTO + erro);
                    }
                } ) )
                .mostra ();
    }

    private void configuraListaProdutos() {
        RecyclerView listaProdutos = findViewById ( R.id.activity_lista_produtos_lista );
        adapter = new ListaProdutosAdapter ( this, this::abreFormularioEditaProduto );

        listaProdutos.setAdapter ( adapter );
        adapter.setOnItemClickRemoveContextMenuListener ( this::remove );
    }

    private void configuraFabSalvaProduto() {
        FloatingActionButton fabAdicionaProduto = findViewById ( R.id.activity_lista_produtos_fab_adiciona_produto );
        fabAdicionaProduto.setOnClickListener ( v -> abreFormularioSalvaProduto () );
    }
}
