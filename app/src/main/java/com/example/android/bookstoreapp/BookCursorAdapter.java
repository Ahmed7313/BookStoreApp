package com.example.android.bookstoreapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;
import com.example.android.bookstoreapp.data.BooksDbHelper;

/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of Books data as its data source. This adapter knows
 * how to create list items for each row of Books data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {

    int quantityChange;

    BooksDbHelper mBookDbHelper;

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param cursor  The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_body, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView productNameTextView = view.findViewById(R.id.product_name);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView sellBookTextView = view.findViewById(R.id.sale_button);

        // Find the columns of book attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int productNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);

        // Read the book attributes from the Cursor for the current book
        final int bookID = cursor.getInt(idColumnIndex);
        String productName = cursor.getString(productNameColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);

        // Update the TextViews with the attributes for the current book
        productNameTextView.setText(productName);
        quantityTextView.setText("" + quantity);
        priceTextView.setText("" + price);

        // Create onclick listener on sellBookImageView
        sellBookTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CatalogActivity Activity = (CatalogActivity) context;
                Activity.saleBook(bookID, quantity);
            }
        });
    }

}
