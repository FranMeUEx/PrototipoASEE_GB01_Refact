package es.unex.prototipoasee.API;

import android.util.Log;

import java.util.List;

import es.unex.prototipoasee.model.Films;
import es.unex.prototipoasee.model.FilmsPages;
import es.unex.prototipoasee.sharedInterfaces.OnFilmsLoadedListener;
import es.unex.prototipoasee.support.AppExecutors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FilmsNetworkLoaderRunnable implements Runnable{

    private final OnFilmsLoadedListener onFilmsLoadedListener;

    // Campos necesarios para trabajar con la API
    private static final String URLBASE = "https://api.themoviedb.org";
    private static final String APIKEY = "a99f635c038e47e61a70f3ab2b526e3e";
    private static final String LANGUAGE = "es-ES";
    private static final String TAG = "APP";

    public FilmsNetworkLoaderRunnable(OnFilmsLoadedListener onFilmsLoadedListener){
        this.onFilmsLoadedListener = onFilmsLoadedListener;
    }

    @Override
    public void run(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLBASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FilmAPI api = retrofit.create(FilmAPI.class);
        Call<FilmsPages> call = api.getFilms(APIKEY,LANGUAGE);

        call.enqueue(new Callback<FilmsPages>() {
            @Override
            public void onResponse(Call<FilmsPages> call, Response<FilmsPages> response) {
                if(!response.isSuccessful()){
                    Log.i(TAG,"CODE: "+response.code());
                }

                assert response.body() != null;
                List<Films> films = response.body().getResults();
                AppExecutors.getInstance().mainThread().execute(() -> onFilmsLoadedListener.onFilmsLoaded(films));
            }

            @Override
            public void onFailure(Call<FilmsPages> call, Throwable t) {
                Log.i(TAG,"ERROR: LA APLICACIÓN FALLÓ AL REALIZAR LA CONSULTA");
                t.printStackTrace();
            }
        });
    }
}
