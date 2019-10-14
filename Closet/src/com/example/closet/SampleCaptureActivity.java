package com.example.closet;


import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;


public class SampleCaptureActivity extends Activity {

	public static String TAG = "SampleCaptureActivity";
	CameraSurfaceView mCameraView;
	FrameLayout mFrameLayout;
	public String fileString;
	
	boolean processing = false;
	
	public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       
       final Window win = getWindow();
       win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
       requestWindowFeature(Window.FEATURE_NO_TITLE);
       setContentView(R.layout.activity_sample_capture);

       mCameraView = new CameraSurfaceView(getApplicationContext());
       mFrameLayout = (FrameLayout) findViewById(R.id.frame);
       mFrameLayout.addView(mCameraView);

       setCaptureBtn();

   }
   
   public void setCaptureBtn(){
       Button saveBtn = (Button) findViewById(R.id.capture_takeBtn);
       saveBtn.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
        	   if (!processing) {
                   processing = true;
                   mCameraView.capture(new CameraPictureCallback());
               }
           }
       });	   
   }
   class CameraPictureCallback implements Camera.PictureCallback {
	   
       public void onPictureTaken(byte[] data, Camera camera) {
           Log.v(TAG, "onPictureTaken() called.");

           int bitmapWidth = 480;
           int bitmapHeight = 360;

           Bitmap capturedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
           Bitmap scaledBitmap = Bitmap.createScaledBitmap(capturedBitmap, bitmapWidth, bitmapHeight, false);

           Bitmap resultBitmap = null;

           Matrix matrix = new Matrix();
           matrix.postRotate(0);

           resultBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);

           try {
        	   String folder = Environment.getExternalStorageDirectory()+"/"+BasicInfo.FOLDER_PHOTO+"/";
               File photoFolder = new File(folder);

               //폴더가 없다면 폴더를 생성한다.
               if(!photoFolder.isDirectory()){
                   Log.i(TAG, "creating photo folder : " + photoFolder);
                   photoFolder.mkdirs();
                   if(!photoFolder.exists())
                       Log.e(TAG, "ERROR creating photo folder : " + photoFolder);
               }

               String photoName = "/captured.png";
               fileString = photoFolder+"/"+ photoName;
               // 기존 이미지가 있으면 삭제
               File file = new File(fileString);
               if(file.exists()) {
                   file.delete();
               }

               FileOutputStream outstream = new FileOutputStream(fileString);
               resultBitmap.compress(CompressFormat.PNG, 100, outstream);
               outstream.close();

           } catch (Exception ex) {
               Log.e(TAG, "Error in writing captured image.", ex);
               showDialog(BasicInfo.IMAGE_CANNOT_BE_STORED);
           }

           showParentActivity();
       }
   }



   /**
    * 부모 액티비티로 돌아가기
    */
   private void showParentActivity() {
       Intent resultIntent = new Intent();
       resultIntent.putExtra("filename", fileString);
       setResult(RESULT_OK, resultIntent);

       finish();
   }


   protected Dialog onCreateDialog(int id) {
       Log.d(TAG, "onCreateDialog() called");

       switch (id) {
           case BasicInfo.IMAGE_CANNOT_BE_STORED:
               AlertDialog.Builder builder = new AlertDialog.Builder(this);
               builder.setTitle("사진을 저장할 수 없습니다. SD카드 상태를 확인하세요.");
               builder.setPositiveButton("확인",
                       new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int which) {

                           }
                       });
               return builder.create();
       }

       return null;
   }

   /**
    * 카메라 미리보기를 위한 서피스뷰
    *
    * @author Mike
    * @date 2011-07-01
    */
   public class CameraSurfaceView extends SurfaceView implements Callback {
    
       public static final String TAG = "CameraSurfaceView";
    
       private SurfaceHolder mHolder;
       private Camera mCamera = null;
    
       public CameraSurfaceView(Context context) {
           super(context);
    
           mHolder = getHolder();
           mHolder.addCallback(this);
           mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
       }
    
       public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
       	// 표시할 영역의 크기를 알았으므로 해당 크기로 Preview를 시작합니다.
    	   Camera.Parameters parameters = mCamera.getParameters();
    	   parameters.setPreviewSize(getWindowManager().getDefaultDisplay().getWidth(),
    			   getWindowManager().getDefaultDisplay().getHeight());
    	   parameters.setRotation(90);
//    	   mCamera.setDisplayOrientation(90);
    	   mCamera.setParameters(parameters);
    	   
    	   mCamera.startPreview();
       }
    
       public void surfaceCreated(SurfaceHolder holder) {
           openCamera();
       }
    
       public void surfaceDestroyed(SurfaceHolder holder) {
           stopPreview();
       }
    
       public Surface getSurface() {
           return mHolder.getSurface();
       }
    
       public boolean capture(Camera.PictureCallback jpegHandler) {
           if (mCamera != null) {
               mCamera.takePicture(null, null, jpegHandler);
               return true;
           } else {
               return false;
           }
       }
    
       public void stopPreview() {
           mCamera.stopPreview();
           mCamera.release();
           mCamera = null;
       }
       public void startPreview() {
           openCamera();
           mCamera.startPreview();
       }
    
       public void openCamera() {
           mCamera = Camera.open();
           try {
               mCamera.setPreviewDisplay(mHolder);
           } catch (Exception ex) {
               Log.e(TAG, "Failed to set camera preview display", ex);
           }
       }
    
   }
   
}