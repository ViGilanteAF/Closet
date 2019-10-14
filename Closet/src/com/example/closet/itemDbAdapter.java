package com.example.closet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class itemDbAdapter {
	
	public static final String KEY_NAME="name";
	public static final String KEY_CATEGORY="category";
	public static final String KEY_BRAND="brand";
	public static final String KEY_WEATHER="weather";
	public static final String KEY_IMAGE="image";
	public static final String KEY_ID="_id";
	
	
	public static int COLUMN_NAME=1;
	public static int COLUMN_CATEGORY=2;
	public static int COLUMN_BRAND=3;
	public static int COLUMN_WEATHER=4;
	public static int COLUMN_IMAGE=5;
	
	public static final String TAG="ItemDbAdapter";
	
	public static SQLiteDatabase mDb;
	private ItemDbOpenHelper dbHelper;
	private static itemDbAdapter database;
	
	public static final String DATABASE_NAME="DATA_CLOSET";
	public static final String DATABASE_TABLE="ITEM";
	public static final int DATABASE_VERSION=1;

	public static final String DATABASE_PHOTO_TABLE="PHOTO";
	public static final String PHOTO_KEY_ID = "_id";
	public static final String PHOTO_KEY_URI="URI";
	
	private final Context mContext;
	
	   /**
     * 데이터베이스 열기
     */
    public boolean open() {
//        println("opening database [" + BasicInfo.DATABASE_NAME + "].");
 
        dbHelper = new ItemDbOpenHelper(mContext);
        mDb = dbHelper.getWritableDatabase();
 
        return true;
    }
 
    /**
     * 데이터베이스 닫기
     */
    public void close() {
 //       println("closing database [" + BasicInfo.DATABASE_NAME + "].");
    	mDb.close();
 
        database = null;
    }
 	private static class ItemDbOpenHelper extends SQLiteOpenHelper {
		public ItemDbOpenHelper(Context context, String name, 
				CursorFactory factory, int version) {
			super (context, name, factory, version);
		}
		public ItemDbOpenHelper(Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			// TODO Auto-generated method stub
			String DROP_SQL = "drop table if exists"+DATABASE_TABLE;
			try{
				_db.execSQL(DROP_SQL);
				Log.d(TAG,"DATABASE_CREATE : SUCCESS");
				
			}catch(Exception e){
				Log.e(TAG,"DATABASE_CREATE : ERROR",e);
			}

			String DATABASE_CREATE=
					"create table "+DATABASE_TABLE+
					"("+KEY_ID+" integer primary key autoincrement, "+
					KEY_NAME+" text, "+KEY_CATEGORY+" text, "+
					KEY_BRAND+" text, "+KEY_WEATHER+" text, "+
					KEY_IMAGE+" text);";
			try{
				_db.execSQL(DATABASE_CREATE);
				Log.d(TAG,"DATABASE_CREATE : SUCCESS");
				
			}catch(Exception e){
				Log.e(TAG,"DATABASE_CREATE : ERROR",e);
			}
			
			DROP_SQL = "drop table if exists"+DATABASE_PHOTO_TABLE;
			try{
				_db.execSQL(DROP_SQL);
				Log.d(TAG,"DATABASE_CREATE : SUCCESS");
				
			}catch(Exception e){
				Log.e(TAG,"DATABASE_CREATE : ERROR",e);
			}
			DATABASE_CREATE=
					"create table "+DATABASE_PHOTO_TABLE+
					"("+PHOTO_KEY_ID+" integer primary key autoincrement, "+
					PHOTO_KEY_URI+" text);";
			try{
				_db.execSQL(DATABASE_CREATE);
				Log.d(TAG,"DATABASE_CREATE : SUCCESS");
				
			}catch(Exception e){
				Log.e(TAG,"DATABASE_CREATE : ERROR",e);
			}
			 // create index
            String CREATE_INDEX_SQL = "create index " + DATABASE_PHOTO_TABLE + "_IDX ON " + DATABASE_PHOTO_TABLE + "("
                            + PHOTO_KEY_URI
                            + ")";
            try {
                _db.execSQL(CREATE_INDEX_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in CREATE_INDEX_SQL", ex);
            }
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
			// TODO Auto-generated method stub
			_db.execSQL("DROP TABLE IF EXISTS"+DATABASE_TABLE);
			onCreate(_db);
		}
	}
    /**
     * execute raw query using the input SQL
     * close the cursor after fetching any result
     *
     * @param SQL
     * @return
     */
    public Cursor rawQuery(String SQL) {
 
        Cursor c1 = null;
        try {
            c1 = mDb.rawQuery(SQL, null);
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery", ex);
        }
 
        return c1;
    }
 
    public boolean execSQL(String SQL) {
 
        try {
            Log.d(TAG, "SQL : " + SQL);
            mDb.execSQL(SQL);
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery", ex);
            return false;
        }
 
        return true;
    }	
	
	private static ItemDbOpenHelper mDbHelper;
		
	public itemDbAdapter(Context context){
		this.mContext=context;
		
		mDbHelper=new ItemDbOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	
	public long insertItem(Item _item){
		
		ContentValues newTaskValues=new ContentValues();
		newTaskValues.put(KEY_NAME, _item.getName());
		newTaskValues.put(KEY_CATEGORY, _item.getCategory());
		newTaskValues.put(KEY_BRAND, _item.getBrand());
		newTaskValues.put(KEY_WEATHER, _item.getWeather());
		newTaskValues.put(KEY_IMAGE, _item.getImage());
		
		return mDb.insert(DATABASE_TABLE, null, newTaskValues);
	}
	
	public boolean removeItem(long _rowIndex) {
		return mDb.delete(DATABASE_TABLE, KEY_ID+"="+_rowIndex, null)>0;
	}
	
	public boolean updateItem(long _rowIndex, Item _item){
		ContentValues newValue=new ContentValues();
		newValue.put(KEY_NAME, _item.getName());
		newValue.put(KEY_CATEGORY, _item.getCategory());
		newValue.put(KEY_BRAND, _item.getBrand());
		newValue.put(KEY_WEATHER, _item.getWeather());
		newValue.put(KEY_IMAGE, _item.getImage());
		
		return mDb.update(DATABASE_TABLE, newValue, KEY_ID+"="+_rowIndex, null )>0;
	}

	public static Cursor getAllItemCursor(){
		return mDb.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_NAME,
				KEY_CATEGORY, KEY_BRAND, KEY_WEATHER, KEY_IMAGE}, 
				null, null, null, null, null);
	}
	public static Cursor getCategoryItemCursor(String _cateString){
		return mDb.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_NAME,
				KEY_CATEGORY, KEY_BRAND, KEY_WEATHER, KEY_IMAGE}, 
				KEY_CATEGORY+"="+_cateString, null, null, null, null);
	}
	public Cursor setItemCursor(long _rowIndex) throws SQLException{
		Cursor result=mDb.query(true, DATABASE_TABLE, new String[] {KEY_ID, KEY_NAME,
				KEY_CATEGORY, KEY_BRAND, KEY_WEATHER, KEY_IMAGE}, 
				KEY_ID+"="+_rowIndex,null, null, null, null, null);
		
		if ((result.getCount() ==0) || !result.moveToFirst()){
			throw new SQLException ("no items found in row"+_rowIndex);
		}
		return result;
	}
	
	public Item getItem(long _rowIndex) throws SQLException {
		Cursor cursor=mDb.query(true, DATABASE_TABLE, new String[] {KEY_ID, KEY_NAME,
				KEY_CATEGORY, KEY_BRAND, KEY_WEATHER, KEY_IMAGE}, 
				KEY_ID+"="+_rowIndex, null, null, null, null, null);
		if ((cursor.getCount() == 0) || !cursor.moveToFirst()){
			throw new SQLException ("no item found in row"+_rowIndex);
		}
		String name=cursor.getString(COLUMN_NAME);
		String category=cursor.getString(COLUMN_CATEGORY);
		String brand=cursor.getString(COLUMN_BRAND);
		String weather=cursor.getString(COLUMN_WEATHER);
		String image=cursor.getString(COLUMN_IMAGE);
		
		Item result=new Item(name, category, brand, weather, image);
		return result;
	}


}
