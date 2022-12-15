package es.unex.prototipoasee.room;

import androidx.room.Dao;
import androidx.room.Ignore;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FilmsGenresListDAO {


    @Query("INSERT OR IGNORE INTO FilmsGenresList VALUES ((:filmid), (:genreid))")
    void insertFilmGenre(int filmid, int genreid);

    @Query("SELECT name FROM FilmsGenresList fg JOIN Genres f ON (fg.genreID=f.genreID) WHERE filmID = (:filmid)")
    List<String> getAllFilmsGenresNames(int filmid);
}
