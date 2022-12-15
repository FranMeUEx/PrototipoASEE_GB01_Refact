package es.unex.prototipoasee.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import es.unex.prototipoasee.API.FilmsNetworkDataSource;
import es.unex.prototipoasee.repository.Repository;
import es.unex.prototipoasee.support.AppExecutors;
import es.unex.prototipoasee.R;
import es.unex.prototipoasee.sharedInterfaces.ItemDetailInterface;
import es.unex.prototipoasee.model.Films;
import es.unex.prototipoasee.room.FilmsDatabase;

public class ItemDetailInfoFragment extends Fragment {

    // Referencias a vistas de la UI
    private ImageView ivMoviePosterDetail;
    private TextView tvMovieTitleDetail;
    private TextView tvReleaseDateValueDetail;
    private TextView tvRatingAPIDetail;
    private TextView tvRatingValueDetail;
    private TextView tvMovieGenresValue;
    private TextView tvSynopsisValueDetail;
    private Button bToggleFavoriteDetail;
    private Button bTogglePendingDetail;

    private Repository repository;

    // Interfaz para comunicarse con la actividad ItemDetailActivity y obtener de ella la información básica de la película
    private ItemDetailInterface itemDetailInterface;

    // Objeto película con el que se recupera la información básica de la película seleccionada
    private Films film;

    public ItemDetailInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_detail_info, container, false);

        // Se obtiene la película de la que se quiere mostrar información
        film = itemDetailInterface.getFilmSelected();

        getViewsReferences(view);

        FilmsDatabase db = FilmsDatabase.getInstance(getContext());
        repository = Repository.getInstance(db.filmDAO(), db.favoritesDAO(), db.pendingsDAO(), db.commentDAO(), db.ratingDAO(), db.genreDAO(), db.filmsGenresListDAO(), FilmsNetworkDataSource.getInstance());

        // Se actualiza la IU con la información de la película recuperada en film
        updateUI();

        // Cuando se presiona en el botón de añadir/quitar de favoritos
        bToggleFavoriteDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filmInFavorites()){
                    repository.removeUserFavoriteFilm(film);
                    setFavButtonAdd();
                    Toast.makeText(getActivity(), R.string.toggle_favorites_remove, Toast.LENGTH_SHORT).show();
                } else {
                    repository.addUserFavoriteFilm(film);
                    setFavButtonRemove();
                    Toast.makeText(getActivity(), R.string.toggle_favorites_add, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Cuando se presiona en el botón de añadir/quitar de pendientes
        bTogglePendingDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filmInPendings()){
                    repository.removeUserPedingFilm(film);
                    setPendingButtonAdd();
                    Toast.makeText(getActivity(), R.string.toggle_pending_remove, Toast.LENGTH_SHORT).show();
                } else {
                    repository.addUserPendingFilm(film);
                    setPendingButtonRemove();
                    Toast.makeText(getActivity(), R.string.toggle_pending_add, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    /**
     * Se obtienen todas las referencias de los widgets presentes en la vista pertinente.
     * @param view Vista de la que se obtiene las referencias a las otras vistas o widgets que contiene.
     */
    private void getViewsReferences(View view) {
        ivMoviePosterDetail = view.findViewById(R.id.ivMoviePosterDetail);
        tvMovieTitleDetail = view.findViewById(R.id.tvMovieTitleDetail);
        tvReleaseDateValueDetail = view.findViewById(R.id.tvReleaseDateValueDetail);
        tvRatingAPIDetail = view.findViewById(R.id.tvRatingAPIDetail);
        tvRatingValueDetail = view.findViewById(R.id.tvRatingValueDetail);
        tvMovieGenresValue = view.findViewById(R.id.tvMovieGenresValue);
        tvSynopsisValueDetail = view.findViewById(R.id.tvSynopsisValueDetail);
        bToggleFavoriteDetail = view.findViewById(R.id.bToggleFavoriteDetail);
        bTogglePendingDetail = view.findViewById(R.id.bTogglePendingDetail);
    }

    /**
     * Actualiza todas las vistas incluidas en este fragmento que reflejan información sobre la película
     */
    private void updateUI(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(filmInFavorites()){
                    setFavButtonRemove();
                } else {
                    setFavButtonAdd();
                }
                if(filmInPendings()){
                    setPendingButtonRemove();
                } else {
                    setPendingButtonAdd();
                }
                Glide.with(getContext()).load("https://image.tmdb.org/t/p/original/"+film.getPosterPath()).into(ivMoviePosterDetail);
                tvMovieTitleDetail.setText(film.getTitle());
                tvReleaseDateValueDetail.setText(film.getReleaseDate());
                tvRatingAPIDetail.setText(String.valueOf(film.getVoteAverage()));
                tvSynopsisValueDetail.setText(film.getOverview());
                if(film.getTotalVotesMovieCheck()!=0){
                    tvRatingValueDetail.setText(String.valueOf(film.getTotalRatingMovieCheck()/film.getTotalVotesMovieCheck()));
                }else{
                    tvRatingValueDetail.setText(String.valueOf(0));
                }
                String genresAsString = String.join(" - ", repository.getFilmGenres(film));
                tvMovieGenresValue.setText(genresAsString);
            }
        });
    }

    /**
     * Modifica el aspecto del botón para añadir/quitar favorito si la película no está marcada como favorita.
     */
    private void setFavButtonAdd(){
        bToggleFavoriteDetail.setText(R.string.detail_add_favorites);
        bToggleFavoriteDetail.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_widgets));
    }

    /**
     * Modifica el aspecto del botón para añadir/quitar favorito si la película sí está marcada como favorita.
     */
    private void setFavButtonRemove() {
        bToggleFavoriteDetail.setText(R.string.remove_favorite);
        bToggleFavoriteDetail.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_widgets_darker));
    }

    /**
     * Modifica el aspecto del botón para añadir/quitar pendiente si la película sí está marcada como pendiente
     */
    private void setPendingButtonRemove() {
        bTogglePendingDetail.setText(R.string.remove_pending);
        bTogglePendingDetail.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_widgets_darker));
    }

    /**
     * Modifica el aspecto del botón para añadir/quitar pendiente si la película no está marcada como pendiente
     */
    private void setPendingButtonAdd() {
        bTogglePendingDetail.setText(R.string.detail_add_pendant);
        bTogglePendingDetail.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_widgets));
    }

    /**
     * Consulta las listas que mantienen la información viva del usuario respecto a sus películas favoritas para determinar si está o no
     * marcada como tal.
     * @return True si la película está marcada como favorita o False en caso contrario
     */
    private boolean filmInFavorites(){
        return repository.getUserFavoritesFilms().get(film.getId()) != null;
    }

    /**
     * Consulta la información viva del usuario respecto a sus películas pendientes y determina la presencia de la película en cuestión (film)
     * @return True si la película está marcada como pendiente o False en caso contrario
     */
    private boolean filmInPendings(){
        return repository.getUserPendingsFilms().get(film.getId()) != null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            itemDetailInterface = (ItemDetailInterface) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context + " must implement ItemDetailInterface");
        }
    }
}