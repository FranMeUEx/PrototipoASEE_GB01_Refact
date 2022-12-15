package es.unex.prototipoasee.room;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.Set;

import es.unex.prototipoasee.model.Films;

@Dao
public interface FilmDAO {

    @Insert(onConflict = REPLACE)
    void insertAllFilms(List<Films> filmsList);

    @Update
    void updateFilm(Films film);

    @Query("SELECT * FROM Films WHERE filmID = (:filmid)")
    Films getFilm(int filmid);

    @Query("SELECT * FROM Films WHERE filmID IN (:filmids)")
    List<Films> getFilmsByID(List<Integer> filmids);

    @Query("SELECT * FROM Films")
    LiveData<List<Films>> getAllFilms();

    @Query("SELECT * FROM Films")
    List<Films> getAllFilmsAsList();

    @Query("SELECT f.filmID, f.poster, f.overview, f.releaseDate, f.originalTitle, f.title, f.totalRatingMovieCheck, f.totalVotesMovieCheck, f.voteAverage FROM films f JOIN Pendings p ON f.filmID = p.filmID WHERE p.username = (:username)")
    List<Films> getPendingsFilms(String username);

    @Query("SELECT f.filmID, f.poster, f.overview, f.releaseDate, f.originalTitle, f.title, f.totalRatingMovieCheck, f.totalVotesMovieCheck, f.voteAverage FROM films f JOIN Favorites fav ON f.filmID = fav.filmID WHERE fav.username = (:username)")
    List<Films> getFavoritesFilms(String username);

    @Query("SELECT f.filmID, f.poster, f.overview, f.releaseDate, f.originalTitle, f.title, f.totalRatingMovieCheck, f.totalVotesMovieCheck, f.voteAverage FROM films f JOIN FilmsGenresList g  ON f.filmID = g.filmID WHERE g.genreID = (:genreid)")
    LiveData<List<Films>> getFilmsByGenres(int genreid);

    @Query("SELECT * FROM Films WHERE totalVotesMovieCheck != 0")
    List<Films> getRatedFilms();
}
