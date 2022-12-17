
package es.unex.prototipoasee.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "Genres", indices = {@Index(value = {"genreID"}, unique = true)})
public class Genre implements Comparable<Genre> {

    @PrimaryKey
    @ColumnInfo(name = "genreID")
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;

    public Genre(){

    }

    public Genre(int genreID, String name){
        this.id = genreID;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Genre genre) {
        return this.getName().compareTo(genre.getName());
    }

}
