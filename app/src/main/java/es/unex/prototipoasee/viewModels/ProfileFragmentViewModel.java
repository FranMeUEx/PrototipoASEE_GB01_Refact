package es.unex.prototipoasee.viewModels;

import androidx.lifecycle.ViewModel;

import es.unex.prototipoasee.model.User;
import es.unex.prototipoasee.repository.UsersRepository;

public class ProfileFragmentViewModel extends ViewModel {

    private final UsersRepository usersRepository;

    public ProfileFragmentViewModel(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public User getUser(String username) {
        return usersRepository.getUser(username);
    }
}
