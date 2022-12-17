package es.unex.prototipoasee.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import es.unex.prototipoasee.AppContainer;
import es.unex.prototipoasee.MyApplication;
import es.unex.prototipoasee.R;
import es.unex.prototipoasee.adapters.FilmListAdapter;
import es.unex.prototipoasee.viewModels.FavoritesFragmentViewModel;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    private FilmListAdapter filmListAdapter;

    // Referencia al ViewModel
    FavoritesFragmentViewModel favoritesFragmentViewModel;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Para el ViewModel
        AppContainer appContainer = ((MyApplication) getActivity().getApplication()).appContainer;
        favoritesFragmentViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) appContainer.factory).get(FavoritesFragmentViewModel.class);

        View v = inflater.inflate(R.layout.fragment_favorites, container, false);
        filmListAdapter = new FilmListAdapter(new ArrayList<>(favoritesFragmentViewModel.getUserFavoritesFilms()),R.layout.favorites_item_list_content, getContext());

        View recyclerView = v.findViewById(R.id.fragment_favorites);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        return v;
    }

    // Se asigna a la lista los datos del dummy
    public void setupRecyclerView(@NonNull RecyclerView recyclerView){
        recyclerView.setAdapter(filmListAdapter);
    }

    @Override
    public void onResume() {
        getActivity().runOnUiThread(() -> filmListAdapter.swap(new ArrayList<>(favoritesFragmentViewModel.getUserFavoritesFilms())));
        super.onResume();
    }
}