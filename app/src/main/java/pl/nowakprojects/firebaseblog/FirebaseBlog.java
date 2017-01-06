package pl.nowakprojects.firebaseblog;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Mateusz on 06.01.2017.
 */

public class FirebaseBlog extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if(!FirebaseApp.getApps(this).isEmpty())
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
