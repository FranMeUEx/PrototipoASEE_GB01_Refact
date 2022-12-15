package es.unex.prototipoasee.ui.explore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.unex.prototipoasee.API.FilmsNetworkDataSource;
import es.unex.prototipoasee.repository.Repository;
import es.unex.prototipoasee.sharedInterfaces.OnFilmsLoadedListener;
import es.unex.prototipoasee.sharedInterfaces.OnGenresLoadedListener;
import es.unex.prototipoasee.support.LevenshteinSearch;
import es.unex.prototipoasee.R;
import es.unex.prototipoasee.adapters.FilmAdapter;
import es.unex.prototipoasee.model.Films;
import es.unex.prototipoasee.model.Genre;
import es.unex.prototipoasee.room.FilmsDatabase;

public class ExploreFragment extends Fragment implements OnFilmsLoadedListener, OnGenresLoadedListener {

    // Referencias a vistas
    private SearchView svSearchFilm;
    private ImageButton ibResetFilms;
    private ChipGroup cgGenreFilter;

    private List<Films> filmList = new ArrayList<>();

    // Adaptador para las películas de la RecyclerView
    private FilmAdapter filmAdapter;

    // Campo para trabajar con el Repositorio
    private Repository repository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        getViewReferences(view);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_explore);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        filmAdapter = new FilmAdapter(filmList, R.layout.explore_item_grid_content, getContext());
        recyclerView.setAdapter(filmAdapter);

        FilmsDatabase db = FilmsDatabase.getInstance(getContext());
        repository = Repository.getInstance(db.filmDAO(), db.favoritesDAO(), db.pendingsDAO(), db.commentDAO(), db.ratingDAO(), db.genreDAO(), db.filmsGenresListDAO(), FilmsNetworkDataSource.getInstance());
        repository.getCurrentFilms().observe(getActivity(), new Observer<List<Films>>() {
            @Override
            public void onChanged(List<Films> films) {
                onFilmsLoaded(films);
            }
        });
        repository.getCurrentGenres().observe(getActivity(), new Observer<List<Genre>>() {
            @Override
            public void onChanged(List<Genre> genres) {
                onGenresLoaded(genres);
            }
        });
        repository.getFilmsByGenre().observe(getActivity(), new Observer<List<Films>>() {
            @Override
            public void onChanged(List<Films> films) {
                onFilmsLoaded(films);
            }
        });
        repository.getTitleFilterLiveData().observe(getActivity(), new Observer<List<Films>>() {
            @Override
            public void onChanged(List<Films> films) {
                onFilmsLoaded(films);
            }
        });

        svSearchFilm.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                repository.getFilmsByTitle(query);
                return false;
            }
        });

        ibResetFilms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repository.forceFetchFilmsAndGenres();
            }
        });

        return view;
    }

    private void getViewReferences(View view) {
        svSearchFilm = view.findViewById(R.id.svSearchFilm);
        ibResetFilms = view.findViewById(R.id.ibResetFilms);
        cgGenreFilter = view.findViewById(R.id.cgGenreFilter);
    }

    private void loadChipFilters(List<Genre> genres) {
        for (Genre genre : genres) {
            Chip chip = new Chip(getContext());
            chip.setText(genre.getName());
            chip.setChipBackgroundColorResource(R.color.gray);
            chip.setCloseIconVisible(false);
            cgGenreFilter.addView(chip);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadGenreFilms(genre.getId());
                }
            });
        }

    }

    // Llamado al buscar por filtro
    private void loadGenreFilms(int genreId) {
        filmAdapter.clear();
        repository.setGenre(genreId);
    }

    @Override
    public void onFilmsLoaded(List<Films> films) {
        Log.d("Repository", "Pelis actualizadas");
        getActivity().runOnUiThread(() -> filmAdapter.swap(films));
    }

    @Override
    public void onGenresLoaded(List<Genre> genres) {
        Collections.sort(genres);
        getActivity().runOnUiThread(() -> loadChipFilters(genres));
    }

}