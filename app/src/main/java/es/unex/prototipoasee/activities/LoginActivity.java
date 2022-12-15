package es.unex.prototipoasee.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import es.unex.prototipoasee.AppContainer;
import es.unex.prototipoasee.MyApplication;
import es.unex.prototipoasee.R;
import es.unex.prototipoasee.viewmodels.LoginActivityViewModel;

public class LoginActivity extends AppCompatActivity {

    // Referencias a vistas de la UI
    private EditText etUserLogin;
    private EditText etPasswordLogin;
    private TextView tvRegisterLogin;
    private Button bLogin;

    // Referencia al ViewModel
    LoginActivityViewModel loginActivityViewModel;

    // Objeto de preferencias para almacenar el nombre de usuario y permitir un inicio de sesión automático
    private SharedPreferences loginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.login_bar_title);

        // Para el View Model
        AppContainer appContainer = ((MyApplication) getApplication()).appContainer;
        loginActivityViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) appContainer.factory).get(LoginActivityViewModel.class);

        // Se accede a las preferencias almacenadas en la App
        loginPreferences = getSharedPreferences(getPackageName()+"_preferences", Context.MODE_PRIVATE);

        // Se intenta iniciar sesión con las preferencias recuperadas
        autoLogin();

        getViewsReferences();

        // Se presiona sobre la cadena de Registro
        tvRegisterLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se inicia la actividad de RegisterActivity esperando que devuelva como resultado el nombre de usuario que se haya registrado
                startRegisterActivityForResult();
            }
        });

        // Se hace click al botón de Inicio de sesión
        bLogin.setOnClickListener(this::logIn);
    }

    /**
     * Comprueba si existe un valor para la preferencia que almacena el usuario al que corresponde la sesión.
     * En caso afirmativo, se inicia la actividad HomeActivity y se mantiene la sesión del usuario referenciado.
     */
    public void autoLogin(){
        if(!loginPreferences.getString("USERNAME", "").equals("")){
            // El usuario ya se ha loggeado o registrado anteriormente en el dispositivo y aún no ha cerrado sesión
            // Inicio de sesión automático, es decir, el usuario no introduce sus credenciales y se usa el valor de la preferencia como referencia al usuario loggeado
            Toast.makeText(this, getString(R.string.auto_login) + " " + loginPreferences.getString("USERNAME", ""), Toast.LENGTH_SHORT).show();
            startHomeActivity();
        }
    }

    /**
     * Método para obtener las referencias a cada una de las vistas que forman parte de la UI de la actividad.
     */
    private void getViewsReferences() {
        etUserLogin = findViewById(R.id.etUserLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        tvRegisterLogin = findViewById(R.id.tvRegisterLogin);
        bLogin = findViewById(R.id.bLogin);
    }

    /**
     * Inicia la actividad HomeActivity y finaliza la actividad actual para eliminarla de la pila de Back, de forma que no se vuelva al login
     * si el usuario presiona el botón Back desde la actividad HomeActivity.
     */
    private void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Inicia la actividad RegisterActivity a la espera de que esta devuelva como resultado el nombre de usuario que se registre.
     * La respuesta recibida es gestionada a través de registerLaucher.
     */
    private void startRegisterActivityForResult(){
        Intent intent = new Intent(this, RegisterActivity.class);
        registerLauncher.launch(intent);
    }

    /**
     * Recupera la respuesta devuelta por RegisterActivity. Si el resultado ha sido satisfactorio (se ha devuelto el nombre de usuario - RESULT_OK),
     * se obtiene el valor de dicho nombre para guardarlo en la preferencia correspondiente e iniciar la actividad HomeActivity con la nueva sesión.
     * En caso contrario no se hace nada, ya que el usuario puede volver a RegisterActivity o presionar Back para salir de la App.
     */
    ActivityResultLauncher<Intent> registerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        assert result.getData() != null;
                        String username = result.getData().getExtras().get("USERNAME").toString();
                        saveUserPreferences(username);
                        Toast.makeText(LoginActivity.this, getString(R.string.auto_login) + " " + username, Toast.LENGTH_SHORT).show();
                        startHomeActivity();
                    }
                }
            });

    /**
     * Edita las preferencias compartidas para actualizar el nombre de usuario al que se asocia la sesión.
     */
    private void saveUserPreferences(String username){
        SharedPreferences.Editor editPreferences = loginPreferences.edit();
        editPreferences.putString("USERNAME", username);
        editPreferences.apply();
    }

    /**
     * Si existe la cuenta asociada a las credenciales introducidas, almacena el nombre de usuario en las preferencias e inicia la actividad
     * HomeActivity con la nueva sesión.
     */
    private void logIn(View view){
        String username = etUserLogin.getText().toString();
        String password = etPasswordLogin.getText().toString();
        if (loginActivityViewModel.accountExists(username, password)) {
            saveUserPreferences(etUserLogin.getText().toString());
            startHomeActivity();
        } else {
            Snackbar.make(view, R.string.wrong_credentials, 3500)
                    .setTextMaxLines(10).show();
        }
    }
}