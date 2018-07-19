package com.example.android.bookstoreapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;
import com.example.android.bookstoreapp.data.BooksDbHelper;

public class MainActivity extends AppCompatActivity {

    private BooksDbHelper mBooksDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBooksDbHelper = new BooksDbHelper(this);
    }

    private void displayInfo (){
        // Create and/or open a database to read from it
        SQLiteDatabase db = mBooksDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        //  will be actually used after this query.
        String [] projection = {
                BookEntry.COLUMN_BOOK_PRODUCT_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER};

        Cursor cursor = db.query(
                BookEntry.TABLE_NAME_BOOKS,
                projection,
                null,
                null,
                null,
                null,
                null);
        TextView displayView = findViewById(R.id.display_information);

        try{
            displayView.setText("The books table contains " + cursor.getCount() + " Book.\n\n");
            displayView.append(BookEntry._ID + " - " +
                    BookEntry.COLUMN_BOOK_PRODUCT_NAME +" - " +
                    BookEntry.COLUMN_BOOK_PRICE+ " - "+
                    BookEntry.COLUMN_BOOK_QUANTITY+" - "+
                    BookEntry.COLUMN_BOOK_SUPPLIER_NAME+" - "+
                    BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER+ "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int productNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex= cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneColumnIndex= cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()){
                int currentID = cursor.getInt(idColumnIndex);
                String currentProductName = cursor.getString(productNameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

                displayView.append("\n" + currentID + " - " +
                        currentProductName + " - " + currentPrice + " - "+ currentQuantity + " - "+ currentSupplierName
                + " - " + currentSupplierPhone);
            }
        } finally {
            cursor.close();
        }

    }
    private void insertBook (){
        // Gets the database in write mode
        SQLiteDatabase db = mBooksDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and the user inserted book attributes are the values.
        ContentValues values = new ContentValues();
        values.put(BookEntry._ID, 1);
        values.put(BookEntry.COLUMN_BOOK_PRODUCT_NAME , "100 most influenced men on the world ");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 16);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 55);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, "I don't remember XD");
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER, "01014568626");

        long newRowId = db.insert(BookEntry.TABLE_NAME_BOOKS, null, values);
        Log.v("CatalogActivity", "new row ID" + newRowId);

        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving pet", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Pet saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
        Log.v("CatalogActivity", "new row ID" + newRowId);
    }

    }



