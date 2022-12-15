package es.unex.prototipoasee.viewmodels;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import es.unex.prototipoasee.repository.Repository;
import es.unex.prototipoasee.repository.UsersRepository;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Repository mRepository;
    private final UsersRepository mUsersRepository;

    public MainViewModelFactory(Repository repository, UsersRepository usersRepository) {
        //TODO - Si es un factory por cada ViewModel, hay que eliminar el mRepository de aquí
        this.mRepository = repository;
        this.mUsersRepository = usersRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new LoginActivityViewModel(mUsersRepository);
        //return (T) new RegisterActivityViewModel(mUsersRepository);
    }
}