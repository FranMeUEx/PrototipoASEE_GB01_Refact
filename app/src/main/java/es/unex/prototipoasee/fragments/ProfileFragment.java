package es.unex.prototipoasee.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import es.unex.prototipoasee.AppContainer;
import es.unex.prototipoasee.MyApplication;
import es.unex.prototipoasee.R;
import es.unex.prototipoasee.databinding.FragmentProfileBinding;
import es.unex.prototipoasee.model.User;
import es.unex.prototipoasee.viewModels.ProfileFragmentViewModel;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileListener profileListener;

    // Campo para el acceso a las preferencias de usuario compartidas
    private SharedPreferences loginPreferences;

    // Referencias a vistas de la UI
    private ImageButton ibAppInfo;
    private Button bLogOut;
    private Button bDeleteAccount;
    private TextView tvUsernameValueProfile;
    private TextView tvEmailValueProfile;
    private TextView tvPasswordValueProfile;

    // Referencia al ViewModel
    ProfileFragmentViewModel profileFragmentViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Para el ViewModel
        AppContainer appContainer = ((MyApplication) getActivity().getApplication()).appContainer;
        profileFragmentViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) appContainer.factory).get(ProfileFragmentViewModel.class);

        getViewsReferences(root);

        updateUI();

        // Se hace click en el botón para mostrar la información de la App
        ibAppInfo.setOnClickListener(view -> profileListener.onInfoButtonPressed());

        // Se hace click en el botón para Cerrar sesión
        bLogOut.setOnClickListener(view -> logOut());

        // Se hace click en el botón para Eliminar cuenta
        bDeleteAccount.setOnClickListener(view -> profileListener.onDeleteAccountButtonPressed());

        return root;
    }

    /**
     * Método para obtener las referencias a cada una de las vistas que forman parte de la UI del fragmento.
     */
    private void getViewsReferences(View view) {
        ibAppInfo = view.findViewById(R.id.ibAppInfo);
        bLogOut = view.findViewById(R.id.bLogOut);
        bDeleteAccount = view.findViewById(R.id.bDeleteAccount);
        tvUsernameValueProfile = view.findViewById(R.id.tvUsernameValueProfile);
        tvEmailValueProfile = view.findViewById(R.id.tvEmailValueProfile);
        tvPasswordValueProfile = view.findViewById(R.id.tvPasswordValueProfile);
    }

    /**
     * Resetea las preferencias compartidas para eliminar la referencia al usuario y llama al método de la interfaz ProfileListener
     * definido para controlar el comportamiento del botón de cierre de sesión desde HomeActivity.
     */
    private void logOut() {
        loginPreferences = getActivity().getSharedPreferences(getActivity().getPackageName()+"_preferences", Context.MODE_PRIVATE);
        removeUserPreference(loginPreferences);
        profileListener.onLogoutButtonPressed();
    }

    /**
     * Elimina la preferencia empleada para referenciar el nombre de usuario del que se mantiene la sesión.
     */
    private void removeUserPreference(SharedPreferences loginPreferences){
        SharedPreferences.Editor editPreferences = loginPreferences.edit();
        editPreferences.remove("USERNAME");
        editPreferences.apply();
    }

    private void updateUI(){
        loginPreferences = getActivity().getSharedPreferences(getActivity().getPackageName()+"_preferences", Context.MODE_PRIVATE);
        User user = profileFragmentViewModel.getUser(loginPreferences.getString("USERNAME", ""));
        getActivity().runOnUiThread(() -> {
            tvUsernameValueProfile.setText(user.getUsername());
            tvEmailValueProfile.setText(user.getEmail());
            tvPasswordValueProfile.setText(user.getPassword());
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            profileListener = (ProfileListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context + " must implement InfoButtonListener");
        }
    }

    /**
     * Interfaz para comunicar el fragmento con su actividad (HomeActivity)
     */
    public interface ProfileListener{
        void onInfoButtonPressed();
        void onLogoutButtonPressed();
        void onDeleteAccountButtonPressed();
    }
}
