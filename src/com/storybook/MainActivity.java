package com.storybook;

import java.util.ArrayList;

import com.storybook.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	public final static int FIRST_PHOTO_REQUEST = 42;

	public static ArrayList<Album> albums = new ArrayList<Album>();
	public static Bitmap insertMarker = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
	AlbumArrayAdapter adapter;
	GridView gridView;
	Button create_album;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		adapter = new AlbumArrayAdapter(this, R.layout.list_item, albums);

		create_album = (Button) findViewById(R.id.create_album);
		create_album.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		        
		        builder.setTitle("Input Album Title");
		        final EditText input = new EditText(MainActivity.this);
		        builder.setView(input);
		        
		        builder.setPositiveButton("Take a Picture!", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		            	String title = input.getText().toString();
		                Album a = new Album(title, new ArrayList<Bitmap>());
		                ArrayList<Bitmap> newPhotos = a.getPhotos();
		                newPhotos.add(insertMarker);
		                a.setPhotos(newPhotos);
		                Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
		                startActivityForResult(i, FIRST_PHOTO_REQUEST);
		                albums.add(a);
		                //add to mongodb
		                dialog.cancel();
		            }
		        });
		        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		            }
		        });
		        
		        AlertDialog add = builder.create(); 
		        add.show();

			}

		});

		gridView = (GridView) findViewById(R.id.gridView);

		gridView.setAdapter(adapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ViewHolder holder = (ViewHolder) view.getTag();
				String title = holder.title_tv.getText().toString();
				ArrayList<Bitmap> photos;
				if (holder.photos != null)
					photos = holder.photos;
				else
					photos = new ArrayList<Bitmap>();
				Intent i = new Intent(getApplicationContext(),
						ViewAlbumActivity.class);
				i.putExtra("title", title);
				i.putParcelableArrayListExtra("photos", photos);
				startActivity(i);
			}
		});

		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// LayoutInflater li = LayoutInflater.from(MainActivity.this);
				// View promptsView =
				// li.inflate(R.layout.prompts_long_click_album, null);

				ViewHolder holder = (ViewHolder) view.getTag();
				final String title = holder.title_tv.getText().toString();
				final ArrayList<Bitmap> photos;
				if (holder.photos != null)
					photos = holder.photos;
				else
					photos = new ArrayList<Bitmap>();
				Dialog albumLongClickDialog = new Dialog(MainActivity.this);
				albumLongClickDialog.getWindow().requestFeature(
						Window.FEATURE_NO_TITLE);
				albumLongClickDialog.setContentView(getLayoutInflater()
						.inflate(R.layout.prompts_long_click_album, null));

				LinearLayout prompts_long_click_ll = (LinearLayout) findViewById(R.id.prompts_long_click_ll);
				Button prompt_long_click_view = (Button) findViewById(R.id.prompt_long_click_view);
				prompt_long_click_view.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent i = new Intent(getApplicationContext(),
								ViewAlbumActivity.class);
						i.putExtra("title", title);
						i.putParcelableArrayListExtra("photos", photos);
						startActivity(i);
					}
					
				});
				Button prompt_long_click_edit = (Button) findViewById(R.id.prompt_long_click_edit);
				Button prompt_long_click_delete = (Button) findViewById(R.id.prompt_long_click_delete);
				return false;

			}
		});
	}

	public void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode == RESULT_OK){
			if(requestCode == FIRST_PHOTO_REQUEST){
				Album album = null;
				for(Album a : albums)
					if(a.getPhotos().size() == 1 && a.getPhotos().get(0).equals(insertMarker))
						album = a;
				
				if(album != null){
					Bitmap photo = (Bitmap) data.getExtras().get("data");
					ArrayList<Bitmap> newPhotos = new ArrayList<Bitmap>();
					newPhotos.add(photo);
					album.setPhotos(newPhotos);
				}
			}
		}
        	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
