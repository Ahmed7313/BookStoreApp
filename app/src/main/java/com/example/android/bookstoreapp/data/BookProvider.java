package com.example.android.bookstoreapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

/**
 * {@link ContentProvider} for books app
 */
public class BookProvider extends ContentProvider {
    /**
     * URI matcher code for the content URI for the Books table
     */
    public final static int BOOKS = 100;
    /**
     * URI matcher code for the content URI for a single pet in the pets Books
     */
    public final static int BOOKS_ID = 101;
    private static final String LOG_TAG = BookProvider.class.getSimpleName();
    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static UriMatcher mMriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        mMriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        mMriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOKS_ID);
    }

    /**
     * Initializing the {@link BooksDbHelper}  with the {@link SQLiteDatabase}
     */
    BooksDbHelper mBooksDB;
    SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        //get version form the Books Database
        mBooksDB = new BooksDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        //get readable database instance
        SQLiteDatabase database = mBooksDB.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        //set the matcher of the books URI
        final int match = mMriMatcher.match(uri);

        // Figure out if the URI matcher can match the URI to a specific code
        switch (match) {
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME_BOOKS, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BookEntry.TABLE_NAME_BOOKS, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        final int match = mMriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOKS_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //initialize the matcher
        final int match = mMriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    private Uri insertBook(Uri uri, ContentValues values) {

        // Check that the name is not null
        String name = values.getAsString(BookEntry.COLUMN_BOOK_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Book as product requires a name");
        }

        // Check that the price is not null and is not lesser than 0
        Integer price = values.getAsInteger(BookEntry.COLUMN_BOOK_PRICE);
        if (price == null && price < 0) {
            throw new IllegalArgumentException("Book requires a price");
        }

        // Check that the price is not null and is not lesser than 0
        Integer quantity = values.getAsInteger(BookEntry.COLUMN_BOOK_PRICE);
        if (quantity == null && quantity < 0) {
            throw new IllegalArgumentException("Book requires a quantity number");
        }

        // Check that the supplier name is not null
        String supplierName = values.getAsString(BookEntry.COLUMN_BOOK_PRODUCT_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Book as product requires a name");
        }
        // Check that the store phone is not null
        String storePhone = values.getAsString(BookEntry.COLUMN_BOOK_PRODUCT_NAME);
        if (storePhone == null) {
            throw new IllegalArgumentException("Book as product requires a name");
        }
        //get the writable database to insert into
        SQLiteDatabase database = mBooksDB.getWritableDatabase();

        long id = database.insert(BookEntry.TABLE_NAME_BOOKS, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mBooksDB.getWritableDatabase();
        // Track the number of rows that were deleted
        int rowsDeleted;
        final int match = mMriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                rowsDeleted = database.delete(BookEntry.TABLE_NAME_BOOKS, selection, selectionArgs);
                break;
            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookEntry.TABLE_NAME_BOOKS, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final int match = mMriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, values, selection, selectionArgs);
            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Check that the name is not null
        if (values.containsKey(BookEntry.COLUMN_BOOK_PRODUCT_NAME)) {
            String name = values.getAsString(BookEntry.COLUMN_BOOK_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Book as product requires a name");
            }
        }

        // Check that the price is not null and is not lesser than 0
        if (values.containsKey(BookEntry.COLUMN_BOOK_PRICE)) {
            Integer price = values.getAsInteger(BookEntry.COLUMN_BOOK_PRICE);
            if (price == null && price < 0) {
                throw new IllegalArgumentException("Book requires a price");
            }
        }

        // Check that the price is not null and is not lesser than 0
        if (values.containsKey(BookEntry.COLUMN_BOOK_QUANTITY)) {
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
            if (quantity == null && quantity < 0) {
                throw new IllegalArgumentException("Book requires a quantity number");
            }
        }

        // Check that the supplier name is not null
        if (values.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Book as product requires a name");
            }
        }
        // Check that the store phone is not null
        if (values.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER)) {
            String storePhone = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER);
            if (storePhone == null) {
                throw new IllegalArgumentException("Book as product requires a name");
            }
        }
        //get the writable database to update into
        SQLiteDatabase database = mBooksDB.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(BookEntry.TABLE_NAME_BOOKS, values, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
