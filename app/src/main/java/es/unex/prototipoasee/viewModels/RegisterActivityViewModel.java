package es.unex.prototipoasee.viewModels;

import android.text.TextUtils;
import android.util.Patterns;
import androidx.lifecycle.ViewModel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import es.unex.prototipoasee.model.User;
import es.unex.prototipoasee.repository.UsersRepository;

/**
 * {@link ViewModel} for {@link es.unex.prototipoasee.activities.RegisterActivity}
 */
public class RegisterActivityViewModel extends ViewModel {
    private final UsersRepository usersRepository;

    public RegisterActivityViewModel(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    /**
     * Comprueba si el nombre de usuario introducido existe ya en la BD.
     * Retorna True si ya existe o False en caso contrario.
     * Se hace uso de los mapas para ahorrar transacciones con la BD.
     */
    public boolean usernameInDB(String username) {
        return usersRepository.getUsersByUsername().get(username) != null;
    }

    /**
     * Comprueba si el correo electrónico introducido existe ya en la BD.
     * Retorna True si ya existe o False en caso contrario.
     * Se hace uso de los mapas para ahorrar transacciones con la BD.
     */
    public boolean emailInDB(String email) {
        return usersRepository.getUsersByEmail().get(email) != null;
    }

    /**
     * Inserta un nuevo usuario en la BD con los datos obtenidos una vez validados.
     */
    public void insertUserInDB(String username, String email, String password) {
        User user = new User(username, email, password);
        usersRepository.registerNewUser(user);
    }

    /**
     * Comprueba si el nombre de usuario introducido cumple con el patrón indicado.
     * Retorna True si el nombre es válido o False en caso contrario.
     */
    public boolean usernameIsValid(String username) {
        Pattern pattern;
        Matcher matcher;

        // Un nombre de usuario válido debe contener al menos una letra y su longitud debe ser mayor o igual que 6 y menor o igual que 16. No se permiten espacios.
        final String USERNAME_PATTERN = "^(?=.*[a-zA-Z])(?=\\S+$).{6,12}$";

        pattern = Pattern.compile(USERNAME_PATTERN);
        matcher = pattern.matcher(username);

        return matcher.matches();
    }

    /**
     * Comprueba si el correo electrónico introducido cumple con el patrón indicado.
     * Retorna True si es válido o False en caso contrario.
     */
    public boolean emailIsValid(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    /**
     * Comprueba si la contraseña introducida cumple con el patrón indicado.
     * Retorna True si es válida o False en caso contrario.
     */
    public boolean passwordIsValid(String password) {
        Pattern pattern;
        Matcher matcher;

        // Una contraseña válida debe contener como mínimo un dígito, una letra minúscula y una letra mayúscula. Además, no se permiten espacios y la longitud debe ser mayor o igual que 6, pero menor o igual que 16
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,16}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    /**
     * Comprueba si las dos constraseñas introducidas coinciden.
     */
    public boolean passwordCoincidence(String password, String repeatedPassword) {
        return password.equals(repeatedPassword);
    }
}
