package com.humbleai.humblenotes;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

class SetItemSQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 2131361801;
    // Database Name
    private static final String DATABASE_NAME = "SetsDB";

    // SetItem table name
    private static final String TABLE_SETITEMS = "SetItem";

    // SetItem Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_PARENTSETID = "parentSetId";
    private static final String KEY_ICON = "icon";
    private static final String KEY_TITLE = "title";
    private static final String KEY_COLOR = "color";
    private static final String KEY_SETTYPE = "settype";
    private static final String KEY_EXCLUDED = "excluded";
    private static final String KEY_IMAGE = "image";

    private static final String[] COLUMNS = {KEY_ID, KEY_PARENTSETID, KEY_ICON, KEY_TITLE, KEY_COLOR, KEY_SETTYPE, KEY_EXCLUDED, KEY_IMAGE};

    public SetItemSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }


    public Long addSetItem(SetItem setitem){

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_PARENTSETID, setitem.getParentSetId()); // get icon
        values.put(KEY_ICON, setitem.getIcon()); // get icon
        values.put(KEY_TITLE, setitem.getTitle()); // get title
        values.put(KEY_SETTYPE, setitem.getSetType()); // get setType
        values.put(KEY_EXCLUDED, setitem.getExcluded()); // get exc
        values.put(KEY_IMAGE, setitem.getImage());
        // 3. insert

        // 4. close
      //  db.close();

        return db.insert(TABLE_SETITEMS, // table
                null, //nullColumnHack
                values);
    }


    public SetItem getSetItem(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_SETITEMS, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[]{String.valueOf(id)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build setitem object
        SetItem setitem = new SetItem();
        assert cursor != null;
        setitem.setId(Integer.parseInt(cursor.getString(0)));
        setitem.setParentSetId(Integer.parseInt(cursor.getString(1)));
        setitem.setIcon(Integer.parseInt(cursor.getString(2)));
        setitem.setTitle(cursor.getString(3));
        setitem.setSetType(cursor.getString(5));
        //setitem.setItemViewType(cursor.getInt(6));
        setitem.setExcluded(cursor.getInt(6));
        setitem.setImage(cursor.getString(7));
        cursor.close();
        // 5. return setitem
        return setitem;
    }


    public Long getCount(int setIdsi){
        SQLiteDatabase db = this.getReadableDatabase();

        String wherePart =  KEY_PARENTSETID + " = " + setIdsi;

        return DatabaseUtils.queryNumEntries(db, TABLE_SETITEMS, wherePart);
    }

    public List<SetItem> getAllSetItems(int setIdsi, int lastItemId, boolean full) {
        List<SetItem> setitems = new LinkedList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_SETITEMS + " WHERE " + KEY_PARENTSETID + " = " + setIdsi + " AND " + KEY_ID + " > " + lastItemId;



        if (!full) query = query + " LIMIT 100";
            // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build setitem and add it to list
        SetItem setitem;
        if (cursor.moveToFirst()) {
            do {
                setitem = new SetItem();
                setitem.setId(Integer.parseInt(cursor.getString(0)));
                setitem.setParentSetId(Integer.parseInt(cursor.getString(1)));
                setitem.setIcon(Integer.parseInt(cursor.getString(2)));
                setitem.setTitle(cursor.getString(3));
                setitem.setSetType(cursor.getString(5));
                //setitem.setItemViewType(cursor.getInt(6));
                setitem.setExcluded(cursor.getInt(6));
                setitem.setImage(cursor.getString(7));

                // Add setitem to setitems
                setitems.add(setitem);

            } while (cursor.moveToNext());
        }

        cursor.close();
        // return setitems
        return setitems;
    }


     public int updateSetItem(SetItem setitem) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("parentSetId", setitem.getParentSetId()); // get icon
         values.put("icon", setitem.getIcon()); // get icon
         values.put("title", setitem.getTitle()); // get title
         values.put("settype", setitem.getSetType());
        // values.put("itemviewtype", setitem.getItemViewType());
         values.put("excluded", setitem.getExcluded());
         values.put("image", setitem.getImage());

         // 3. updating row
         return db.update(TABLE_SETITEMS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(setitem.getId()) }); //selection args

        // 4. close
       // db.close();



    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
    public void deleteSetItem(SetItem setitem) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_SETITEMS, //table name
                KEY_ID + " = ?",  // selections
                new String[]{String.valueOf(setitem.getId())}); //selections args

        // 3. close
     //   db.close();


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //do nothing
    }


}
