package es.unex.prototipoasee.viewModels;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import es.unex.prototipoasee.repository.Repository;
import es.unex.prototipoasee.repository.UsersRepository;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Repository repository;
    private final UsersRepository usersRepository;

    public ViewModelFactory(Repository repository, UsersRepository usersRepository) {
        this.repository = repository;
        this.usersRepository = usersRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (LoginActivityViewModel.class.isAssignableFrom(modelClass)) {
            return (T) new LoginActivityViewModel(usersRepository);
        } else if (RegisterActivityViewModel.class.isAssignableFrom(modelClass)) {
            return (T) new RegisterActivityViewModel(usersRepository);
        } else if (DeleteAccountActivityViewModel.class.isAssignableFrom(modelClass)) {
            return (T) new DeleteAccountActivityViewModel(usersRepository);
        } else if (HomeActivityViewModel.class.isAssignableFrom(modelClass)) {
            return (T) new HomeActivityViewModel(repository);
        } else if (ProfileFragmentViewModel.class.isAssignableFrom(modelClass)) {
            return (T) new ProfileFragmentViewModel(usersRepository);
        } else if (FavoritesFragmentViewModel.class.isAssignableFrom(modelClass)) {
            return (T) new FavoritesFragmentViewModel(repository);
        } else if (PendingsFragmentViewModel.class.isAssignableFrom(modelClass)) {
            return (T) new PendingsFragmentViewModel(repository);
        } else if (ExploreFragmentViewModel.class.isAssignableFrom(modelClass)) {
            return (T) new ExploreFragmentViewModel(repository);
        } else if (ItemDetailActivityViewModel.class.isAssignableFrom(modelClass)) {
            return (T) new ItemDetailActivityViewModel(repository);
        } else if (ItemDetailInfoFragmentViewModel.class.isAssignableFrom(modelClass)) {
            return (T) new ItemDetailInfoFragmentViewModel(repository);
        } else if (ItemDetailSocialFragmentViewModel.class.isAssignableFrom(modelClass)) {
            return (T) new ItemDetailSocialFragmentViewModel(repository);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
    }
}