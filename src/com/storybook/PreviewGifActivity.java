package com.storybook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class PreviewGifActivity extends Activity {
	private ImageView gifView;
	private Timer timer;
	private int index;
	private MyHandler handler;
	private ArrayList<Bitmap> photos;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_preview_gif);
	    handler = new MyHandler();
	    gifView = (ImageView) findViewById(R.id.gif_view);
	}
	
	public void onResume(){
		super.onResume();
		Intent i = getIntent();
		int albumIndex = i.getIntExtra("index", 0);
		int time = i.getIntExtra("time", 500);
		photos = MainActivity.albums.get(albumIndex).getPhotos();
		
		index = 0;
	    timer = new Timer();
	    timer.schedule(new TickClass(), 500, (long) time);
	}

	private class TickClass extends TimerTask
	{
	    @Override
	    public void run() {
	        // TODO Auto-generated method stub
	    	if(index < photos.size()){
	    		handler.sendEmptyMessage(index);
	    	}
	    	else{
	    		timer.cancel();
	    		timer.purge();
	    		finish();
	    	}
	    }
	}

	private class MyHandler extends Handler
	{
	    @Override
	    public void handleMessage(Message msg) {
	        // TODO Auto-generated method stub
	        super.handleMessage(msg);
	        
	        Bitmap bmp = photos.get(index);
			gifView.setImageBitmap(bmp);
			Log.v("Loading Image: ",index+"");
			index++;
	    }
	}
}
