package es.unex.prototipoasee.repository;

import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.unex.prototipoasee.API.FilmsNetworkDataSource;
import es.unex.prototipoasee.model.Comments;
import es.unex.prototipoasee.model.Favorites;
import es.unex.prototipoasee.model.Films;
import es.unex.prototipoasee.model.Genre;
import es.unex.prototipoasee.model.Pendings;
import es.unex.prototipoasee.model.Rating;
import es.unex.prototipoasee.room.CommentDAO;
import es.unex.prototipoasee.room.FavoritesDAO;
import es.unex.prototipoasee.room.FilmDAO;
import es.unex.prototipoasee.room.FilmsGenresListDAO;
import es.unex.prototipoasee.room.GenreDAO;
import es.unex.prototipoasee.room.PendingsDAO;
import es.unex.prototipoasee.room.RatingDAO;
import es.unex.prototipoasee.support.AppExecutors;
import es.unex.prototipoasee.support.LevenshteinSearch;

/**
 * Repositorio agregado para la gestión de la información obtenida de la API (LiveData de Films y Genres), y para la gestión de información cacheada de forma
 * local, como son los usuarios y sus películas favoritas y pendientes, y las valoraciones y comentarios propios de MovieCheck.
 */
public class Repository {

    private static final String LOG_TAG = Repository.class.getSimpleName();

    // For Singleton instantiation
    private static Repository sInstance;

    private final FilmDAO filmDAO;
    private final GenreDAO genreDAO;
    private final RatingDAO ratingDAO;
    private final FavoritesDAO favoritesDAO;
    private final PendingsDAO pendingsDAO;
    private final CommentDAO commentDAO;
    private final FilmsGenresListDAO filmsGenresListDAO;

    // Estructuras para la gestión de la información del usuario en relación a las películas
    private HashMap<Integer, Films> userPendingFilms = new HashMap<>();
    private HashMap<Integer, Films> userFavoriteFilms = new HashMap<>();
    private Set<Integer> userRatedFilms = new HashSet<>();
    private String username;
    private List<String> genreList = new ArrayList<>();
    private Films mFilm;

    private final FilmsNetworkDataSource filmsNetworkDataSource;
    private final AppExecutors mExecutors = AppExecutors.getInstance();

    private final MutableLiveData<Integer> genreFilterLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Films>> titleFilterLiveData = new MutableLiveData<>();

    // Objeto para la sincronización de hilos
    public static final Object lock = new Object();

    private long lastUpdateTimeMillisMap;
    private static final long MIN_TIME_FROM_LAST_FETCH_MILLIS = 30000;

    private Repository(FilmDAO filmDAO, FavoritesDAO favoritesDAO, PendingsDAO pendingsDAO, CommentDAO commentDAO, RatingDAO ratingDAO, GenreDAO genreDAO, FilmsGenresListDAO filmsGenresListDAO, FilmsNetworkDataSource filmsNetworkDataSource) {
        this.filmDAO = filmDAO;
        this.genreDAO = genreDAO;
        this.ratingDAO = ratingDAO;
        this.favoritesDAO = favoritesDAO;
        this.pendingsDAO = pendingsDAO;
        this.commentDAO = commentDAO;
        this.filmsGenresListDAO = filmsGenresListDAO;
        this.filmsNetworkDataSource = filmsNetworkDataSource;

        // LiveData that fetches films and genres from network
        LiveData<Films[]> filmsNetworkData = filmsNetworkDataSource.getCurrentFilms();
        LiveData<Genre[]> genresNetworkData = filmsNetworkDataSource.getCurrentGenres();
        // As long as the repository exists, observe the network LiveData.
        // If that LiveData changes, update the database.
        filmsNetworkData.observeForever(newFilmsFromNetwork -> mExecutors.diskIO().execute(() -> {

            // Como se añade el rating a las películas cargadas de la API, aquellas que tengan votos no son reemplazadas en la BD
            HashMap<Integer,Films> ratedFilms = new HashMap<>();
            for (Films i : filmDAO.getRatedFilms()) ratedFilms.put(i.getId(),i);
            HashMap<Integer,Films> filmsFetched = new HashMap<>();
            for(Films i : newFilmsFromNetwork) filmsFetched.put(i.getId(), i);
            for (Map.Entry<Integer, Films> entry : ratedFilms.entrySet()) {
                filmsFetched.remove(entry.getKey());
            }
            List<Films> notRatedFilmsList = new ArrayList<>(filmsFetched.values());

            // Insert our new films into local database
            filmDAO.insertAllFilms(notRatedFilmsList);
            Log.d(LOG_TAG, "Nuevas películas insertadas en Room");
            // Se actualiza la tabla que contiene los ID de las películas junto a los ID de sus géneros asociados
            for (Films film : newFilmsFromNetwork) {
                for (Integer id : film.getGenreIds()) {
                    filmsGenresListDAO.insertFilmGenre(film.getId(), id);
                }
            }
        }));
        genresNetworkData.observeForever(newGenresFromNetwork -> mExecutors.diskIO().execute(() -> {
            // Insert our new genres into local database
            genreDAO.insertAllGenres(Arrays.asList(newGenresFromNetwork));
            Log.d(LOG_TAG, "Nuevos géneros insertados en Room");
        }));
        doFetchFilmsAndGenres();
    }

