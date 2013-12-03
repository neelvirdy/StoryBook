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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
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
	OnLongClickListener photo_long_click_listener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Context context = getApplicationContext();

		this.setContentView(R.layout.activity_view_album);

		images_hsv = (HorizontalScrollView) this.findViewById(R.id.images_hsv);

		photo_long_click_listener = new OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				// TODO Auto-generated method stub
				LayoutInflater li = LayoutInflater.from(ViewAlbumActivity.this);
				View promptsView = li.inflate(
						R.layout.prompts_long_click_photo, null);

				final int position = (Integer) view.getTag();
				Log.d("pos", position + "");

				AlertDialog.Builder photoLongClickDialogBuilder = new AlertDialog.Builder(
						ViewAlbumActivity.this);

				// set prompts.xml to alertdialog builder
				photoLongClickDialogBuilder.setView(promptsView);

				Button prompt_long_click_view = (Button) promptsView
						.findViewById(R.id.prompt_long_click_photo_view);
				Log.d("null?", "" + (prompt_long_click_view == null));

				photoLongClickDialogBuilder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

				final AlertDialog photoLongClickDialog = photoLongClickDialogBuilder
						.create();

				prompt_long_click_view
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								photoLongClickDialog.dismiss();
							}

						});

				Button prompt_long_click_retake = (Button) promptsView
						.findViewById(R.id.prompt_long_click_photo_retake);
				prompt_long_click_retake
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								
								AlertDialog.Builder areYouSure = new AlertDialog.Builder(ViewAlbumActivity.this);
								areYouSure.setTitle("Are you sure you want to retake this photo?");
								areYouSure.setPositiveButton("OK", new DialogInterface.OnClickListener(){

									@Override
									public void onClick(DialogInterface dialog, int id) {
										// TODO Auto-generated method stub
										photoLongClickDialog.dismiss();
										File dir = new File(Environment.getExternalStorageDirectory(),
												"StoryBook");
										dir.mkdirs();
										File albumFolder = new File(dir, album.getTitle());
										albumFolder.mkdirs();
										File path = new File(albumFolder, position
												+ ".jpg");
										Uri uriSavedImage = Uri.fromFile(path);
										Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
										i.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
										startActivityForResult(i, ADD_IMAGE_REQUEST);
										MainActivity.loadAlbums();
										deletePhoto(position);
										refresh();
									}
									
								});
								
								areYouSure.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});
								
								areYouSure.show();
							}
						});

				Button prompt_long_click_delete = (Button) promptsView
						.findViewById(R.id.prompt_long_click_photo_delete);
				prompt_long_click_delete
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								
								AlertDialog.Builder areYouSure = new AlertDialog.Builder(ViewAlbumActivity.this);
								areYouSure.setTitle("Are you sure you want to delete this photo?");
								areYouSure.setPositiveButton("OK", new DialogInterface.OnClickListener(){

									@Override
									public void onClick(DialogInterface dialog, int id) {
										// TODO Auto-generated method stub
										photoLongClickDialog.dismiss();
										deletePhoto(position);
										fixNamesAfter(position);
										MainActivity.loadAlbums();
										refresh();
									}
									
								});
								
								areYouSure.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});
								
								areYouSure.show();
							}

						});
				photoLongClickDialog.show();
				return false;

			}
		};

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

				final AlertDialog.Builder alert = new AlertDialog.Builder(
						ViewAlbumActivity.this);
				alert.setTitle("Time Between Frames (ms)");

				LayoutInflater inflater = LayoutInflater.from(alert
						.getContext());
				final View seek_and_value = inflater.inflate(
						R.layout.preview_gif_dialog, null);

				final SeekBar seek = (SeekBar) seek_and_value
						.findViewById(R.id.preview_gif_dialog_sb);
				final TextView value = (TextView) seek_and_value
						.findViewById(R.id.preview_gif_dialog_tv);
				seek.setMax(2000);

				seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						// TODO Auto-generated method stub
						if (progress > 50)
							value.setText(progress + "");
						else
							value.setText(50 + "");
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
				int suggestedTime = (int) (Math.abs(800 - Math.sqrt(Math.pow(
						album.getPhotos().size() + 10, 3))));
				if (suggestedTime <= 50)
					suggestedTime = 50;
				seek.setProgress(suggestedTime);
				value.setText(suggestedTime + "");

				alert.setView(seek_and_value);

				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								int time = seek.getProgress();
								if (time <= 50)
									time = 50;
								Intent i = new Intent(getApplicationContext(),
										PreviewGifActivity.class);
								i.putExtra("index", albumIndex);
								i.putExtra("time", time);
								startActivity(i);
							}
						});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

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
		refresh();
	}
	
	public void refresh(){
		album = MainActivity.albums.get(albumIndex);
		images_ll.removeAllViews();
		Bitmap photo;
		for (int pos = 0; pos < album.getPhotos().size(); pos++) {
			photo = album.getPhotos().get(pos);
			ImageView photo_iv = new ImageView(this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(5, 0, 5, 0);
			photo_iv.setLayoutParams(lp);
			photo_iv.setMinimumWidth(300);
			photo_iv.setMinimumHeight(300);
			photo_iv.setImageBitmap(photo);
			photo_iv.setTag(pos);
			photo_iv.setOnLongClickListener(photo_long_click_listener);
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

	public void fixNamesAfter(int index){
		File dir = new File(Environment.getExternalStorageDirectory(),
				"StoryBook");
		dir.mkdirs();
		File[] folders = dir.listFiles();
		if (folders == null)
			folders = new File[0];
		for (File folder : folders)
			if (folder.getName().equals(album.getTitle())) {
				File[] photos = folder.listFiles();
				if (photos == null)
					photos = new File[0];
				for (File photo : photos){
					Log.d("old photo name", photo.getName());
					if(photo.getName().split("\\.").length > 0 && Integer.valueOf(photo.getName().split("\\.")[0]) > index)
						photo.renameTo(new File(folder, (Integer.valueOf(photo.getName().split("\\.")[0])-1)+".jpg"));
					Log.d("new photo name", photo.getName());
				}
			}
	}
	
	public void deletePhoto(int index) {
		File dir = new File(Environment.getExternalStorageDirectory(),
				"StoryBook");
		dir.mkdirs();
		File[] folders = dir.listFiles();
		if (folders == null)
			folders = new File[0];
		for (File folder : folders)
			if (folder.getName().equals(album.getTitle())) {
				File[] photos = folder.listFiles();
				if (photos == null)
					photos = new File[0];
				for (File photo : photos)
					if (photo.getName().equals(index + ".jpg"))
						photo.delete();
			}
	}

}
