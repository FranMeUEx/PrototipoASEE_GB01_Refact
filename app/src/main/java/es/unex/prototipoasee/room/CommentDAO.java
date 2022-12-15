package es.unex.prototipoasee.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import es.unex.prototipoasee.model.Comments;

@Dao
public interface CommentDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllComment(List<Comments> commentsList);

    @Query("DELETE FROM Comments WHERE filmID = (:filmID) AND username = (:username)")
    void deleteCommentsUserFilm(int filmID, String username);

    @Query("SELECT * FROM Comments WHERE filmID = (:filmID)")
    LiveData<List<Comments>> getFilmComments(int filmID);

    @Insert
    void addComment(Comments comments);

    @Delete
    void deleteComment(Comments comment);
}
