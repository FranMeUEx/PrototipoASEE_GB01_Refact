package es.unex.prototipoasee.sharedInterfaces;

import java.util.List;

import es.unex.prototipoasee.model.Films;

public interface OnFilmsLoadedListener {
    void onFilmsLoaded(List<Films> films);
}
