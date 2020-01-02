package br.com.alura.estoque.retrofit;

import br.com.alura.estoque.retrofit.service.ProdutoService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EstoqueRetrofit {

    public static final String URL_BASE = "http://192.168.18.25:8080";
    private final ProdutoService produtoService;

    public EstoqueRetrofit() {
        Retrofit retrofit = this.buildRetrofit ();

        produtoService = retrofit.create ( ProdutoService.class );
    }

    private Retrofit buildRetrofit() {
        return new Retrofit.Builder ()
                .baseUrl ( URL_BASE )
                .addConverterFactory ( GsonConverterFactory.create () )
                .client ( this.buildClient () )
                .build ();
    }

    private OkHttpClient buildClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor ();
        logging.setLevel ( HttpLoggingInterceptor.Level.BODY );
        return new OkHttpClient.Builder ()
                .addInterceptor ( logging )
                .build ();
    }

    public ProdutoService getProdutoService() {
        return produtoService;
    }
}