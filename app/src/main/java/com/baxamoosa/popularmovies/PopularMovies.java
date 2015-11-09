package com.baxamoosa.popularmovies;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.facebook.stetho.Stetho;

import timber.log.Timber;

public class PopularMovies extends Application {

    private static Context context;

    public static Context getAppContext() {
        return PopularMovies.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PopularMovies.context = getApplicationContext();
        Stetho.initializeWithDefaults(this); // http://facebook.github.io/stetho/
        // LeakCanary.install(this); // https://github.com/square/leakcanary

        //Including Jake Wharton's Timber logging library
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    /**
     * A tree which logs important information for crash reporting.
     */
    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            FakeCrashLibrary.log(priority, tag, message);

            if (t != null) {
                if (priority == Log.ERROR) {
                    FakeCrashLibrary.logError(t);
                } else if (priority == Log.WARN) {
                    FakeCrashLibrary.logWarning(t);
                }
            }
        }
    }
}