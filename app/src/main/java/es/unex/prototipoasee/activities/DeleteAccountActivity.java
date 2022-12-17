package es.unex.prototipoasee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import es.unex.prototipoasee.AppContainer;
import es.unex.prototipoasee.MyApplication;
import es.unex.prototipoasee.R;
import es.unex.prototipoasee.viewModels.DeleteAccountActivityViewModel;

public class DeleteAccountActivity extends AppCompatActivity {

    // Referencias a vistas de la UI
    private Button bDelete;
    private Button bCancelDelete;

    // Objeto de preferencias para almacenar el nombre de usuario y permitir un inicio de sesión automático
    SharedPreferences loginPreferences;

    // Referencia al ViewModel
    DeleteAccountActivityViewModel deleteAccountActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);
        setTitle(R.string.delete_account_bar_title);

        // Para el ViewModel
        AppContainer appContainer = ((MyApplication) getApplication()).appContainer;
        deleteAccountActivityViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) appContainer.factory).get(DeleteAccountActivityViewModel.class);

        getViewsReferences();

        // Se presiona en Cancelar
        bCancelDelete.setOnClickListener(view -> {
            // Se finaliza la actividad para eliminarla de la pila de Back y volver a la actividad anterior sin eliminar la cuenta de la BD
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        });

        // Se presiona en Borrar la cuenta
        bDelete.setOnClickListener(view -> deleteAccount());
    }

    /**
     * Método para obtener las referencias a cada una de las vistas que forman parte de la UI de la actividad.
     */
    private void getViewsReferences() {
        bDelete = findViewById(R.id.bDelete);
        bCancelDelete = findViewById(R.id.bCancelDelete);
    }

    /**
     * Elimina las preferencias de usuario actuales y borra al usuario de la BD, devolviendo el resultado a la actividad que lo invoca (HomeActivity)
     */
    private void deleteAccount() {
        // Se recupera el nombre de usuario loggeado
        loginPreferences = getSharedPreferences(getPackageName() + "_preferences", Context.MODE_PRIVATE);
        String username = loginPreferences.getString("USERNAME", "");

        // Se elimina el usuario de la BD dado su nombre
        deleteAccountActivityViewModel.deleteAccount(username);

        // Se eliminan las preferencias para evitar un login automático
        removePreferences();

        // Se devuelve el control a la actividad anterior tras eliminar el usuario de la BD
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Elimina las preferencias compartidas asociadas al usuario que tiene la sesión abierta.
     */
    private void removePreferences() {
        SharedPreferences.Editor editPreferences = loginPreferences.edit();
        editPreferences.remove("USERNAME");
        editPreferences.apply();
    }
}