package com.storybook;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;

public class ViewAlbumActivity extends Activity {

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
				File dir = new File(Environment.getExternalStorageDirectory(),
						"StoryBook");
				dir.mkdirs();
				File albumFolder = new File(dir, album.getTitle());
				albumFolder.mkdirs();
				File path = new File(albumFolder, album.getPhotos().size()
						+ ".jpg");
				Uri uriSavedImage = Uri.fromFile(path);
				Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				i.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
				startActivityForResult(i, ADD_IMAGE_REQUEST);
			}
		});

		preview_gif = (Button) this.findViewById(R.id.preview_gif);
		preview_gif.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				final AlertDialog.Builder alert = new AlertDialog.Builder(ViewAlbumActivity.this);
				alert.setTitle("Time Between Frames (ms)");
				
				LayoutInflater inflater = LayoutInflater.from(alert.getContext());
				final View seek_and_value = inflater.inflate(R.layout.preview_gif_dialog, null);
				
				final SeekBar seek = (SeekBar) seek_and_value.findViewById(R.id.preview_gif_dialog_sb);
				final TextView value = (TextView) seek_and_value.findViewById(R.id.preview_gif_dialog_tv);
				seek.setMax(2000);

				seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						// TODO Auto-generated method stub
						value.setText(progress + "");
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
						
					}
					
				});
				int suggestedTime = (int) (Math.abs(800 - Math.sqrt(Math.pow(album.getPhotos().size() + 10, 2))));
				if(suggestedTime <= 50)
					suggestedTime = 50;
				seek.setProgress(suggestedTime);
				value.setText(suggestedTime + "");
				
				alert.setView(seek_and_value);

				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								int time = seek.getProgress();
								if(time == 0)
									time = 1;
								Intent i = new Intent(getApplicationContext(),
										PreviewGifActivity.class);
								i.putExtra("index", albumIndex);
								i.putExtra("time", time);
								startActivity(i);
								finish();
							}
						});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						});

				alert.show();

			}
		});

		publish_to_facebook = (Button) this
				.findViewById(R.id.publish_to_facebook);
		publish_to_facebook.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ArrayList<Bitmap> photos = album.getPhotos();
				for (int i = 0; i < photos.size(); i++) {
					Request request = Request.newUploadPhotoRequest(
							Session.getActiveSession(), photos.get(i),
							new Request.Callback() {
								@Override
								public void onCompleted(Response res) {
									int duration = Toast.LENGTH_SHORT;
									CharSequence text = "Images uploaded to facebook.";
									Toast.makeText(context, text, duration)
											.show();
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

	public void onResume() {
		super.onResume();
		MainActivity.loadAlbums();
		Intent i = getIntent();
		albumIndex = i.getIntExtra("index", 0);
		album = MainActivity.albums.get(albumIndex);
		images_ll.removeAllViews();
		for (Bitmap photo : album.getPhotos()) {
			ImageView photo_iv = new ImageView(this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(5, 0, 5, 0);
			photo_iv.setLayoutParams(lp);
			photo_iv.setMinimumWidth(300);
			photo_iv.setMinimumHeight(300);
			photo_iv.setImageBitmap(photo);
			images_ll.addView(photo_iv);
		}
		title_tv.setText(album.getTitle());
	}

	public void onPause() {
		super.onPause();
		for (int i = 0; i < MainActivity.albums.size(); i++)
			if (MainActivity.albums.get(i).getTitle().equals(album.getTitle()))
				MainActivity.albums.set(i, album);
	}

}