    public synchronized static Repository getInstance(FilmDAO filmDAO, FavoritesDAO favoritesDAO, PendingsDAO pendingsDAO, CommentDAO commentDAO, RatingDAO ratingDAO, GenreDAO genreDAO, FilmsGenresListDAO filmsGenresListDAO, FilmsNetworkDataSource filmsNetworkDataSource) {
        Log.d(LOG_TAG, "Obteniendo el Repositorio");
        if (sInstance == null) {
            sInstance = new Repository(filmDAO, favoritesDAO, pendingsDAO, commentDAO, ratingDAO, genreDAO, filmsGenresListDAO, filmsNetworkDataSource);
            Log.d(LOG_TAG, "Se ha creado una nueva instancia del Repositorio");
        }
        return sInstance;
    }

    /**
     * Recupera datos de la API si es necesario.
     */
    public void doFetchFilmsAndGenres() {
        if (isFetchNeeded()) {
            forceFetchFilmsAndGenres();
        }
    }

    public void forceFetchFilmsAndGenres() {
        Log.d(LOG_TAG, "Obteniendo las películas y los géneros de la API");
        mExecutors.networkIO().execute(() -> {
            filmsNetworkDataSource.fetchFilms();
            filmsNetworkDataSource.fetchGenres();
            lastUpdateTimeMillisMap = System.currentTimeMillis();
        });
    }

    public void setGenre(int genreId) {
        genreFilterLiveData.setValue(genreId);
    }

    public LiveData<List<Films>> getFilmsByGenre() {
        return Transformations.switchMap(genreFilterLiveData, input -> filmDAO.getFilmsByGenres(input));
    }

    public void getFilmsByTitle(String input) {
        mExecutors.diskIO().execute(() -> {
            List<Films> queryResult = new ArrayList<>();
            for (Films film : filmDAO.getAllFilmsAsList()) {
                if (LevenshteinSearch.distance(film.getTitle().toLowerCase(), input.toLowerCase()) <= 3 || film.getTitle().toLowerCase().contains(input.toLowerCase())) {
                    queryResult.add(film);
                }
            }
            titleFilterLiveData.postValue(queryResult);
        });
    }

    public LiveData<List<Films>> getTitleFilterLiveData() {
        return titleFilterLiveData;
    }

    /**
     * Métodos relacionados con la información de películas del usuario (favoritas, pendientes y valoradas).
     */

    /**
     * Carga las estructuras del repositorio que se destinan a almacenar la información de las películas favoritas, pendientes y valoradas por el usuario.
     * Se emplean mapas y conjuntos para que la labores de consulta no impliquen transacciones con la BD que afecten al rendimiento de la app.
     * Antes de realizar la carga se llama al método resetUserFilmData(). Esto es necesario para borrar la información de un posible usuario anterior en caso de
     * que la instancia del repositorio sea utilizada por varias cuentas, como por ejemplo, al iniciar y cerrar sesión varias veces en una misma ejecución.
     *
     * @param username Nombre del usuario del que se recupera la información.
     */
    public void getUserFilmData(String username) {
        this.username = username;
        resetUserFilmData();
        mExecutors.diskIO().execute(() -> {
            for (Films film : filmDAO.getFavoritesFilms(username)) {
                userFavoriteFilms.put(film.getId(), film);
            }
            for (Films film : filmDAO.getPendingsFilms(username)) {
                userPendingFilms.put(film.getId(), film);
            }
            userRatedFilms.addAll(ratingDAO.getRatingIDs(username));
        });
    }

    /**
     * Limpia los datos almacenados en las estructuras destinadas a las películas favoritas, pendientes y valoradas del usuario.
     * Esto resulta necesario cuando un usuario inicia sesión tras haber cerrado sesión otro usuario diferente. Esto se debe a que la instancia del repositorio
     * no es destruida puesto que la ejecución de la app no finaliza.
     */
    private void resetUserFilmData() {
        userFavoriteFilms.clear();
        userPendingFilms.clear();
        userRatedFilms.clear();
    }

    /**
     * Retorna las películas marcadas como favoritas por el usuario.
     *
     * @return HashMap con las películas favoritas del usuario.
     */
    public HashMap<Integer, Films> getUserFavoritesFilms() {
        return userFavoriteFilms;
    }

