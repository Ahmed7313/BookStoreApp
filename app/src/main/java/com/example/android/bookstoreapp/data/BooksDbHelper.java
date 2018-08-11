package com.example.android.bookstoreapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

public class BooksDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "store.db";

    public BooksDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //the SQLight quarry that will create the table of book
        String SQL_CREATE_BOOKS_TABLE = " CREATE TABLE " +BookEntry.TABLE_NAME_BOOKS + "( "
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                +BookEntry.COLUMN_BOOK_PRODUCT_NAME  + " TEXT NOT NULL ,"
                +BookEntry.COLUMN_BOOK_PRICE + " INTEGER NOT NULL, "
                +BookEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL  DEFAULT 0, "
                +BookEntry.COLUMN_BOOK_SUPPLIER_NAME + " TEXT NOT NULL, "
                +BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER  + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
