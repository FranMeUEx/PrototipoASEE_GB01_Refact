package es.unex.prototipoasee.viewModels;

import androidx.lifecycle.ViewModel;

import es.unex.prototipoasee.repository.UsersRepository;

public class DeleteAccountActivityViewModel extends ViewModel {

    private final UsersRepository usersRepository;

    public DeleteAccountActivityViewModel(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public void deleteAccount(String username) {
        usersRepository.deleteUser(username);
    }
}
