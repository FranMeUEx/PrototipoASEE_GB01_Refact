package es.unex.prototipoasee.viewModels;

import androidx.lifecycle.ViewModel;

import java.util.Collection;

import es.unex.prototipoasee.model.Films;
import es.unex.prototipoasee.repository.Repository;

public class FavoritesFragmentViewModel extends ViewModel {

    private final Repository repository;

    public FavoritesFragmentViewModel(Repository repository) {
        this.repository = repository;
    }

    public Collection<Films> getUserFavoritesFilms() {
        return repository.getUserFavoritesFilms().values();
    }
}
