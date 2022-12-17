package es.unex.prototipoasee.API;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import es.unex.prototipoasee.model.Films;
import es.unex.prototipoasee.model.Genre;
import es.unex.prototipoasee.support.AppExecutors;

public class FilmsNetworkDataSource {

    private static final String LOG_TAG = FilmsNetworkDataSource.class.getSimpleName();
    private static FilmsNetworkDataSource sInstance;

    // LiveData para las películas y géneros obtenidos
    private final MutableLiveData<Films[]> downloadedFilms;
    private final MutableLiveData<Genre[]> downloadedGenres;

    private FilmsNetworkDataSource() {
        downloadedFilms = new MutableLiveData<>();
        downloadedGenres = new MutableLiveData<>();
    }

    public synchronized static FilmsNetworkDataSource getInstance() {
        Log.d(LOG_TAG, "Obteniendo la fuente de datos de red");
        if (sInstance == null) {
            sInstance = new FilmsNetworkDataSource();
            Log.d(LOG_TAG, "Nueva fuente de datos de red instanciada");
        }
        return sInstance;
    }

    public LiveData<Films[]> getCurrentFilms() {
        return downloadedFilms;
    }

    public LiveData<Genre[]> getCurrentGenres() {
        return downloadedGenres;
    }

    /**
     * Gets the newest films
     */
    public void fetchFilms() {
        Log.d(LOG_TAG, "Ha comenzado la recuperación de las películas");
        // Get data from network and pass it to LiveData
        AppExecutors.getInstance().networkIO().execute(new FilmsNetworkLoaderRunnable(
                films -> downloadedFilms.postValue(films.toArray(new Films[0]))));
    }

    /**
     * Gets the newest genres
     */
    public void fetchGenres() {
        Log.d(LOG_TAG, "Ha comenzado la recuperación de los géneros");
        // Get data from network and pass it to LiveData
        AppExecutors.getInstance().networkIO().execute(new GenresNetworkLoaderRunnable(
                genres -> downloadedGenres.postValue(genres.toArray(new Genre[0]))));
    }
}
