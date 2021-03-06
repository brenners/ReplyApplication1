package com.reply.salesmen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;














//import com.example.android.camera2raw.tests.R;
import com.reply.salesmen.connect.ConnectionManager;
import com.reply.salesmen.control.AsyncTaskHandler;
import com.reply.salesmen.control.CameraManager;
import com.reply.salesmen.control.ConstantManager;
import com.reply.salesmen.control.SettingsManager;
import com.reply.salesmen.control.Voice.VoiceManager;
import com.reply.salesmen.control.Voice.VoiceManagerInterface;
import com.reply.salesmen.view.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends Activity implements SurfaceHolder.Callback {

	/******************************************
	 * 				Declaration 			  *
	 *****************************************/
	
	public static AssetManager assetManager;
	
	private AsyncTaskHandler asyncTask;
	private ProgressDialog pd;
	private AlertDialog.Builder adb;
		
	// Voice Recognition: List result in ListView
	public ListView wordsList;	
	
	/******************************************
	 * 				Constructor 			  *
	 *****************************************/
	
	
	/******************************************
	 * 				Methods 			  *
	 *****************************************/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		
		// Initialize objects and connections
		init();	
	}
	
	private void init() {	
		
		// define Asset Manager		
		if(assetManager == null) {
			assetManager = this.getAssets();
		}
		
		// Test Voice Recognition
		wordsList = (ListView) findViewById(R.id.L_List);
		
		// Disable button if no recognition service is present
		Button b_voice = (Button) findViewById(R.id.B_Voice);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
        	b_voice.setEnabled(false);
        	b_voice.setText("Voice unabled");
        }
        
        // Init Camera Manager
        CameraManager cameraManager = CameraManager.getInstance(this);
        
        if(cameraManager.getStatus().equals("Locked")) {
        	// Calculate Screen size
        	Display display = this.getWindowManager().getDefaultDisplay();
    		Point size = new Point();
    		display.getSize(size);
    		int width = size.x;
    		int height = size.y;
    		
    		// Get Video View and define size
			VideoView videoView = (VideoView) this.findViewById(R.id.videoView1);
			videoView.setLayoutParams(new FrameLayout.LayoutParams(width,height));
		    SurfaceHolder holder = videoView.getHolder();		    
		    holder.addCallback(this);
		    holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
	}
	
	private void startAsyncTask() {
		ConnectionManager conMan = ConnectionManager.getInstance();
		
		// If ConnectinManager is not correctly connected to Server, try it
		if(conMan.getStatus() != 200)
		{
			this.asyncTask = new AsyncTaskHandler(this);		
			this.asyncTask.execute();
		}		
	}
	
	public void onClick_Listener(View view) { 	

		SettingsManager sm = SettingsManager.getInstance();
	
		switch(view.getId()) {
			case (R.id.B_SalesOrder):
				if(sm.isDataLoadCompleted())					
					navigate2Intent(SalesOrderView.class);			
				else {
					String title = "No Data is loaded:";
					String msg = "Do you want to load Mock Data first?: ";
					String posButtonTitle = ConstantManager.GO_ON_WITHOUT_LOADING;
					String negButtonTitle = ConstantManager.RETRY_CONNECTION;									
					showAlertDialog(title, msg, posButtonTitle, negButtonTitle);					
				}
				break;
			case (R.id.B_Settings):
				navigate2Intent(SettingsView.class);					
				break;
			case (R.id.B_LoadData):
				// start asynchron task for connecting to Server
				this.startAsyncTask();
				sm.setDataLoadCompleted(true);			
				break;
			case (R.id.B_Voice):				
				VoiceManager voice = VoiceManager.getInstance(this);				
				voice.start();
				break;
		}
	}
	
	public void navigate2Intent(Class cl) {		
		Intent intent = new Intent(this, cl);
		startActivity(intent);	
	}
	
	public void showToast(final String toast)
	{
	    runOnUiThread(new Runnable() {
	        public void run()
	        {
	            Toast.makeText(MainActivity.this, toast, Toast.LENGTH_LONG).show();
	        }
	    });
	}

	public void initProgressDialog(String msg) {
		// initialize Progress Dialog
		pd = new ProgressDialog(MainActivity.this);		
		// Show Progress Dialog Bar				
		pd.setMessage(msg);		       
	}
	
	public void showProgressDialog() {
		if(pd == null) {
			pd.show();	
		}
	}
	
	public void showProgressDialog(String msg) {
		// initialize Progress Dialog
		pd = new ProgressDialog(MainActivity.this);		
		// Show Progress Dialog Bar				
		pd.setMessage(msg);		       
		// Show Message
		pd.show();
	}
	
	public void closeProgessDialog() {
		if(pd != null) {
			pd.dismiss();
	        pd.cancel();	
		}		
	}

	public void initAlertDialog(String title, String msg, final String posButtonTitle, final String negButtonTitle) {
		// initialize Alert Dialog Box
		adb = new AlertDialog.Builder(this)
							.setTitle(title)
							.setMessage(msg)
							.setPositiveButton(posButtonTitle, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) { 
						            // continue with cancel
									dialog.cancel();									
						        }
							})
							.setNegativeButton(negButtonTitle,  new DialogInterface.OnClickListener() {
						        public void onClick(DialogInterface dialog, int which) { 
						        	dialog.cancel();
						            // start connecting to Server again
						        	if(negButtonTitle.equals("Retry"))
						        		startAsyncTask();		        	
						        }
						     })
						     .setIcon(android.R.drawable.ic_dialog_alert);
		
	
	}
	
	public void showAlertDialog() {
		if(adb != null)
			adb.show();
	}
	
	public void showAlertDialog(String title, String msg, final String posButtonTitle, final String negButtonTitle) {
		new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(msg)
		.setPositiveButton(posButtonTitle, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 	            
				dialog.cancel();	// continue with cancel
				
				if(posButtonTitle.equals(ConstantManager.GO_ON_WITHOUT_LOADING))
					navigate2Intent(SalesOrderView.class);				
	        }
		})
		.setNegativeButton(negButtonTitle,  new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	dialog.cancel();
	            // start connecting to Server again
	        	if(negButtonTitle.equals(ConstantManager.RETRY_CONNECTION)) {
	        		SettingsManager sm = SettingsManager.getInstance();
	        		sm.setDataLoadCompleted(true);
	        		startAsyncTask();
	        		navigate2Intent(SalesOrderView.class);
	        	}	        		
	        }
	     })
	     .setIcon(android.R.drawable.ic_dialog_alert)
	     .show();
	}
	
    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VoiceManagerInterface.REQUESTCODE+1 && resultCode == RESULT_OK) {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList <String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);  
            this.navigateByVoice(matches);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

	@Override
	public void surfaceCreated(SurfaceHolder holder) {		
		
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
	
	private void navigateByVoice(ArrayList<String> matches) {		
		
		boolean found = false;
		
		for (String match : matches) {
			switch(match) {
			case "sales order":
			case "order":
				navigate2Intent(SalesOrderView.class);
				found = true;
				break;	// leave switch
			case "settings":
			case "setting":
				navigate2Intent(SettingsView.class);
				found = true;
				break;	// leave switch
			case "load":
			case "load data":
			case "data":
				// start connecting to Server
				this.startAsyncTask();
				SettingsManager sm = SettingsManager.getInstance();
				sm.setDataLoadCompleted(true);
				found = true;
				break;	// leave switch
			}
			
			if(found)
				break;	// leave for loop
		}
		
        wordsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, matches));		
	}
	
}
