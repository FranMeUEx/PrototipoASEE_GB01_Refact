package es.unex.prototipoasee;

import android.content.Context;

import es.unex.prototipoasee.API.FilmsNetworkDataSource;
import es.unex.prototipoasee.repository.Repository;
import es.unex.prototipoasee.repository.UsersRepository;
import es.unex.prototipoasee.room.FilmsDatabase;
import es.unex.prototipoasee.viewmodels.MainViewModelFactory;

public class AppContainer {
    private FilmsDatabase filmsDatabase;
    private FilmsNetworkDataSource filmsNetworkDataSource;
    public UsersRepository usersRepository;
    public Repository repository;
    public MainViewModelFactory factory;

    public AppContainer(Context context){
        filmsDatabase = FilmsDatabase.getInstance(context);
        filmsNetworkDataSource = FilmsNetworkDataSource.getInstance();
        repository = Repository.getInstance(filmsDatabase.filmDAO(), filmsDatabase.favoritesDAO(), filmsDatabase.pendingsDAO(), filmsDatabase.commentDAO(), filmsDatabase.ratingDAO(), filmsDatabase.genreDAO(), filmsDatabase.filmsGenresListDAO(), filmsNetworkDataSource);
        usersRepository = UsersRepository.getInstance(filmsDatabase.userDAO());
        factory = new MainViewModelFactory(repository, usersRepository);
    }
}
