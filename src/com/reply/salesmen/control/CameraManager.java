package com.reply.salesmen.control;

import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

public class CameraManager {
	
	private static CameraManager cameraManager;
	private static Camera camera = null;
	private static String status = "";
	private Activity currActivity = null;
	
	private CameraManager(Activity act) {
		init(act);
	}
	
	public static CameraManager getInstance(Activity act) {
		if(cameraManager == null) {
			cameraManager = new CameraManager(act);
		} else if(camera == null) {
			init(act);
		}
		
		return cameraManager;	
	}
	
	private static void init(Activity act) {		
		try {
	        camera = Camera.open();
	        camera.lock();
	        status = "Locked";
	        
	    } catch(RuntimeException re) {
	        Log.e(ConstantManager.CONSOLE_TAG, "Could not initialize the Camera");
	        re.printStackTrace();
	        status = "Fail";
	    }
	}
	
	public Camera getCamera(Activity act) {
		if(camera == null) {
			init(act);
		}		
		return camera;
	}
	
	public void rotateCamera(Activity activity) {
		SettingsManager sm = SettingsManager.getInstance();
		int result = 0;
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();	
		
		//
		// To Do: Please, implemenet missing functionality to rotate camera
		//
		
		camera.setDisplayOrientation(result);       
	}
	
	public void stopCamera(Activity activity) {		
		if(camera != null) { 
			//stop the preview  
	        camera.stopPreview();  
	        camera.setPreviewCallback(null);
	        // unlock camera, so other applications can use camera
	        camera.unlock();
	        //release the camera  
	        camera.release();	        
	        //unbind the camera from this object  
	        camera = null;
		}
	}
	
	public void setPreviewDisplay(SurfaceHolder holder) {
		try {
			//
            // To Do: 
			//	The camera is instantiated but the camera does not start successfully
			//	Please, implement the necessary functionality to start the preview. 
			//	Hint: You need a reference between the surface holder (display) and the camera
            //
			
		} catch(IOException e)  {
			Log.e(ConstantManager.CONSOLE_TAG, "Could not place the Camera");
		}
	}
	
	public String getStatus() {
		return status;
	}
}
