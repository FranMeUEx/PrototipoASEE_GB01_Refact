package es.unex.prototipoasee.viewModels;

import androidx.lifecycle.ViewModel;

import java.util.Collection;

import es.unex.prototipoasee.model.Films;
import es.unex.prototipoasee.repository.Repository;

public class PendingsFragmentViewModel extends ViewModel {

    private final Repository repository;

    public PendingsFragmentViewModel(Repository repository) {
        this.repository = repository;
    }

    public Collection<Films> getUserPendingFilms() {
        return repository.getUserPendingsFilms().values();
    }

}
