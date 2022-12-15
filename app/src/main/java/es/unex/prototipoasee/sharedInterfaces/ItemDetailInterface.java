package es.unex.prototipoasee.sharedInterfaces;

import java.util.List;

import es.unex.prototipoasee.model.Comments;
import es.unex.prototipoasee.model.Films;
import es.unex.prototipoasee.model.Genre;

// Interfaz para comunicar este Fragment con su actividad (ItemDetailActivity)
public interface ItemDetailInterface{
    Films getFilmSelected();
    //List<Comments> getCommentList();
    //List<String> getGenreList();
}
