package es.unex.prototipoasee.viewModels;


import androidx.lifecycle.ViewModel;

import java.util.Collection;

import es.unex.prototipoasee.model.Films;
import es.unex.prototipoasee.repository.Repository;

public class HomeActivityViewModel extends ViewModel {

    private final Repository repository;

    public HomeActivityViewModel(Repository repository) {
        this.repository = repository;
    }

    public void getUserFilmData(String username) {
        repository.getUserFilmData(username);
    }

    public void removeUserFavoriteFilm(Films film) {
        repository.removeUserFavoriteFilm(film);
    }

    public Collection<Films> getUserFavoriteFilms() {
        return repository.getUserFavoritesFilms().values();
    }

    public void removeUserPendingFilm(Films film) {
        repository.removeUserPedingFilm(film);
    }

    public Collection<Films> getUserPendingFilms() {
        return repository.getUserPendingsFilms().values();
    }
}
