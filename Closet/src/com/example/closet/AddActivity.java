package com.example.closet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

public class AddActivity extends Activity {

	private itemDbAdapter dbAdapter;
	private static final String TAG = "AddActivity";
	Cursor itemCursor;

	ImageView mPhoto;
	EditText nameEditText;
	EditText brandEditText;
	Spinner catspin;
	Spinner weatherSpin;
	Button addButton;
	Button imageButton;

	String tempPhotoUri;
	String mMediaPhotoId;
	String mMediaPhotoUri;

	String folder;

	Bitmap resultPhotoBitmap;

	boolean isPhotoFileSaved;
	boolean isPhotoCaptured;

	int mSelectdContentArray;
	int mChoicedArrayItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		mPhoto = (ImageView) findViewById(R.id.imageClothe);

		folder = Environment.getExternalStorageDirectory() + "/"
				+ BasicInfo.FOLDER_PHOTO + "/";

		nameEditText = (EditText) findViewById(R.id.nameEditText);
		brandEditText = (EditText) findViewById(R.id.brandEditText);
		catspin = (Spinner) findViewById(R.id.categorySpinner);
		weatherSpin = (Spinner) findViewById(R.id.weatherSpinner);
		addButton = (Button) findViewById(R.id.addbutton);
		imageButton = (Button) findViewById(R.id.imageButton);

		dbAdapter = new itemDbAdapter(this);
		dbAdapter.open();

		// get intent values
		String str = getIntent().getStringExtra("cateString");
		int cateNum = getIntent().getIntExtra("cateNum", 0);
		// Toast.makeText(getBaseContext(), "category="+str,
		// Toast.LENGTH_SHORT).show();

		// category spinner setting
		ArrayAdapter catadap = ArrayAdapter.createFromResource(this,
				R.array.category, android.R.layout.simple_spinner_item);
		catadap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		catspin.setAdapter(catadap);
		catspin.setSelection(cateNum, true);

		// weather spinner setting
		ArrayAdapter weatherAdap = ArrayAdapter.createFromResource(this,
				R.array.weather, android.R.layout.simple_spinner_item);
		weatherAdap
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		weatherSpin.setAdapter(weatherAdap);

		imageButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isPhotoCaptured || isPhotoFileSaved) {
					showDialog(BasicInfo.CONTENT_PHOTO_EX);
				} else {
					showDialog(BasicInfo.CONTENT_PHOTO);
				}
			}
		});
		brandEditText.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN)
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(
								brandEditText.getWindowToken(), 0);
						return true;
					}
				return false;
			}
		});

		nameEditText.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN)
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(
								nameEditText.getWindowToken(), 0);
						return true;
					}
				return false;
			}
		});
		addButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				// insert item into adapter
				// get values: name, brand, category, weather
				saveInput();
			}
		});
		// Intent intent = getIntent();

	}

	public void saveInput() {
		Intent intent = new Intent(getBaseContext(), GridViewActivity.class);

		String photoFilename = insertPhoto();
		int photoId = -1;
		String stringPhotoId = null;

		String SQL = null;

		if (photoFilename != null) {
			// query picture id
			SQL = "select " + itemDbAdapter.PHOTO_KEY_ID + " from "
					+ itemDbAdapter.DATABASE_PHOTO_TABLE + " where URI = '"
					+ photoFilename + "'";
			Log.d(TAG, "SQL : " + SQL);
			if (itemDbAdapter.mDb != null) {
				Cursor cursor = itemDbAdapter.mDb.rawQuery(SQL, null);
				if (cursor.moveToNext()) {
					photoId = cursor.getInt(0);
					stringPhotoId = Integer.toString(photoId);
					Log.d(TAG, "photo id : " + stringPhotoId);
				}
				cursor.close();
			}

			String mName = nameEditText.getText().toString();
			String mBrand = brandEditText.getText().toString();
			String mCategory = catspin.getSelectedItem().toString();
			String mWeather = weatherSpin.getSelectedItem().toString();
			String mImage = stringPhotoId;

			Log.d("minbung", "DB Data :  name " + mName + " category : "
					+ mCategory + " brand : " + mBrand + " weather : "
					+ mWeather + " image : " + mImage);

			Item newItem = new Item(mName, mCategory, mBrand, mWeather, mImage);
			long id = dbAdapter.insertItem(newItem);

			Cursor itemCursor = dbAdapter.setItemCursor(id);
			String itemName = itemCursor.getString(itemDbAdapter.COLUMN_NAME);
			String categoryName = itemCursor
					.getString(itemDbAdapter.COLUMN_CATEGORY);
			String brandName = itemCursor.getString(itemDbAdapter.COLUMN_BRAND);
			String weatherName = itemCursor
					.getString(itemDbAdapter.COLUMN_WEATHER);
			String imageName = itemCursor.getString(itemDbAdapter.COLUMN_IMAGE);

			Log.d("minbung", "DB insert : (" + id + ") name " + itemName
					+ " category : " + categoryName + " brand : " + brandName
					+ " weather : " + weatherName + " image : " + imageName);

			intent.putExtra("cateString", categoryName);
			startActivity(intent);
		} else {
			showDialog(BasicInfo.CONFIRM_NO_PHOTO);
		}
	}

	/**
	 * 앨범의 사진을 사진 폴더에 복사한 후, PICTURE 테이블에 사진 정보 추가 이미지의 이름은 현재 시간을 기준으로 한
	 * getTime() 값의 문자열 사용
	 * 
	 * @return 새로 추가된 이미지의 이름
	 */

	private String insertPhoto() {
		String photoName = null;
		File photoFolder = new File(folder);
		if (isPhotoCaptured) { // captured Bitmap
			try {

				Log.i(TAG, "photoFolder : " + photoFolder);
				// 폴더가 없다면 폴더를 생성한다.
				if (!photoFolder.isDirectory()) {
					Log.d(TAG, "creating photo folder : " + photoFolder);
					photoFolder.mkdirs();
				}

				// Temporary Hash for photo file name
				photoName = createFilename() + ".png";

				FileOutputStream outstream = new FileOutputStream(photoFolder
						+ "/" + photoName);
				resultPhotoBitmap.compress(CompressFormat.PNG, 100, outstream);
				outstream.close();

				if (photoName != null) {
					Log.d(TAG, "isCaptured            : " + isPhotoCaptured);

					// INSERT PICTURE INFO
					String SQL = "insert into "
							+ itemDbAdapter.DATABASE_PHOTO_TABLE
							+ "(URI) values(" + "'" + photoFolder + "/"
							+ photoName + "')";
					Log.i(TAG, "photo SQL : " + SQL);
					if (itemDbAdapter.mDb != null) {
						itemDbAdapter.mDb.execSQL(SQL);
					}
				}

			} catch (IOException ex) {
				Log.d(TAG, "Exception in copying photo : " + ex.toString());
			}
		}
		Log.i(TAG, "photoName : " + photoName);

		if (photoName != null)
			return photoFolder + "/" + photoName;
		else
			return null;
	}

	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = null;

		switch (id) {
		case BasicInfo.CONTENT_PHOTO:
			builder = new AlertDialog.Builder(this);
			mSelectdContentArray = R.array.array_photo;
			// builder.setTitle("선택하세요");
			builder.setSingleChoiceItems(mSelectdContentArray, 0,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							mChoicedArrayItem = which;
						}
					});
			builder.setPositiveButton("선택",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if (mChoicedArrayItem == 0) {
								showPhotoCaptureActivity();
							} else if (mChoicedArrayItem == 1) {
								showPhotoSelectionActivity();
							}
						}
					});
			builder.setNegativeButton("취소",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

						}
					});
			break;
		case BasicInfo.CONFIRM_NO_PHOTO:
			builder = new AlertDialog.Builder(this);
			builder.setMessage("사진을 입력하세요!");
			builder.setPositiveButton("확인", null);
			builder.show();
			break;
		default:
			break;

		}
		return builder.create();
	}

	public void showPhotoCaptureActivity() {
		Intent intent = new Intent(getApplicationContext(),
				SampleCaptureActivity.class);
		startActivityForResult(intent, BasicInfo.REQ_PHOTO_CAPTURE_ACTIVITY);
	}

	public void showPhotoSelectionActivity() {
		Intent intent = new Intent(getApplicationContext(),
				SampleCaptureActivity.class);
		startActivityForResult(intent, BasicInfo.REQ_PHOTO_SELECTION_ACTIVITY);
	}

	/**
	 * 저장된 사진 파일 확인
	 */
	private boolean checkCapturedPhotoFile() {
		File file = new File(folder + "captured.png");
		if (file.exists()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_add, menu);
		return true;
	}

	/**
	 * 다른 액티비티로부터의 응답 처리
	 */
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		switch (requestCode) {
		case BasicInfo.REQ_PHOTO_CAPTURE_ACTIVITY: // 사진 찍는 경우
			Log.d(TAG, "onActivityResult() for REQ_PHOTO_CAPTURE_ACTIVITY.");

			if (resultCode == RESULT_OK) {
				Log.d(TAG, "resultCode : " + resultCode);

				boolean isPhotoExists = checkCapturedPhotoFile();
				if (isPhotoExists) {
					Log.d(TAG, "image file exists : " + folder + "captured.png");

					resultPhotoBitmap = BitmapFactory.decodeFile(folder
							+ "captured.png");

					tempPhotoUri = "captured.png";

					mPhoto.setImageBitmap(resultPhotoBitmap);
					isPhotoCaptured = true;

					mPhoto.invalidate();
				} else {
					Log.d(TAG, "image file doesn't exists : " + folder
							+ "captured.png");
				}
			}

			break;
		/*
		 * case BasicInfo.REQ_PHOTO_SELECTION_ACTIVITY: // 사진을 앨범에서 선택하는 경우
		 * Log.d(TAG, "onActivityResult() for REQ_PHOTO_LOADING_ACTIVITY.");
		 * 
		 * if (resultCode == RESULT_OK) { Log.d(TAG, "resultCode : " +
		 * resultCode);
		 * 
		 * Uri getPhotoUri = intent.getParcelableExtra(BasicInfo.KEY_URI_PHOTO);
		 * try { resultPhotoBitmap =
		 * BitmapFactory.decodeStream(getContentResolver
		 * ().openInputStream(getPhotoUri), null, null); } catch
		 * (FileNotFoundException e) { e.printStackTrace(); }
		 * 
		 * mPhoto.setImageBitmap(resultPhotoBitmap); isPhotoCaptured = true;
		 * 
		 * mPhoto.invalidate(); }
		 * 
		 * break;
		 */
		}
	}

	private String createFilename() {
		Date curDate = new Date();
		String curDateStr = String.valueOf(curDate.getTime());

		return curDateStr;
	}

}