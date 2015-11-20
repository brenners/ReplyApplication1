package com.reply.salesmen.view;

import com.reply.salesmen.R;
import com.reply.salesmen.control.CameraManager;
import com.reply.salesmen.control.SettingsManager;
import com.reply.salesmen.control.Voice.VoiceManager;
import com.reply.salesmen.model.CollectionWrapper;
import com.reply.salesmen.model.SalesOrderSet;
import com.reply.salesmen.model.elements.*;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;

public class SalesOrderView extends Activity implements SurfaceHolder.Callback {

	private CollectionWrapper colWrap;
	private SalesOrderSet so_set;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales_order_view);
		
		// Get first Sales Order
		colWrap = CollectionWrapper.getInstance();
		so_set = colWrap.getSalesOrderSet();
		SalesOrder so = so_set.getLast();
		
		init();
		
		// Show first object on view
		setObject2View(so, false);		
	}
	
	private void init() {
		// Init Camera Manager
        CameraManager cameraManager = CameraManager.getInstance(this);
        
        if(cameraManager.getStatus().equals("Locked")) {
        	
        	SettingsManager sm = SettingsManager.getInstance();
			String deviceName = sm.getCurrentDevice();
			
			if(deviceName.equals("Vuzix M100")) {
				// Special handling for Vuzix
				cameraManager.stopCamera(this);
			}
        	
        	// Calculate Screen size
        	Display display = this.getWindowManager().getDefaultDisplay();
    		Point size = new Point();
    		display.getSize(size);
    		int width = size.x;
    		int height = size.y;
    		
    		// Get Video View and define size
			VideoView videoView = (VideoView) this.findViewById(R.id.videoViewOrderView);
			videoView.setLayoutParams(new FrameLayout.LayoutParams(width,height));
		    SurfaceHolder holder = videoView.getHolder();		    
		    holder.addCallback(this);
		    holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
	}
	
	public void onClick_Listener(View view) {
		SalesOrder so = null;
		
		switch(view.getId()) {
			case (R.id.B_Next):
				// Navigate and display to next Sales Order
				so = so_set.getNext();
				setObject2View(so, false);
				break;
			case (R.id.B_Previous):
				// Navigate and display to previous Sales Order
				so = so_set.getPrev();
				setObject2View(so, false);
				break;
			case (R.id.B_Items):
				navigate2Intent(SalesOrderItemView.class);
				break;
		}
		// clear unnecessary object
		so = null;
	}

	
	private void setObject2View(SalesOrder so, boolean editable) {
		
		if(so == null)
			return;
		
		// Save current Object ID
		SettingsManager sm = SettingsManager.getInstance();
		sm.setCurrentObjectId(so.getObjectId());
		
		//
		// To Do: Implement the necessary data binding
		// Example: TextView t_objID = (TextView) findViewById(R.id.ET_ObjectID);
		//
		
	}
	
	private void navigate2Intent(Class cl) {		
		Intent intent = new Intent(this, cl);
		startActivity(intent);	
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		CameraManager cameraManager = CameraManager.getInstance(this);
        
        if(cameraManager.getStatus().equals("Locked")) {
        	cameraManager.setPreviewDisplay(holder);
        	cameraManager.rotateCamera(this);
        }		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) { 
		
	}
	
	@Override
	protected void onDestroy() {
		CameraManager cameraManager = CameraManager.getInstance(this);
		if(cameraManager.getStatus().equals("Locked")) {
        	cameraManager.stopCamera(this);
        }		
		
		VoiceManager voice = VoiceManager.getInstance(this);				
		voice.stop();
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		CameraManager cameraManager = CameraManager.getInstance(this);
		if(cameraManager.getStatus().equals("Locked")) {
        	cameraManager.stopCamera(this);
        }		
		
		VoiceManager voice = VoiceManager.getInstance(this);				
		voice.stop();
		super.onBackPressed();
	}
	
}
