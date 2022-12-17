package es.unex.prototipoasee.viewModels;

import androidx.lifecycle.ViewModel;

import java.util.List;

import es.unex.prototipoasee.model.Films;
import es.unex.prototipoasee.repository.Repository;

public class ItemDetailInfoFragmentViewModel extends ViewModel {
    private final Repository repository;

    public ItemDetailInfoFragmentViewModel(Repository repository) {
        this.repository = repository;
    }

    /**
     * Consulta las listas que mantienen la información viva del usuario respecto a sus películas favoritas para determinar si está o no
     * marcada como tal.
     * @return True si la película está marcada como favorita o False en caso contrario
     */
    public boolean filmInFavorites(Films film) {
        return repository.getUserFavoritesFilms().get(film.getId()) != null;
    }

    /**
     * Consulta la información viva del usuario respecto a sus películas pendientes y determina la presencia de la película en cuestión (film)
     * @return True si la película está marcada como pendiente o False en caso contrario
     */
    public boolean filmInPendings(Films film) {
        return repository.getUserPendingsFilms().get(film.getId()) != null;
    }

    public List<String> getFilmGenres(Films film) {
        return repository.getFilmGenres(film);
    }

    public void addFavoriteFilm(Films film) {
        repository.addUserFavoriteFilm(film);
    }

    public void removeFavoriteFilm(Films film) {
        repository.removeUserFavoriteFilm(film);
    }

    public void addPendingFilm(Films film) {
        repository.addUserPendingFilm(film);
    }

    public void removePendingFilm(Films film) {
        repository.removeUserPedingFilm(film);
    }

}
