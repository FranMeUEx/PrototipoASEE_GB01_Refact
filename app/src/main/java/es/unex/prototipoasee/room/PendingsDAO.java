package es.unex.prototipoasee.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import es.unex.prototipoasee.model.Pendings;

@Dao
public interface PendingsDAO {

    @Insert
    void insertPendings(Pendings pendings);

    @Delete
    void deletePendings(Pendings pendings);
}
