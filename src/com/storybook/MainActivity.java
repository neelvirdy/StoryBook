package com.storybook;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Session;

public class MainActivity extends Activity {

	public final static int FIRST_PHOTO_REQUEST = 42;

	public static ArrayList<Album> albums = new ArrayList<Album>();
	public static Bitmap insertMarker = Bitmap.createBitmap(1, 1,
			Bitmap.Config.ARGB_8888);
	AlbumArrayAdapter adapter;
	GridView gridView;
	Button create_album;
	
	private String userKey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		userKey = savedInstanceState.getString("userKey");
		setContentView(R.layout.activity_main);
		
		/*HPStoryBookMongoLib sbmongo = new HPStoryBookMongoLib();
		sbmongo.initConnection("168.62.177.219", 10000);
		sbmongo.initDatabase("PadTesting", "sidd", "squid05");
		sbmongo.initCollection("testcollection");
		sbmongo.createNewUser("siddthesquid", "sidd singal");*/
		if(albums == null)
			albums = new ArrayList<Album>();
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
				builder.setView(input);

				builder.setPositiveButton("Take a Picture!",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String title = input.getText().toString();
								Album a = new Album(title,
										new ArrayList<Bitmap>());
								ArrayList<Bitmap> newPhotos = a.getPhotos();
								newPhotos.add(insertMarker);
								a.setPhotos(newPhotos);
								Intent i = new Intent(
										android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
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
				LayoutInflater li = LayoutInflater.from(MainActivity.this);
				View promptsView = li.inflate(
						R.layout.prompts_long_click_album, null);

				final ViewHolder holder = (ViewHolder) view.getTag();
				final Album album = new Album(holder);
				final String title = holder.title_tv.getText().toString();
				final ArrayList<Bitmap> photos;
				if (holder.photos != null)
					photos = holder.photos;
				else
					photos = new ArrayList<Bitmap>();
				
				int toChange = 0;
				for (int i = 0; i < albums.size(); i++)
					if (albums.get(i).getTitle()
							.equals(album.getTitle()))
						toChange = i;
				
				final int finalToChange = toChange;
				Log.d("index", ""+finalToChange);
				
				AlertDialog.Builder albumLongClickDialogBuilder = new AlertDialog.Builder(
						MainActivity.this);

				// set prompts.xml to alertdialog builder
				albumLongClickDialogBuilder.setView(promptsView);

				LinearLayout prompts_long_click_ll = (LinearLayout) promptsView
						.findViewById(R.id.prompts_long_click_ll);
				Button prompt_long_click_view = (Button) promptsView
						.findViewById(R.id.prompt_long_click_view);
				Log.d("null?", "" + (prompt_long_click_view == null));
				
				albumLongClickDialogBuilder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				
				final AlertDialog albumLongClickDialog = albumLongClickDialogBuilder.create();
				
				prompt_long_click_view
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								albumLongClickDialog.dismiss();
								Intent i = new Intent(getApplicationContext(),
										ViewAlbumActivity.class);
								i.putExtra("title", title);
								i.putParcelableArrayListExtra("photos", photos);
								startActivity(i);
							}

						});

				Button prompt_long_click_edit = (Button) promptsView
						.findViewById(R.id.prompt_long_click_edit);
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
								final TextView promptTitle = (TextView) editView
										.findViewById(R.id.edit_album_title_tv);
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
												String title = input.getText().toString();
												boolean reminder = remind.isChecked();
												albums.set(finalToChange, new Album(
																				title,
																				album.getPhotos(),
																				reminder));
												Log.d("Reminder", ""+albums.get(finalToChange).isReminder());
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
						.findViewById(R.id.prompt_long_click_delete);
				prompt_long_click_delete
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								albumLongClickDialog.dismiss();
								Log.d("removed?", "" + albums.remove(finalToChange));
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
		adapter.notifyDataSetChanged();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == FIRST_PHOTO_REQUEST) {
				Album album = null;
				for (Album a : albums)
					if (a.getPhotos().size() == 1
							&& a.getPhotos().get(0).equals(insertMarker))
						album = a;

				if (album != null) {
					Bitmap photo = (Bitmap) data.getExtras().get("data");
					Log.d("Photo Dimensions: ", photo.getWidth() + "x" + photo.getHeight());
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			break;
		case R.id.action_logout:
			Session.getActiveSession().closeAndClearTokenInformation();
			Intent i = new Intent(this, LoginScreenActivity.class);
			startActivity(i);
			break;
		}
		return true;
	}

}
