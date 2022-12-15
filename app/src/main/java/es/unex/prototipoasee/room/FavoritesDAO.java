package es.unex.prototipoasee.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;

import es.unex.prototipoasee.model.Favorites;

@Dao
public interface FavoritesDAO {

    @Insert
    void insertFavorites(Favorites favorites);

    @Delete
    void deleteFavorites(Favorites favorites);
}
