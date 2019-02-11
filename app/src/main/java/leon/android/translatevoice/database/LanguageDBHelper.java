package leon.android.translatevoice.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import leon.android.translatevoice.model.Language;

public class LanguageDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "language.db";
    public static final int DATABASE_VERSION = 1;
    public  static final String SQL_CREATE_LANGUAGE_TABLE = "CREATE TABLE " +
            LanguageContract.LanguageEntry.TABLE_NAME + " (" +
            LanguageContract.LanguageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            LanguageContract.LanguageEntry.COLUMN_CHOOSEN_LANGUAGE + " TEXT, " +
            LanguageContract.LanguageEntry.COLUMN_CHOOSEN_LANGUAGE_TEXT + " TEXT, " +
            LanguageContract.LanguageEntry.COLUMN_TRANSALATED_LANGUAGE + " TEXT, " +
            LanguageContract.LanguageEntry.COLUMN_TRANSLATED_LANGUAGE_TEXT + " TEXT " +
            ");";

    public LanguageDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_LANGUAGE_TABLE = "CREATE TABLE " +
                LanguageContract.LanguageEntry.TABLE_NAME + " (" +
                LanguageContract.LanguageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LanguageContract.LanguageEntry.COLUMN_CHOOSEN_LANGUAGE + " TEXT, " +
                LanguageContract.LanguageEntry.COLUMN_CHOOSEN_LANGUAGE_TEXT + " TEXT, " +
                LanguageContract.LanguageEntry.COLUMN_TRANSALATED_LANGUAGE + " TEXT, " +
                LanguageContract.LanguageEntry.COLUMN_TRANSLATED_LANGUAGE_TEXT + " TEXT " +
                ");";

        db.execSQL(SQL_CREATE_LANGUAGE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LanguageContract.LanguageEntry.TABLE_NAME);
        onCreate(db);
    }
}
