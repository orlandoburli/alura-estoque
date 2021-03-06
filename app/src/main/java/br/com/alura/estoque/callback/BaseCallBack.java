package br.com.alura.estoque.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class BaseCallBack<T> implements Callback<T> {

    private final RespostaCallBack<T> callBack;

    public BaseCallBack(RespostaCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    @EverythingIsNonNull
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful ()) {
            T resultado = response.body ();

            if (resultado != null) {
                callBack.quandoSucesso ( resultado );
            } else {
                callBack.quandoFalha ( "Resposta não sucedida" );
            }
        } else {
            callBack.quandoFalha ( "Resposta não sucedida" );
        }
    }

    @EverythingIsNonNull
    @Override
    public void onFailure(Call<T> call, Throwable t) {
        callBack.quandoFalha ( "Falha de comunicação: " + t.getMessage () );
    }

    public interface RespostaCallBack<T> {
        void quandoSucesso(T resposta);

        void quandoFalha(String erro);
    }
}