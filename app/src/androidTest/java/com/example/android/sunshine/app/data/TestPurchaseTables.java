package com.example.android.sunshine.app.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by Ben on 2016/5/23.
 */
public class TestPurchaseTables extends AndroidTestCase {

    public static final String LOG_TAG = TestPurchaseTables.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(PurchaseDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(PurchaseContract.CustomerEntry.TABLE_NAME);
        tableNameHashSet.add(PurchaseContract.ProductEntry.TABLE_NAME);
        tableNameHashSet.add(PurchaseContract.RelationEntry.TABLE_NAME);

        mContext.deleteDatabase(PurchaseDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new PurchaseDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the customer entry and product entry tables",
               tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + PurchaseContract.CustomerEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> customerColumnHashSet = new HashSet<String>();
        customerColumnHashSet.add(PurchaseContract.CustomerEntry._ID);
        customerColumnHashSet.add(PurchaseContract.CustomerEntry.COLUMN_CUSTOMER_NAME);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            customerColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                customerColumnHashSet.isEmpty());


        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + PurchaseContract.ProductEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        final HashSet<String> productColumnHashSet = new HashSet<String>();
        productColumnHashSet.add(PurchaseContract.ProductEntry._ID);
        productColumnHashSet.add(PurchaseContract.ProductEntry.COLUMN_PRODUCT_NAME);
        productColumnHashSet.add(PurchaseContract.ProductEntry.COLUMN_PRODUCT_PRICE);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            productColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                productColumnHashSet.isEmpty());

        db.close();
    }

    public void testCustomerTable() {
        insertCustomer();
    }
    public void testProductTable() {
        insertProduct();
    }

    public long insertCustomer() {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        PurchaseDbHelper dbHelper = new PurchaseDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues testValues = createJackCustomerValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long customerRowId;
        customerRowId = db.insert(PurchaseContract.CustomerEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(customerRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                PurchaseContract.CustomerEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from customer query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Customer Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from customer query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return customerRowId;
    }

    static ContentValues createJackCustomerValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(PurchaseContract.CustomerEntry.COLUMN_CUSTOMER_NAME, "Jack");
        return testValues;
    }

    static ContentValues createMaryCustomerValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(PurchaseContract.CustomerEntry.COLUMN_CUSTOMER_NAME, "Mary");
        return testValues;
    }

    public long insertProduct() {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        PurchaseDbHelper dbHelper = new PurchaseDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues testValues = createTShirtProductValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long productRowId;
        productRowId = db.insert(PurchaseContract.ProductEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(productRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                PurchaseContract.ProductEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from product query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Product Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from product query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return productRowId;
    }

    static ContentValues createTShirtProductValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(PurchaseContract.ProductEntry.COLUMN_PRODUCT_NAME, "T-shirt");
        testValues.put(PurchaseContract.ProductEntry.COLUMN_PRODUCT_PRICE, 50);
        return testValues;
    }

    static ContentValues createSkirtProductValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(PurchaseContract.ProductEntry.COLUMN_PRODUCT_NAME, "skirt");
        testValues.put(PurchaseContract.ProductEntry.COLUMN_PRODUCT_PRICE, 80);
        return testValues;
    }

    public void testRelationTable() {
        PurchaseDbHelper dbHelper = new PurchaseDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //long customerRowId = insertCustomer();
        //long productRowId = insertProduct();
        ContentValues testValueTS = createTShirtProductValues();
        ContentValues testValueSK = createSkirtProductValues();
        ContentValues testValueJ = createJackCustomerValues();
        ContentValues testValueM = createMaryCustomerValues();
        long productTSRowId = db.insert(PurchaseContract.ProductEntry.TABLE_NAME, null, testValueTS);
        long productSKRowId = db.insert(PurchaseContract.ProductEntry.TABLE_NAME, null, testValueSK);
        long customerJRowId = db.insert(PurchaseContract.CustomerEntry.TABLE_NAME, null, testValueJ);
        long customerMRowId = db.insert(PurchaseContract.CustomerEntry.TABLE_NAME, null, testValueM);

        // Make sure we have a valid row ID.
        assertFalse("Error: Customer Not Inserted Correctly", customerJRowId == -1L);
        assertFalse("Error: Customer Not Inserted Correctly", customerMRowId == -1L);
        assertFalse("Error: Product Not Inserted Correctly", productSKRowId == -1L);
        assertFalse("Error: Product Not Inserted Correctly", productTSRowId == -1L);

        // Second Step (Weather): Create weather values
        ContentValues relationValue1 = createRelationValues(customerJRowId, productTSRowId);
        ContentValues relationValue2 = createRelationValues(customerMRowId, productSKRowId);

        // Third Step (Weather): Insert ContentValues into database and get a row ID back
        long relationRowId1 = db.insert(PurchaseContract.RelationEntry.TABLE_NAME, null, relationValue1);
        assertTrue(relationRowId1 != -1);
        long relationRowId2 = db.insert(PurchaseContract.RelationEntry.TABLE_NAME, null, relationValue2);
        assertTrue(relationRowId2 != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor relationCursor = db.query(
                PurchaseContract.RelationEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue( "Error: No Records returned from relation query", relationCursor.moveToFirst() );

        // Fifth Step: Validate the location Query
        TestUtilities.validateCurrentRecord("testInsertReadDb relationEntry failed to validate",
                relationCursor, relationValue1);

        // Move the cursor to demonstrate that there is only one record in the database
        //assertFalse( "Error: More than one record returned from relation query",
        //        relationCursor.moveToNext() );
        relationCursor.moveToNext();
        assertTrue(relationCursor != null);
        TestUtilities.validateCurrentRecord("testInsertReadDb relationEntry failed to validate",
                relationCursor, relationValue2);

        // Sixth Step: Close cursor and database
        relationCursor.close();
        dbHelper.close();
    }

    static ContentValues createRelationValues(long customerRowId, long productRowId) {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(PurchaseContract.RelationEntry.COLUMN_CUSTOMER_KEY, "customer_id");
        testValues.put(PurchaseContract.RelationEntry.COLUMN_PRODUCT_KEY, "product_id");
        return testValues;
    }
}
