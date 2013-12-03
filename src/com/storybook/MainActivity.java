package com.storybook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;

import com.facebook.Session;

public class MainActivity extends Activity {

	public final static int FIRST_PHOTO_REQUEST = 42;

	public static ArrayList<Album> albums;
	AlbumArrayAdapter adapter;
	GridView gridView;
	Button create_album;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		albums = new ArrayList<Album>();
		loadAlbums();
		adapter = new AlbumArrayAdapter(this, R.layout.list_item, albums);
		create_album = (Button) findViewById(R.id.create_album);
		create_album.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);

				builder.setTitle("Input Album Title");
				final EditText input = new EditText(MainActivity.this);
				Calendar rightNow = Calendar.getInstance();
				StringBuilder timestamp = new StringBuilder();
				timestamp.append(rightNow.getDisplayName(Calendar.DAY_OF_WEEK,
						Calendar.SHORT, Locale.US));
				timestamp.append(", "
						+ rightNow.getDisplayName(Calendar.MONTH,
								Calendar.SHORT, Locale.US));
				timestamp.append(" " + rightNow.get(Calendar.DATE));
				timestamp.append(" " + rightNow.get(Calendar.YEAR));
				input.setText(timestamp.toString());
				builder.setView(input);

				builder.setPositiveButton("Take a Picture!",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String title = input.getText().toString();
								Album a = new Album(title,
										new ArrayList<Bitmap>());

								File dir = new File(Environment
										.getExternalStorageDirectory(),
										"StoryBook");
								dir.mkdirs();
								File albumFolder = new File(dir, a.getTitle());
								albumFolder.mkdirs();
								File path = new File(albumFolder, a.getPhotos()
										.size() + ".jpg");
								Uri uriSavedImage = Uri.fromFile(path);
								Intent i = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								i.putExtra(MediaStore.EXTRA_OUTPUT,
										uriSavedImage);
								startActivityForResult(i, FIRST_PHOTO_REQUEST);

								albums.add(a);
								// add to mongodb
								dialog.cancel();
							}
						});
				builder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
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
				Intent i = new Intent(getApplicationContext(),
						ViewAlbumActivity.class);
				i.putExtra("index", position);
				startActivity(i);
			}
		});

		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				LayoutInflater li = LayoutInflater.from(MainActivity.this);
				View promptsView = li.inflate(
						R.layout.prompts_long_click_album, null);

				final ViewHolder holder = (ViewHolder) view.getTag();
				final Album album = new Album(holder);
				int toChange = 0;
				for (int i = 0; i < albums.size(); i++)
					if (albums.get(i).getTitle().equals(album.getTitle()))
						toChange = i;

				final int finalToChange = toChange;
				Log.d("index", "" + finalToChange);

				AlertDialog.Builder albumLongClickDialogBuilder = new AlertDialog.Builder(
						MainActivity.this);

				// set prompts.xml to alertdialog builder
				albumLongClickDialogBuilder.setView(promptsView);

				Button prompt_long_click_view = (Button) promptsView
						.findViewById(R.id.prompt_long_click_album_view);
				Log.d("null?", "" + (prompt_long_click_view == null));

				albumLongClickDialogBuilder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

				final AlertDialog albumLongClickDialog = albumLongClickDialogBuilder
						.create();

				prompt_long_click_view
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								albumLongClickDialog.dismiss();
								Intent i = new Intent(getApplicationContext(),
										ViewAlbumActivity.class);
								i.putExtra("index", position);
								startActivity(i);
							}

						});

				Button prompt_long_click_edit = (Button) promptsView
						.findViewById(R.id.prompt_long_click_album_edit);
				prompt_long_click_edit
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								albumLongClickDialog.dismiss();

								AlertDialog.Builder builder = new AlertDialog.Builder(
										MainActivity.this);

								LayoutInflater li = LayoutInflater
										.from(MainActivity.this);
								View editView = li.inflate(
										R.layout.form_edit_album, null);

								builder.setTitle("Edit Album");

								final EditText input = (EditText) editView
										.findViewById(R.id.edit_album_title_et);
								final CheckBox remind = (CheckBox) editView
										.findViewById(R.id.edit_album_reminder);
								input.setText(album.getTitle());
								remind.setChecked(album.isReminder());
								builder.setView(editView);

								builder.setPositiveButton("Save Changes",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												String title = input.getText()
														.toString();
												boolean reminder = remind
														.isChecked();
												String oldTitle = albums.get(finalToChange).getTitle();
												albums.set(
														finalToChange,
														new Album(title, album
																.getPhotos(),
																reminder));
												Log.d("Reminder",
														""
																+ albums.get(
																		finalToChange)
																		.isReminder());
												renameFiles(oldTitle, title);
												adapter.notifyDataSetChanged();
												dialog.cancel();
											}
										});
								builder.setNegativeButton("Cancel",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												dialog.cancel();
											}
										});

								AlertDialog edit = builder.create();
								edit.show();

							}

						});

				Button prompt_long_click_delete = (Button) promptsView
						.findViewById(R.id.prompt_long_click_album_delete);
				prompt_long_click_delete
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								albumLongClickDialog.dismiss();
								Album toRemove = albums.get(finalToChange);
								deleteAlbum(toRemove);
								Log.d("removed?",
										"" + albums.remove(finalToChange));
								adapter.notifyDataSetChanged();
							}

						});
				albumLongClickDialog.show();
				return false;

			}
		});
	}

	public void onResume() {
		super.onResume();
		loadAlbums();
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		return;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			break;
		case R.id.action_logout:
			if (Session.getActiveSession() != null)
				Session.getActiveSession().closeAndClearTokenInformation();
			Intent i = new Intent(this, LoginScreenActivity.class);
			startActivity(i);
			break;
		}
		return true;
	}

	public void deleteAlbum(Album album) {
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
					photo.delete();
				folder.delete();
			}
	}
	
	public void renameFiles(String oldTitle, String newTitle){
		File dir = new File(Environment.getExternalStorageDirectory(),
				"StoryBook");
		dir.mkdirs();
		File[] folders = dir.listFiles();
		if (folders == null)
			folders = new File[0];
		for (File folder : folders)
			if (folder.getName().equals(oldTitle)) {
				File newFolder = new File(dir, newTitle);
				File[] photos = folder.listFiles();
				if (photos == null)
					photos = new File[0];
				for (File photo : photos)
					photo.renameTo(new File(newFolder, photo.getName()));
				folder.renameTo(newFolder);
			}
	}

	public static void loadAlbums() {
		albums.clear();
		File dir = new File(Environment.getExternalStorageDirectory(), "StoryBook");
		dir.mkdirs();
		File[] folders = dir.listFiles();
		if (folders == null)
			folders = new File[0];
		Bitmap bmp;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		for (File folder : folders) {
			Album newAlbum = new Album(folder.getName(),
					new ArrayList<Bitmap>());
			File[] photos = folder.listFiles();
			if (photos == null)
				photos = new File[0];
			for (File photo : photos) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				bmp = BitmapFactory.decodeFile(photo.getPath(), options);

				options.inSampleSize = calculateInSampleSize(options, 300, 300);
				options.inJustDecodeBounds = false;
				bmp = BitmapFactory.decodeFile(photo.getPath(), options);
				Log.d("loaded photo dimensions",
						bmp.getWidth() + " " + bmp.getHeight());

				bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
				newAlbum.addPhoto(bmp);
			}
			albums.add(newAlbum);
		}
		Log.d("num albums", albums.size() + "");
		for (Album album : albums)
			Log.d("num photos in " + album.getTitle(), album.getPhotos().size()
					+ "");
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
}
