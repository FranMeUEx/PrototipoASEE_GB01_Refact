package es.unex.prototipoasee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import es.unex.prototipoasee.API.FilmsNetworkDataSource;
import es.unex.prototipoasee.model.Genre;
import es.unex.prototipoasee.repository.Repository;
import es.unex.prototipoasee.support.AppExecutors;
import es.unex.prototipoasee.R;
import es.unex.prototipoasee.adapters.CommentAdapter;
import es.unex.prototipoasee.adapters.TabsViewPagerAdapter;
import es.unex.prototipoasee.sharedInterfaces.ItemDetailInterface;
import es.unex.prototipoasee.model.Comments;
import es.unex.prototipoasee.model.Films;
import es.unex.prototipoasee.room.FilmsDatabase;

public class ItemDetailActivity extends AppCompatActivity implements ItemDetailInterface, CommentAdapter.DeleteCommentInterface {

    // Objetos necesarios para la gestión de los Tabs
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    // Objeto película con el que se recupera la información básica de la película seleccionada
    //private LiveData<Films> film;
    private Films film;

    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        setTitle(R.string.detail_title);

        tabLayout = findViewById(R.id.tlDetail);
        viewPager2 = findViewById(R.id.vpDetail);

        FilmsDatabase db = FilmsDatabase.getInstance(this);
        repository = Repository.getInstance(db.filmDAO(), db.favoritesDAO(), db.pendingsDAO(), db.commentDAO(), db.ratingDAO(), db.genreDAO(), db.filmsGenresListDAO(), FilmsNetworkDataSource.getInstance());

        // Se obtiene la película de la que se quiere mostrar información
        film = (Films) getIntent().getSerializableExtra("FILM");

        repository.getFilm(film);

        viewPager2.setAdapter(new TabsViewPagerAdapter(this.getSupportFragmentManager(), getLifecycle()));
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * Devuelve la película seleccionada. Se emplea para la comunicación con los fragmentos que implementan las tabs.
     * @return Película que ha sido seleccionada y de la que se quiere mostrar la información de detalle
     */
    @Override
    public Films getFilmSelected() {
        return film;
    }

    @Override
    public void deleteComment(Comments comment, CommentAdapter commentAdapter) {
        repository.deleteComment(comment);
    }
}