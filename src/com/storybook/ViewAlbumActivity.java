package com.storybook;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;

public class ViewAlbumActivity extends Activity{
	
	
	public static final int ADD_IMAGE_REQUEST = 1;
	
	Album album;
	int albumIndex;
	LinearLayout ll;
	LinearLayout buttons;
	Button add_image;
	Button preview_gif;
	Button publish_to_facebook;
	HorizontalScrollView images_hsv;
	LinearLayout images_ll;
	TextView title_tv;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Context context = getApplicationContext();
        
        this.setContentView(R.layout.activity_view_album);
 
        ll = (LinearLayout) this.findViewById(R.id.ll);
        
        buttons = (LinearLayout) this.findViewById(R.id.buttons);
        
        add_image = (Button) this.findViewById(R.id.add_image);
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	File dir = new File(Environment.getExternalStorageDirectory(), "StoryBook");
            	dir.mkdirs();
            	dir = new File(dir, album.getTitle() + "%%" + album.getPhotos().size() + ".jpg");
            	Uri uriSavedImage=Uri.fromFile(dir);
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                startActivityForResult(i, ADD_IMAGE_REQUEST); 
            }
        });
        
        preview_gif = (Button) this.findViewById(R.id.preview_gif);
        preview_gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PreviewGifActivity.class);
                i.putExtra("index", albumIndex);
                startActivity(i);
            }
        });
        
        publish_to_facebook = (Button) this.findViewById(R.id.publish_to_facebook);
        publish_to_facebook.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ArrayList<Bitmap> photos = album.getPhotos();
				for(int i = 0; i<photos.size(); i++){
					Request request = Request.newUploadPhotoRequest(Session.getActiveSession(), photos.get(i), new Request.Callback(){
						@Override
						public void onCompleted(Response res){
							int duration = Toast.LENGTH_SHORT;
							CharSequence text = "Images uploaded to facebook.";
							Toast.makeText(context, text, duration).show();
							return;
						}
					});
					request.executeAsync();
				}
			}
		});
        
        images_hsv = (HorizontalScrollView) this.findViewById(R.id.images_hsv);
        images_ll = (LinearLayout) this.findViewById(R.id.images_ll);
        title_tv = (TextView) this.findViewById(R.id.current_album_title);
        
    }
	
	public void onResume(){
		super.onResume();
		MainActivity.loadAlbums();
		Intent i = getIntent();
		albumIndex = i.getIntExtra("index", 0);
		album = MainActivity.albums.get(albumIndex);
		images_ll.removeAllViews();
		for(Bitmap photo : album.getPhotos()){
			ImageView photo_iv = new ImageView(this);
			photo_iv.setMinimumWidth(300);
			photo_iv.setMinimumHeight(300);
			photo_iv.setImageBitmap(photo);
			images_ll.addView(photo_iv);
		}
		title_tv.setText(album.getTitle());
	}
	
	public void onPause(){
		super.onPause();
		for(int i = 0; i < MainActivity.albums.size(); i++)
			if(MainActivity.albums.get(i).getTitle().equals(album.getTitle()))
				MainActivity.albums.set(i, album);
	}

}
