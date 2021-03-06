package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for movie favorites.
 */
public class FavoriteDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "favorites.db";
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;
    private final String TAG = FavoriteDbHelper.class.getSimpleName();

    public FavoriteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold favorite movies.
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoriteContract.FavoritesList.TABLE_NAME + " (" +
                FavoriteContract.FavoritesList.COLUMN_ID + " INTEGER PRIMARY KEY," +
                FavoriteContract.FavoritesList.COLUMN_THUMBNAIL + " TEXT NOT NULL, " +
                FavoriteContract.FavoritesList.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoriteContract.FavoritesList.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                FavoriteContract.FavoritesList.COLUMN_RATING + " TEXT NOT NULL, " +
                FavoriteContract.FavoritesList.COLUMN_DATE + " TEXT NOT NULL " +
                " );";

        // Timber.v(TAG + " SQL_CREATE_FAVORITES_TABLE is " + SQL_CREATE_FAVORITES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.FavoritesList.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
