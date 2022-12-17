
package es.unex.prototipoasee.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GenresList {

    @SerializedName("genres")
    @Expose
    private List<Genre> genres;

    public GenresList(List<Genre> list){
        genres = list;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}
