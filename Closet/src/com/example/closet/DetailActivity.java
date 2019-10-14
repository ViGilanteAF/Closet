package com.example.closet;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends Activity {
	
	private itemDbAdapter dbAdapter;
	private String TAG = "DETAIL_ACTIVITY";
	private String photoURI;
	private long photoId;
	private long itemId;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        int categoryNum = getIntent().getIntExtra("cateNum", 0);
        photoURI = getIntent().getStringExtra("photoURI");
   
        TextView nameText=(TextView)findViewById(R.id.showNameEditText);
        TextView cateText = (TextView)findViewById(R.id.showCategorySpinner);
        TextView brandText = (TextView)findViewById(R.id.showBrandEditText);
        TextView weatherText = (TextView)findViewById(R.id.showWeatherSpinner);
        ImageView imagePhoto = (ImageView)findViewById(R.id.showImageClothes);
    	Button editButton=(Button)findViewById(R.id.editbutton);
    	Button deleteButton = (Button)findViewById(R.id.deleteButton);
        
        dbAdapter = new itemDbAdapter(this);
		dbAdapter.open();
		
		String SELECT_PHOTO_SQL =  "Select "+itemDbAdapter.PHOTO_KEY_ID
					+" from "+itemDbAdapter.DATABASE_PHOTO_TABLE
					+" where "+itemDbAdapter.PHOTO_KEY_URI+" = '"+photoURI+"'";
		Cursor photoIdCursor = dbAdapter.mDb.rawQuery(SELECT_PHOTO_SQL, null);
		photoIdCursor.moveToFirst();
        
        photoId = photoIdCursor.getLong(0);
        Log.i(TAG, "photoId : "+photoId);
        
        String SELECT_ITEM_SQL = "Select "+itemDbAdapter.KEY_ID+", "+itemDbAdapter.KEY_NAME+", "+itemDbAdapter.KEY_CATEGORY
        		+", "+itemDbAdapter.KEY_BRAND+", "+itemDbAdapter.KEY_WEATHER+", "+itemDbAdapter.KEY_IMAGE
        		+" From "+itemDbAdapter.DATABASE_TABLE+" where "+itemDbAdapter.KEY_IMAGE+" = '"+photoId+"'";
        Cursor itemCursor = dbAdapter.rawQuery(SELECT_ITEM_SQL);
        Log.i(TAG,SELECT_ITEM_SQL+"\n"+itemCursor.getCount());

        itemCursor.moveToFirst();
        
        itemId = itemCursor.getLong(0);
        nameText.setText(itemCursor.getString(1));
        cateText.setText(itemCursor.getString(2));
        brandText.setText(itemCursor.getString(3));
        weatherText.setText(itemCursor.getString(4));
        Bitmap bm = BitmapFactory.decodeFile(photoURI);
		Log.d(TAG, "filefolder : " +photoURI);
		imagePhoto.setImageBitmap(bm);
        
    	editButton.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(DetailActivity.this, AddActivity.class);
				startActivity(intent);
			}
    	});
    	deleteButton.setOnClickListener(new Button.OnClickListener(){
    		
    		public void onClick(View v){
    			showDialog(BasicInfo.CONFIRM_DELETE);
//    			deleteMemo(photoURI, itemId);
    		}
    	});
    }
    /**
     * 메모 삭제
     */
    private void deleteItem(String photoURI, long itemId) {
 
        // delete photo record
        Log.d(TAG, "deleting previous photo record and file : " + photoURI);
        String SQL = "delete from " + itemDbAdapter.DATABASE_PHOTO_TABLE +
                    " where "+ itemDbAdapter.PHOTO_KEY_URI +" = '" + photoURI + "'";
        Log.d(TAG, "SQL : " + SQL);
        if (dbAdapter.mDb != null) {
        	dbAdapter.mDb.execSQL(SQL);
        	Log.i(TAG,"delete photo DB : "+photoURI);
        }
 
        File photoFile = new File(photoURI);
        if (photoFile.exists()) {
            photoFile.delete();
            Log.i(TAG,"delete photo file : "+photoURI);
        }
 
 
 
        // delete memo record
        Log.d(TAG, "deleting previous memo record : " + itemId);
        SQL = "delete from " + itemDbAdapter.DATABASE_TABLE +
                    " where "+itemDbAdapter.KEY_ID+" = '" + itemId + "'";
        Log.d(TAG, "SQL : " + SQL);
        if (dbAdapter.mDb != null) {
        	dbAdapter.mDb.execSQL(SQL);
        	Log.i(TAG,"delete item : "+itemId);
        }
        setResult(RESULT_OK);
 
        finish();
    }
    protected Dialog onCreateDialog(int id){
    	AlertDialog.Builder builder = null;
    	switch(id){
    	case BasicInfo.CONFIRM_DELETE:
    		builder = new AlertDialog.Builder(this);
    		builder.setMessage("삭제하시겠습니까?");
    		builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					deleteItem(photoURI, itemId);
				}
			});
    		builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dismissDialog(BasicInfo.CONFIRM_DELETE);
				}
			});
    		break;
    	default:
    		break;
    	}
    	return builder.create();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_detail, menu);
        return true;
    }
}
