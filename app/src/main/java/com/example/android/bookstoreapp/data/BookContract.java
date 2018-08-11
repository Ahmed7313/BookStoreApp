package com.example.android.bookstoreapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public final class BookContract {
    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.bookstoreapp";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.pets/books/ is a valid path for
     * looking at pet data. content://com.example.android.books/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_BOOKS = "books";


    private BookContract(){}

    public static final class BookEntry implements BaseColumns{

        /**
         * The content URI to access the books data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);
        // name of data base table for books
        public static final String TABLE_NAME_BOOKS = "books";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */

        public static final String CONTENT_ITEM_TYPE = ContentResolver.ANY_CURSOR_ITEM_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * Unique ID number for the pet (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;
        /**
         * Name of the books.
         *
         * Type: TEXT
         */
        public static final  String COLUMN_BOOK_PRODUCT_NAME = "product_name";
        /**
         * price of the books.
         *
         * Type: TEXT
         */
        public static final String COLUMN_BOOK_PRICE = "price";
        /**
         *quantity of books
         *
         * Type: INTEGER
         **/
        public static final String COLUMN_BOOK_QUANTITY = "quantity";
        /**
         * The supplier of the book
         *
         * Type: INTEGER
         */
        public static final String COLUMN_BOOK_SUPPLIER_NAME = "supplier_name";
        /**
         * Phone number of the supplier
         *
         * Type: TEXT
         */
        public static final String COLUMN_BOOK_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

    }

}
