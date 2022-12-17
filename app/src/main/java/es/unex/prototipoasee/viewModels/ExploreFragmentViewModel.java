package es.unex.prototipoasee.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import es.unex.prototipoasee.model.Films;
import es.unex.prototipoasee.model.Genre;
import es.unex.prototipoasee.repository.Repository;

public class ExploreFragmentViewModel extends ViewModel {

    private final Repository repository;
    private final LiveData<List<Films>> currentFilms;
    private final LiveData<List<Genre>> currentGenres;
    private final LiveData<List<Films>> filmsByTitle;
    private final LiveData<List<Films>> filmsByGenre;

    public ExploreFragmentViewModel(Repository repository) {
        this.repository = repository;
        currentFilms = repository.getCurrentFilms();
        currentGenres = repository.getCurrentGenres();
        filmsByTitle = repository.getTitleFilterLiveData();
        filmsByGenre = repository.getFilmsByGenre();
    }

    public LiveData<List<Films>> getFilms() {
        return currentFilms;
    }

    public LiveData<List<Genre>> getGenres() {
        return currentGenres;
    }

    public LiveData<List<Films>> getFilmsByGenre() {
        return filmsByGenre;
    }

    public LiveData<List<Films>> getTitleFilterLiveData() {
        return filmsByTitle;
    }

    public void forceFetchFilmAndGenres() {
        repository.forceFetchFilmsAndGenres();
    }

    public void setGenre(int genreId) {
        repository.setGenre(genreId);
    }

    public void getFilmsByTitle(String query) {
        repository.getFilmsByTitle(query);
    }
}
