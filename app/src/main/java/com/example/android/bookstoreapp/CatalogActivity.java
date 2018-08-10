package com.example.android.bookstoreapp;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;
import com.example.android.bookstoreapp.data.BooksDbHelper;

public class CatalogActivity extends AppCompatActivity {

    private BooksDbHelper mBooksDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyBook();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * insert dummy data at {@link BooksDbHelper}
     *
     * @return
     */
    private void insertDummyBook() {
        //initialize the object that will cary tha dummy data
        ContentValues values = new ContentValues();
        //add the dummy values
        values.put(BookEntry.COLUMN_BOOK_PRODUCT_NAME, "The prince");
        values.put(BookEntry.COLOMN_BOOK_AUTHOR, "Nicollo Machiavilli");
        values.put(BookEntry.COLUMN_BOOK_PRICE, 100);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 250);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, "History Station");
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER, "01244368863565");

        Uri bookUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

        Toast.makeText(this, getString(R.string.catalog_dummy_data_inserted),
                Toast.LENGTH_SHORT).show();
    }
}
