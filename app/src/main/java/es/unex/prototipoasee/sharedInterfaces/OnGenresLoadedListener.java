package es.unex.prototipoasee.sharedInterfaces;

import java.util.List;

import es.unex.prototipoasee.model.Genre;

public interface OnGenresLoadedListener {
    void onGenresLoaded(List<Genre> genres);
}
