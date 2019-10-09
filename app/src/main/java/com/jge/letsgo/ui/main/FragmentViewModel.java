package com.jge.letsgo.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.jge.letsgo.database.AppDatabase;
import com.jge.letsgo.models.GoLocation;

import java.util.List;

public class FragmentViewModel extends AndroidViewModel {
    private LiveData<List<GoLocation>> goLocations;

    public FragmentViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        goLocations = database.goLocationDao().loadAllLocations();
    }

    public LiveData<List<GoLocation>> getGoLocations() {
        return goLocations;
    }
}
