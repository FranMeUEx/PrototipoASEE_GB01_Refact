package es.unex.prototipoasee.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import es.unex.prototipoasee.R;
import es.unex.prototipoasee.model.Films;

public class FilmListAdapter extends RecyclerView.Adapter<FilmListAdapter.FilmsListAdapterViewHolder>{
    private List<Films> filmList;
    private final FilmAdapter.FilmListener filmListener;
    private final FilmListAdapter.ActionButtonListener actionButtonListener;
    /*
     * Campo para pasarle al Adapter el layout sobre el que actuar. Esto implica que los IDs de las Views de cada layout deben ser iguales (ej: ivMoviePoster)
     * Los layouts 'afectados' son explore_item_grid_content.xml, favorites_item_list_content.xml y pendings_item_list_content.xml
     * Se trata de un Int ya que el ID del layout se resuelve como un entero.
     */
    private final int listItemLayout;

    public FilmListAdapter(List<Films> list, int listItemLayout, Context context) {
        this.filmList = list;
        this.listItemLayout = listItemLayout;
        filmListener = (FilmAdapter.FilmListener) context;
        actionButtonListener = (FilmListAdapter.ActionButtonListener) context;
    }

    @NonNull
    @Override
    public FilmListAdapter.FilmsListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(this.listItemLayout, parent,false);
        return new FilmListAdapter.FilmsListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmListAdapter.FilmsListAdapterViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(filmList.get(position).getTitle());
        Glide.with(holder.image.getContext()).load("https://image.tmdb.org/t/p/original/"+filmList.get(position).getPosterPath()).into(holder.image);
        holder.date.setText(filmList.get(position).getReleaseDate().split("-")[0]);
        holder.actionButton.setOnClickListener(view -> {
            if(listItemLayout == R.layout.favorites_item_list_content){
                actionButtonListener.onFavButtonPressed(filmList.get(position), FilmListAdapter.this);
                Toast.makeText(view.getContext(), R.string.toggle_favorites_remove, Toast.LENGTH_SHORT).show();
            } else {
                actionButtonListener.onPendingButtonPressed(filmList.get(position), FilmListAdapter.this);
                Toast.makeText(view.getContext(), R.string.toggle_pending_remove, Toast.LENGTH_SHORT).show();
            }
        });
        holder.itemView.setOnClickListener(view -> filmListener.onFilmSelected(filmList.get(position)));
    }

    @Override
    public int getItemCount() {
        return filmList.size();
    }

    public static class FilmsListAdapterViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final ImageView image;
        final TextView date;
        final ImageButton actionButton;

        FilmsListAdapterViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.tvMovieTitle);
            image = view.findViewById(R.id.ivMoviePoster);
            date = view.findViewById(R.id.tvMovieDate);
            actionButton = view.findViewById(R.id.ibAction);
        }
    }

    public void swap(List<Films> filmList){
        this.filmList = filmList;
        notifyDataSetChanged();
    }

    public interface ActionButtonListener{
        void onFavButtonPressed(Films film, FilmListAdapter filmListAdapter);
        void onPendingButtonPressed(Films film, FilmListAdapter filmListAdapter);
    }
}
