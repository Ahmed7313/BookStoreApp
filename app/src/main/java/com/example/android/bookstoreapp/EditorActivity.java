package com.example.android.bookstoreapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;
import com.example.android.bookstoreapp.data.BooksDbHelper;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Permission to allow the app make a phone call
     */
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    /**
     * EditText field to enter the pet's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the book name
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the pet's weight
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the
     */
    private EditText mStoreName;

    private EditText mStorePhone;

    //the unique id of the loader
    private final static int EXISTING_BOOK_LOADER = 0;
    /**
     * Content URI for the existing book (null if it's a new pet)
     */
    private Uri mCurrentBookUri;
    // an instance of Books Data Base
    private BooksDbHelper mBooksDB;
    /**
     * Boolean flag that keeps track of whether the pet has been edited (true) or not (false)
     */
    private boolean mBookHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new book or editing an existing one.
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // Find all relevant views that we will need in Edit Book mode
        ImageView callSupplierButton = findViewById(R.id.button_call_supplier);
        final ImageView increaseQuantityImageView = findViewById(R.id.image_view_increase_quantity);
        final ImageView decreaseQuantityImageView = findViewById(R.id.image_view_decrease_quantity);

        // If the intent DOES NOT contain a book content URI, then we know that we are
        // creating a new book.
        if (mCurrentBookUri == null) {
            // This is a new book, so change the app bar to say "Add a Book"
            setTitle(getString(R.string.editor_activity_title_new_book));

            // Make the views that we need in Edit Book mode invisible if we are in Add a Book mode
            callSupplierButton.setVisibility(View.GONE);
            increaseQuantityImageView.setVisibility(View.GONE);
            decreaseQuantityImageView.setVisibility(View.GONE);

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a book that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing book, so change app bar to say "Edit Book"
            setTitle(getString(R.string.editor_activity_title_edit_book));

            // Make the views that we need in Edit Book mode visible if we are in Edit Book mode
            callSupplierButton.setVisibility(View.VISIBLE);
            increaseQuantityImageView.setVisibility(View.VISIBLE);
            decreaseQuantityImageView.setVisibility(View.VISIBLE);

            // Initialize a loader to read the book data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }
        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_book_name);
        mPriceEditText = findViewById(R.id.edit_book_price);
        mQuantityEditText = findViewById(R.id.edit_book_quantity);
        mStoreName = findViewById(R.id.edit_book_supplier_name);
        mStorePhone = findViewById(R.id.edit_supplier_phone);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mStoreName.setOnTouchListener(mTouchListener);
        mStorePhone.setOnTouchListener(mTouchListener);

        // Create onclick listener on callSupplierButton
        callSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });

        // Create onclick listener on increaseQuantityImageView
        increaseQuantityImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString = mQuantityEditText.getText().toString().trim();
                String stringValue = quantityString.matches("") ? "0" : quantityString;
                int quantity = Integer.parseInt(stringValue);
                updateQuantity(quantity, false);
            }
        });

        // Create onclick listener on decreaseQuantityImageView
        decreaseQuantityImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString = mQuantityEditText.getText().toString().trim();
                if (quantityString.matches("")) {
                    mQuantityEditText.setText("0");
                }
                String stringValue = quantityString.matches("") ? "0" : quantityString;
                int quantity = Integer.parseInt(stringValue);
                updateQuantity(quantity, true);
            }
        });
    }

    public void saveBook() {

        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierNameString = mStoreName.getText().toString().trim();
        String supplierPhoneString = mStorePhone.getText().toString().trim();

        int price = 0;
        int quantity = 0;
        if (!TextUtils.isEmpty(priceString) && !TextUtils.isEmpty(quantityString)) {
            price = Integer.parseInt(priceString);
            quantity = Integer.parseInt(quantityString);
        }

        if (mCurrentBookUri == null
                && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString)
                && TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierNameString)
                && TextUtils.isEmpty(supplierPhoneString)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_PRODUCT_NAME, nameString);
        values.put(BookEntry.COLUMN_BOOK_PRICE, price);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierNameString);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER, supplierPhoneString);


        if (mCurrentBookUri == null) {
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_Book_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validation() {

        String productNameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String supplierNameString = mStoreName.getText().toString().trim();
        String supplierPhoneNumberString = mStorePhone.getText().toString().trim();

        if (TextUtils.isEmpty(productNameString)) {
            Toast.makeText(this, "Please add a Product Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, "Please add a Quantity", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, "Please add a Price", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(this, "Please add a Supplier Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(supplierPhoneNumberString)) {
            Toast.makeText(this, "Please add a Supplier Phone Number", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                if (validation()){
                    saveBook();
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_PRODUCT_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,// Parent activity context
                mCurrentBookUri,             // Query the content URI for the current pet
                projection,                  // Columns to include in the resulting Cursor
                null,               // No selection clause
                null,           // No selection arguments
                null);             // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {

            int nameColumnIndex = data.getColumnIndex(BookEntry.COLUMN_BOOK_PRODUCT_NAME);
            int priceColumnIndex = data.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = data.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = data.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = data.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE_NUMBER);

            String name = data.getString(nameColumnIndex);
            int price = data.getInt(priceColumnIndex);
            int quantity = data.getInt(quantityColumnIndex);
            String supplierName = data.getString(supplierNameColumnIndex);
            String supplierPhone = data.getString(supplierPhoneNumberColumnIndex);

            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mStoreName.setText(supplierName);
            mStorePhone.setText(supplierPhone);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mStoreName.setText("");
        mStorePhone.setText("");
    }


    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }


    private void makePhoneCall() {
        String supplierPhoneNumber = mStorePhone.getText().toString().trim();
        if (ContextCompat.checkSelfPermission(EditorActivity.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditorActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        } else {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + supplierPhoneNumber)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission DENIED!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateQuantity(int quantity, boolean decrease) {
        //Decrease or increase quantity
        if (decrease) {
            quantity--;
        } else {
            quantity++;
        }
        // Only perform the update if this is an existing book.
        if (mCurrentBookUri != null) {
            if (quantity >= 0) {
                ContentValues values = new ContentValues();
                values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);

                // Call the ContentResolver to update the book at the given content URI.
                // Pass in null for the selection and selection args because the mCurrentBookUri
                // content URI already identifies the book that we want.
                getContentResolver().update(mCurrentBookUri, values, null, null);

                // Show a toast message when the quantity is equal to 0
                if (quantity == 0) {
                    Toast.makeText(getApplicationContext(), R.string.no_product_in_stock, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}