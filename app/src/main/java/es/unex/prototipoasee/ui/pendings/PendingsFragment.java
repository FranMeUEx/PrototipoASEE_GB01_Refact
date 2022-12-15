package es.unex.prototipoasee.ui.pendings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.unex.prototipoasee.API.FilmsNetworkDataSource;
import es.unex.prototipoasee.repository.Repository;
import es.unex.prototipoasee.R;
import es.unex.prototipoasee.adapters.FilmListAdapter;
import es.unex.prototipoasee.model.Films;
import es.unex.prototipoasee.room.FilmsDatabase;

public class PendingsFragment extends Fragment {

    private List<Films> filmList = new ArrayList<>();
    private FilmListAdapter filmListAdapter;

    private Repository repository;

    public PendingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        FilmsDatabase db = FilmsDatabase.getInstance(getContext());
        repository = Repository.getInstance(db.filmDAO(), db.favoritesDAO(), db.pendingsDAO(), db.commentDAO(), db.ratingDAO(), db.genreDAO(), db.filmsGenresListDAO(), FilmsNetworkDataSource.getInstance());

        View v = inflater.inflate(R.layout.fragment_pendings, container, false);
        filmListAdapter = new FilmListAdapter(new ArrayList<>(repository.getUserPendingsFilms().values()),R.layout.pendings_item_list_content, getContext());

        View recyclerView = v.findViewById(R.id.fragment_pendings);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        return v;
    }

    // Se asigna a la lista los datos de las películas pendientes
    public void setupRecyclerView(@NonNull RecyclerView recyclerView){
        recyclerView.setAdapter(filmListAdapter);
    }

    @Override
    public void onResume() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                filmListAdapter.swap(new ArrayList<>(repository.getUserPendingsFilms().values()));
            }
        });
        super.onResume();
    }
}