    /**
     * Retorna las películas marcadas como pendientes por el usuario.
     *
     * @return HashMap con las películas pendientes del usuario.
     */
    public HashMap<Integer, Films> getUserPendingsFilms() {
        return userPendingFilms;
    }

    /**
     * Retorna las películas valoradas por el usuario.
     *
     * @return Conjunto con las películas valoradas por el usuario.
     */
    public Set<Integer> getUserRatedFilms() {
        return userRatedFilms;
    }

    /**
     * Añade una película al HashMap de películas favoritas del usuario y a la base de datos.
     * Se mantienen actualizadas ambas estructuras para asegurar persistencia al mismo tiempo que consultas con complejidad temporal O(1).
     *
     * @param film Película marcada como favorita por el usuario.
     */
    public void addUserFavoriteFilm(Films film) {
        userFavoriteFilms.put(film.getId(), film);
        mExecutors.diskIO().execute(() -> favoritesDAO.insertFavorites(new Favorites(film.getId(), username)));
    }

    /**
     * Añade una película al HashMap de películas pendientes del usuario y a la base de datos.
     * Se mantienen actualizadas ambas estructuras para asegurar persistencia al mismo tiempo que consultas con complejidad temporal O(1).
     *
     * @param film Película marcada como pendiente por el usuario.
     */
    public void addUserPendingFilm(Films film) {
        userPendingFilms.put(film.getId(), film);
        mExecutors.diskIO().execute(() -> pendingsDAO.insertPendings(new Pendings(film.getId(), username)));
    }

    /**
     * Añade una película al Conjunto de películas valoradas por el usuario y a la base de datos.
     * Se mantienen actualizadas ambas estructuras para asegurar persistencia al mismo tiempo que consultas con complejidad temporal O(1).
     *
     * @param film Película valorada por el usuario.
     */
    public void addUserRatedFilm(Films film, Integer rating) {
        userRatedFilms.add(film.getId());
        mExecutors.diskIO().execute(() -> {
            ratingDAO.insertRating(new Rating(film.getId(), username, rating));
            filmDAO.updateFilm(film);
        });
    }

    /**
     * Elimina una película del HashMap de películas favoritas del usuario y de la base de datos.
     * Se mantienen actualizadas ambas estructuras para asegurar persistencia al mismo tiempo que consultas con complejidad temporal O(1).
     *
     * @param film Película desmarcada como favorita por el usuario.
     */
    public void removeUserFavoriteFilm(Films film) {
        userFavoriteFilms.remove(film.getId());
        mExecutors.diskIO().execute(() -> favoritesDAO.deleteFavorites(new Favorites(film.getId(), username)));
    }

    /**
     * Elimina una película del HashMap de películas pendientes del usuario y de la base de datos.
     * Se mantienen actualizadas ambas estructuras para asegurar persistencia al mismo tiempo que consultas con complejidad temporal O(1).
     *
     * @param film Película desmarcada como pendiente por el usuario.
     */
    public void removeUserPedingFilm(Films film) {
        userPendingFilms.remove(film.getId());
        mExecutors.diskIO().execute(() -> pendingsDAO.deletePendings(new Pendings(film.getId(), username)));
    }

    /**
     * Métodos relacionados con la información de una película concreta
     */

    public LiveData<List<Comments>> getFilmComments(Films film) {
        return commentDAO.getFilmComments(film.getId());
    }

    public void deleteComment(Comments comment) {
        mExecutors.diskIO().execute(() -> commentDAO.deleteComment(comment));
    }

    public void addComment(Comments comment) {
        mExecutors.diskIO().execute(() -> commentDAO.addComment(comment));
    }

    public Films getFilm(Films film) {
        mExecutors.diskIO().execute(() -> {
            synchronized (lock) {
                mFilm = filmDAO.getFilm(film.getId());
                lock.notifyAll();
            }
        });
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return mFilm;
        }
    }

    public List<String> getFilmGenres(Films film) {
        mExecutors.diskIO().execute(() -> {
            synchronized (lock) {
                genreList = filmsGenresListDAO.getAllFilmsGenresNames(film.getId());
                lock.notify();
            }
        });
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return genreList;
        }
    }

    /**
     * Database related operations
     **/

    public LiveData<List<Films>> getCurrentFilms() {
        return filmDAO.getAllFilms();
    }

    public LiveData<List<Genre>> getCurrentGenres() {
        return genreDAO.getAllGenres();
    }

    /**
     * Checks if we have to update the film and genre data.
     *
     * @return Whether a fetch is needed
     */
    private boolean isFetchNeeded() {
        Long lastFetchTimeMillis = lastUpdateTimeMillisMap;
        lastFetchTimeMillis = lastFetchTimeMillis == null ? 0L : lastFetchTimeMillis;
        long timeFromLastFetch = System.currentTimeMillis() - lastFetchTimeMillis;
        return timeFromLastFetch > MIN_TIME_FROM_LAST_FETCH_MILLIS;
    }
}
