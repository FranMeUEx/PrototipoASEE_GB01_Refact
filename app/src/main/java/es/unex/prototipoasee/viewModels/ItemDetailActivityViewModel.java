package es.unex.prototipoasee.viewModels;

import androidx.lifecycle.ViewModel;

import es.unex.prototipoasee.model.Comments;
import es.unex.prototipoasee.model.Films;
import es.unex.prototipoasee.repository.Repository;

public class ItemDetailActivityViewModel extends ViewModel {
    private final Repository repository;

    public ItemDetailActivityViewModel(Repository repository) {
        this.repository = repository;
    }

    public void getFilm(Films film) {
        repository.getFilm(film);
    }

    public void deleteComment(Comments comment) {
        repository.deleteComment(comment);
    }
}
