package com.example.closet;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GridViewActivity extends Activity {
	static final private int ADD_NEW_ITEM = Menu.FIRST;
	static final private int EDIT_ITEM = Menu.FIRST + 1;
	static final private int REMOVE_ITEM = Menu.FIRST + 2;

	public static final int REQUEST_CODE_TOP = 0;
	public static final int REQUEST_CODE_BOTTOM = 1;
	public static final int REQUEST_CODE_SHOSE = 2;

	public static final String TAG = "GRIDVIEW_ACTIVITY";

	public String thisStringCategory;
	public int thisNumCategory;
	private SQLiteDatabase db;
	private itemDbAdapter itemdbadapter;
	// private DatabaseHelper dbhelper;

	static private int cnt;
	static private int numOFDBItems = 0;
	public int req;
	Cursor mCursor;
	public ArrayList<String> images;

	public static int itemCount = 0;
	String folder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid_view);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		folder = Environment.getExternalStorageDirectory() + "/"
				+ BasicInfo.FOLDER_PHOTO;

		thisStringCategory = getIntent().getStringExtra("cateString");
		thisNumCategory = getIntent().getIntExtra("cateNum", 0);
		Log.d("category_GridView", thisStringCategory);
		// Toast.makeText(getBaseContext(), "reqCode="+thisStringCategory,
		// Toast.LENGTH_SHORT).show();

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new ImageAdapter(this));

		itemdbadapter = new itemDbAdapter(GridViewActivity.this);
		itemdbadapter.open();
		// images.clear();

		String SELECT_SQL = "Select " + itemDbAdapter.KEY_NAME + ", "
				+ itemDbAdapter.KEY_CATEGORY + ", " + itemDbAdapter.KEY_BRAND
				+ ", " + itemDbAdapter.KEY_WEATHER + ", "
				+ itemDbAdapter.KEY_IMAGE + " from "
				+ itemDbAdapter.DATABASE_TABLE + " where "
				+ itemDbAdapter.KEY_CATEGORY + " = '" + thisStringCategory
				+ "'";

		Log.d(TAG, SELECT_SQL);

		images = new ArrayList<String>();
		numOFDBItems = 0;
		
		
		if (itemDbAdapter.mDb != null) {

			mCursor = itemdbadapter.mDb.rawQuery(SELECT_SQL, null);
			Log.d(TAG, "itemDbAdapter.mDb ok!");
			// cursor.moveToNext();
			if(mCursor.getCount()!=0){
				mCursor.moveToFirst();
				do {
					Log.d(TAG, "mCursor.getCount() = " + mCursor.getCount());
					int i = mCursor.getColumnIndex(itemDbAdapter.KEY_IMAGE);
					String photo_id = mCursor.getString(i);
					String SELECT_PHOTO_SQL = "Select "
							+ itemDbAdapter.PHOTO_KEY_URI + " from "
							+ itemDbAdapter.DATABASE_PHOTO_TABLE + " where "
							+ itemDbAdapter.PHOTO_KEY_ID + " = " + photo_id;
					Log.d(TAG, SELECT_PHOTO_SQL);
					Cursor photoCursor = itemdbadapter.mDb.rawQuery(SELECT_PHOTO_SQL,
							null);
					photoCursor.moveToFirst();
					images.add(photoCursor.getString(0));
					photoCursor.close();
					// Log.d(TAG, photoCursor.getString(0));
					// images[numOFDBItems] = mCursor.getString(i);
					// Log.d(TAG,"images[numOFDBItems] = "+images[numOFDBItems]);
					numOFDBItems++;
				} while (mCursor.moveToNext());
				// dbAdapter.close();
			}
			mCursor.close();
		}

		gridview.setOnItemClickListener(new OnItemClickListener(){
        	public void onItemClick(AdapterView<?> parent, View v, int position, long id){
        		Intent intent = new Intent(getBaseContext(), DetailActivity.class);
        		Log.d("minbung","position"+position);
        		/*
        		String SELECT_PHOTO_SQL =  "Select "+itemDbAdapter.PHOTO_KEY_ID
 						+" from "+itemDbAdapter.DATABASE_PHOTO_TABLE
 						+" where "+itemDbAdapter.PHOTO_KEY_URI+" = '"+images.get(position)+"'";
        		Cursor photoIdCursor = itemdbadapter.mDb.rawQuery(SELECT_PHOTO_SQL, null);
 				photoIdCursor.moveToFirst();
 				*/
    			intent.putExtra("position", position);
    			intent.putExtra("cateString", thisStringCategory);
    			intent.putExtra("cateNum", thisNumCategory);
    			intent.putExtra("photoURI", images.get(position));
        		startActivity(intent);
        	}
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// 새 메뉴항목을 만들어 추가한다
		MenuItem addItem = menu.add(0, ADD_NEW_ITEM, Menu.NONE,
				R.string.setting_add);

		// 아이콘을 할당한다
		addItem.setIcon(R.drawable.add_new_item);

		// 이들 각각에 대한 ?축키를 할당한다.
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		Intent intent = new Intent(getBaseContext(), AddActivity.class);
		intent.putExtra("cateString", thisStringCategory);
		intent.putExtra("cateNum", thisNumCategory);
		Log.d("minbung", thisStringCategory);

		switch (item.getItemId()) {
		case REMOVE_ITEM: {
			return true;
		}
		case ADD_NEW_ITEM: {
			startActivity(intent);
			return true;
		}
		}
		return false;
	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return numOFDBItems;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View contextView, ViewGroup parent) {
			ImageView imageview;

			if (contextView == null) {
				imageview = new ImageView(mContext);
				imageview.setLayoutParams(new GridView.LayoutParams(85, 85));
				imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageview.setPadding(8, 8, 8, 8);

			} else {
				imageview = (ImageView) contextView;
			}
			// Log.d(TAG, "GRIDVIEW COUNT : "+this.getCount());
			Bitmap bm = BitmapFactory.decodeFile(images.get(position));
			Log.d(TAG, "filefolder : " + images.get(position));
			imageview.setImageBitmap(bm);
			return imageview;
		}
	}
}