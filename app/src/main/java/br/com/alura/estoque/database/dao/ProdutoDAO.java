package br.com.alura.estoque.database.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import br.com.alura.estoque.model.Produto;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ProdutoDAO {

    @Insert
    long salva(Produto produto);

    @Insert(onConflict = REPLACE)
    void salva(List<Produto> produtos);

    @Update
    void atualiza(Produto produto);

    @Delete
    void remove(Produto produto);

    @Query("SELECT * FROM Produto")
    List<Produto> buscaTodos();

    @Query("SELECT * FROM Produto WHERE id = :id")
    Produto buscaProduto(long id);
}
