package br.com.alura.estoque.repository;

import android.content.Context;

import java.util.List;

import br.com.alura.estoque.asynctask.BaseAsyncTask;
import br.com.alura.estoque.callback.BaseCallBack;
import br.com.alura.estoque.callback.CallBackSemRetorno;
import br.com.alura.estoque.database.EstoqueDatabase;
import br.com.alura.estoque.database.dao.ProdutoDAO;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.retrofit.EstoqueRetrofit;
import br.com.alura.estoque.retrofit.service.ProdutoService;
import br.com.alura.estoque.ui.activity.ListaProdutosActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProdutoRepository {

    public static final String MENSAGEM_FALHA_DE_COMUNICACAO = "Falha de comunicação: ";
    private final ProdutoDAO dao;
    private ProdutoService produtoService;

    public ProdutoRepository(Context context) {
        EstoqueDatabase db = EstoqueDatabase.getInstance ( context );
        this.dao = db.getProdutoDAO ();
        produtoService = new EstoqueRetrofit ().getProdutoService ();
    }

    public void buscaProdutosInternos(DadosCarregadosCallback<List<Produto>> listener) {
        new BaseAsyncTask<> ( dao::buscaTodos,
                resultado -> {
                    listener.quandoSucesso ( resultado );
                    buscaProdutosNaApi ( listener );
                } )
                .execute ();
    }

    public void buscaProdutosNaApi(DadosCarregadosCallback<List<Produto>> listener) {
        Call<List<Produto>> call = produtoService.buscaTodos ();

        call.enqueue ( new Callback<List<Produto>> () {
            @Override
            public void onResponse(Call<List<Produto>> call, Response<List<Produto>> response) {

                if (response.isSuccessful ()) {
                    atualizaInterno ( response, listener );
                } else {
                    listener.quandoFalha ( response.message () );
                }
            }

            @Override
            public void onFailure(Call<List<Produto>> call, Throwable t) {
                listener.quandoFalha ( MENSAGEM_FALHA_DE_COMUNICACAO + t.getMessage () );
            }
        } );
    }

    private void atualizaInterno(Response<List<Produto>> response, DadosCarregadosCallback<List<Produto>> listener) {
        new BaseAsyncTask<> ( () -> {
            List<Produto> produtos = response.body ();
            dao.salva ( produtos );
            return dao.buscaTodos ();

        }, (produtos) -> listener.quandoSucesso ( produtos ) )
                .execute ();
    }

    public void salva(Produto produto, DadosCarregadosCallback<Produto> callback) {
        Call<Produto> call = produtoService.salva ( produto );

        call.enqueue ( new BaseCallBack<> ( new BaseCallBack.RespostaCallBack<Produto> () {
            @Override
            public void quandoSucesso(Produto resposta) {
                salvaInterno ( resposta, callback );
            }

            @Override
            public void quandoFalha(String erro) {
                callback.quandoFalha ( MENSAGEM_FALHA_DE_COMUNICACAO + erro );
            }
        } ) );
    }

    private void salvaInterno(Produto produtoSalvo, DadosCarregadosCallback<Produto> listener) {
        new BaseAsyncTask<> ( () ->
                dao.buscaProduto ( dao.salva ( produtoSalvo ) )
                , listener::quandoSucesso )
                .execute ();
    }

    public void edita(Produto produto, DadosCarregadosCallback<Produto> callback) {
        Call<Produto> call = produtoService.atualiza ( produto.getId (), produto );

        call.enqueue ( new BaseCallBack<> ( new BaseCallBack.RespostaCallBack<Produto> () {
            @Override
            public void quandoSucesso(Produto produtoAtualizado) {
                atualizaInterno ( produtoAtualizado, callback );
            }

            @Override
            public void quandoFalha(String erro) {
                callback.quandoFalha ( MENSAGEM_FALHA_DE_COMUNICACAO + erro );
            }
        } ) );
    }

    private void atualizaInterno(Produto produtoAtualizado, DadosCarregadosCallback<Produto> callback) {
        new BaseAsyncTask<> ( () -> {
            dao.atualiza ( produtoAtualizado );
            return produtoAtualizado;
        }, callback::quandoSucesso ).execute ();
    }

    public void remove(Produto produtoRemovido, CallBackSemRetorno.RespostaCallBack callback) {

        Call<Void> call = produtoService.remove ( produtoRemovido.getId () );

        call.enqueue ( new CallBackSemRetorno ( new CallBackSemRetorno.RespostaCallBack () {

            @Override
            public void quandoSucesso() {

                new BaseAsyncTask<> ( () -> {
                    dao.remove ( produtoRemovido );
                    return null;
                }, resultado -> callback.quandoSucesso () )
                        .execute ();
            }

            @Override
            public void quandoFalha(String erro) {
                callback.quandoFalha ( erro );
            }
        } ) );
    }

    public interface DadosCarregadosCallback<T> {
        void quandoSucesso(T resultado);

        void quandoFalha(String erro);
    }
}
