package es.unex.prototipoasee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import es.unex.prototipoasee.AppContainer;
import es.unex.prototipoasee.MyApplication;
import es.unex.prototipoasee.R;
import es.unex.prototipoasee.viewModels.RegisterActivityViewModel;

public class RegisterActivity extends AppCompatActivity {

    // Referencias a vistas de la UI
    private EditText etUsernameRegister;
    private EditText etEmailRegister;
    private EditText etPasswordRegister;
    private EditText etRepeatPasswordRegister;
    private Button bRegister;

    // Referencia al ViewModel
    RegisterActivityViewModel registerActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle(R.string.register_bar_title);

        getViewsReferences();

        // Para el ViewModel
        AppContainer appContainer = ((MyApplication) getApplication()).appContainer;
        registerActivityViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) appContainer.factory).get(RegisterActivityViewModel.class);

        bRegister.setOnClickListener(view -> {
            // Se procede a registrar al usuario
            bRegister.setEnabled(false);
            registerUserIfValid(view);
        });
    }

    /**
     * Método para obtener las referencias a cada una de las vistas que forman parte de la UI de la actividad.
     */
    private void getViewsReferences() {
        etUsernameRegister = findViewById(R.id.etUsernameRegister);
        etEmailRegister = findViewById(R.id.etEmailRegister);
        etPasswordRegister = findViewById(R.id.etPasswordRegister);
        etRepeatPasswordRegister = findViewById(R.id.etRepeatPasswordRegister);
        bRegister = findViewById(R.id.bRegister);
    }

    /**
     * Comprueba si las credenciales son válidas. En caso afirmativo, se inserta el nuevo usuario en la BD y se devuelve el control a LoginActivity.
     */
    private void registerUserIfValid(View view) {
        String username = etUsernameRegister.getText().toString();
        String email = etEmailRegister.getText().toString();
        String password = etPasswordRegister.getText().toString();
        String repeatedPassword = etRepeatPasswordRegister.getText().toString();
        // Si los campos introducidos son válidos, se inserta el usuario en la BD y se devuelve el nombre de usuario a la actividad LoginActivity
        if (checkFields(view, username, email, password, repeatedPassword)) {
            // Se da de alta al usuario en el sistema (se añade a la BD)
            registerActivityViewModel.insertUserInDB(username, email, password);
            // Se devuelve el nombre de usuario a la actividad LoginActivity
            returnToLogin();
        } else {
            view.setEnabled(true);
        }
    }

    /**
     * Realiza las comprobaciones del formato de cada uno de los campos requeridos para el registro. Adcionalmente, comprueba que las credenciales no
     * existan ya en la BD.
     * @param view Referencia a la vista para hacer uso de la Snackbar para informar al usuario de los inconvenientes encontrados.
     * @param username Nombre de usuario que se quiere registrar.
     * @param email Email de usuario que se quiere registrar.
     * @param password Contraseña de usuario que se quiere registrar.
     * @param repeatedPassword Repetición de la contraseña que se quiere registrar.
     * @return True si las credenciales son válidas y no existen en la BD. False en caso contrario.
     */
    private boolean checkFields(View view, String username, String email, String password, String repeatedPassword) {
        // Se comprueba cada opción en un IF independiente para no solapar los mensajes de la Snackbar
        // Además, una vez que una de las opciones no es correcta, el resto se obvian y no se comprueban
        // No se ha optado por IF anidados para mejorar la lectura del código. Para ello se para la ejecución del método con returns en cada uno de los IF.

        // Atributos para personalizar la Snackbar empleada al verificar los datos introducidos
        int snackbarMaxLines = 10; // Número máximo de líneas a mostrar en la Snackbar
        int snackbarTimeLong = 6000; // Duración larga en milisegundos de la Snackbar en pantalla
        int snackbarTimeShort = 3500; // Duración corta en milisegundos de la Snackbar en pantalla

        if (!registerActivityViewModel.usernameIsValid(username)) {
            Snackbar.make(view, R.string.username_is_invalid, snackbarTimeLong)
                    .setTextMaxLines(snackbarMaxLines).show();
            return false;
        }
        if (registerActivityViewModel.usernameInDB(username)) {
            Snackbar.make(view, R.string.username_already_exists, snackbarTimeShort)
                    .setTextMaxLines(snackbarMaxLines).show();
            return false;
        }

        if (registerActivityViewModel.emailInDB(email)) {
            Snackbar.make(view, R.string.email_already_exists, snackbarTimeShort)
                    .setTextMaxLines(snackbarMaxLines).show();
            return false;
        }

        if (!registerActivityViewModel.emailIsValid(email)) {
            Snackbar.make(view, R.string.email_is_invalid, snackbarTimeShort)
                    .setTextMaxLines(snackbarMaxLines).show();
            return false;
        }

        if (!registerActivityViewModel.passwordIsValid(password)) {
            Snackbar.make(view, R.string.password_is_invalid, snackbarTimeLong)
                    .setTextMaxLines(snackbarMaxLines).show();
            return false;
        }

        if (!registerActivityViewModel.passwordCoincidence(password, repeatedPassword)) {
            Snackbar.make(view, R.string.password_coincidence_missing, snackbarTimeShort)
                    .setTextMaxLines(snackbarMaxLines).show();
            return false;
        }
        return true;
    }

    /**
     * Devuelve el control a la actividad LoginActivity pasándole el nombre de usuario que será utilizado para actualizazr las preferencias compartidas.
     */
    private void returnToLogin() {
        Intent intent = new Intent();
        intent.putExtra("USERNAME", etUsernameRegister.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}