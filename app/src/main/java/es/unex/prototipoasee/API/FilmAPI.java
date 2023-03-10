package es.unex.prototipoasee.API;

import es.unex.prototipoasee.model.FilmsPages;
import es.unex.prototipoasee.model.GenresList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FilmAPI {
    @GET("/3/movie/popular")
    Call<FilmsPages> getFilms(
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("/3/genre/movie/list")
    Call<GenresList> getGenres(
            @Query("api_key") String apiKey,
            @Query("language") String language
    );
}
