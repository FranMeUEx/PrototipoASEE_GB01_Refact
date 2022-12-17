package es.unex.prototipoasee.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import es.unex.prototipoasee.model.Comments;
import es.unex.prototipoasee.model.Films;
import es.unex.prototipoasee.repository.Repository;

public class ItemDetailSocialFragmentViewModel extends ViewModel {

    private final Repository repository;
    private LiveData<List<Comments>> comments;

    public ItemDetailSocialFragmentViewModel(Repository repository) {
        this.repository = repository;
    }

    public void changeFilm(Films film) {
        Log.i("PRUEBA", "Film changed to "+film.getId());
        comments = repository.getFilmComments(film);
    }

    public LiveData<List<Comments>> getComments() {
        return comments;
    }

    public void addComment(Comments comment) {
        Log.i("PRUEBA", "Comment added to "+comment.getFilmID());
        repository.addComment(comment);
    }

    public boolean filmNotRated(Films film) {
        return !repository.getUserRatedFilms().contains(film.getId());
    }

    public void addRating(Films film, int rating) {
        repository.addUserRatedFilm(film, rating);
    }
}
