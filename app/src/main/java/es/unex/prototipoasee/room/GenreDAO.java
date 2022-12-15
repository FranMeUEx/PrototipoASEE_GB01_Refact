package es.unex.prototipoasee.room;

import static androidx.room.OnConflictStrategy.IGNORE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import es.unex.prototipoasee.model.Genre;

@Dao
public interface GenreDAO {

    @Insert(onConflict = IGNORE)
    void insertAllGenres(List<Genre> list);

    @Query("SELECT * FROM Genres")
    LiveData<List<Genre>> getAllGenres();
}
