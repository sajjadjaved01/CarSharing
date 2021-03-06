package com.cardee.data_source;

import android.net.Uri;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.util.LruCache;

import com.cardee.data_source.cache.LocalNewCarDataSource;
import com.cardee.data_source.remote.RemoteNewCarDataSource;
import com.cardee.data_source.remote.api.cars.request.NewCarData;

public class NewCarRepository implements NewCarDataSource {
    private static final String TAG = NewCarRepository.class.getSimpleName();

    private static NewCarRepository INSTANCE;

    private final NewCarDataSource localDataSource;
    private final NewCarDataSource remoteDataSource;

    private static final String CACHE_KEY = "last_saved_car";
    private final LruCache<String, NewCarData> cache;
    private boolean dirtyCache = true;

    private NewCarRepository() {
        localDataSource = LocalNewCarDataSource.getInstance();
        remoteDataSource = RemoteNewCarDataSource.getInstance();
        cache = new LruCache<>(1);
    }

    public static NewCarRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NewCarRepository();
        }
        return INSTANCE;
    }

    @Override
    public void obtainSavedCarData(final CacheCallback callback) {
        if (!dirtyCache) {
            NewCarData carData = cache.get(CACHE_KEY);
            if (carData != null) {
                callback.onSuccess(carData);
                return;
            }
            dirtyCache = true;
        }
        localDataSource.obtainSavedCarData(new CacheCallback() {
            @Override
            public void onSuccess(NewCarData carData) {
                updateCache(carData);
                callback.onSuccess(carData);
            }

            @Override
            public void onError(Error error) {
                callback.onError(error);
            }
        });
    }

    @Override
    public void saveCarData(NewCarData carData, boolean forcePush, final Callback callback) {
        if (!forcePush) {
            localDataSource.saveCarData(carData, false, callback);
            updateCache(carData);
            return;
        }
        remoteDataSource.saveCarData(carData, true, new Callback() {
            @Override
            public void onSuccess(Integer newCarId) {
                localDataSource.saveCarData(null, true, null);
                clearCache();
                OwnerCarsRepository.getInstance().refreshCars();
                callback.onSuccess(newCarId);
            }

            @Override
            public void onError(Error error) {
                callback.onError(error);
            }
        });
    }

    @Override
    public void saveCarImage(Uri imgUri, boolean forcePush, final ImageCacheCallback callback) {
        if (!forcePush) {
            localDataSource.saveCarImage(imgUri, false, new ImageCacheCallback() {
                @Override
                public void onSuccess() {
                    clearCache();
                    callback.onSuccess();
                }

                @Override
                public void onError(Error error) {
                    callback.onError(error);
                }
            });
        }

    }

    private void updateCache(NewCarData carData) {
        if (carData == null) {
            Log.e(TAG, "null value cannot be cached");
            return;
        }
        cache.put(CACHE_KEY, carData);
        dirtyCache = false;
    }

    private void clearCache() {
        cache.evictAll();
        dirtyCache = true;
    }
}
