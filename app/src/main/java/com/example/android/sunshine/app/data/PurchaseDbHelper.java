package com.example.android.sunshine.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.sunshine.app.data.PurchaseContract.CustomerEntry;
import com.example.android.sunshine.app.data.PurchaseContract.ProductEntry;
import com.example.android.sunshine.app.data.PurchaseContract.RelationEntry;

/**
 * Created by Ben on 2016/5/23.
 */
public class PurchaseDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "purchase.db";

    public PurchaseDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_CUSTOMER_TABLE = "CREATE TABLE " + CustomerEntry.TABLE_NAME + " (" +
                CustomerEntry._ID + " INTEGER PRIMARY KEY," +
                CustomerEntry.COLUMN_CUSTOMER_NAME + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " (" +
                ProductEntry._ID + " INTEGER PRIMARY KEY," +
                ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                ProductEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL " +
                " );";

        final String SQL_CREATE_RELATION_TABLE = "CREATE TABLE " + RelationEntry.TABLE_NAME + " (" +
                RelationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RelationEntry.COLUMN_CUSTOMER_KEY + " INTEGER NOT NULL, " +
                RelationEntry.COLUMN_PRODUCT_KEY + " INTEGER NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + RelationEntry.COLUMN_CUSTOMER_KEY + ") REFERENCES " +
                CustomerEntry.TABLE_NAME + " (" + CustomerEntry._ID + "), " +

                " FOREIGN KEY (" + RelationEntry.COLUMN_PRODUCT_KEY + ") REFERENCES " +
                ProductEntry.TABLE_NAME + " (" + ProductEntry._ID + ") ); ";

                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                //" UNIQUE (" + WeatherContract.WeatherEntry.COLUMN_DATE + ", " +
                //WeatherContract.WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_CUSTOMER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_RELATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PurchaseContract.CustomerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PurchaseContract.ProductEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PurchaseContract.RelationEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
