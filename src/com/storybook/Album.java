package com.storybook;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class Album{

	private String title;
	private ArrayList<Bitmap> photos;
	private boolean reminder;
	
	public Album(String t, ArrayList<Bitmap> phts){
		title = t;
		photos = phts;
		reminder = false;
	}
	
	public Album(String t, ArrayList<Bitmap> phts, boolean rmd){
		title = t;
		photos = phts;
		reminder = rmd;
	}
	
	public Album(ViewHolder holder){
		title = holder.title_tv.getText().toString();
		photos = holder.photos;
		reminder = holder.reminder;
	}
	
	public boolean practicallyEqual(Album other){
		return title.equals(other.getTitle()) && photos.size() == other.getPhotos().size() &&
				photos.get(0).getWidth() == other.getPhotos().get(0).getWidth() &&
				photos.get(0).getHeight() == other.getPhotos().get(0).getHeight() && 
				photos.get(0).getPixel(0, 0) == (other.getPhotos().get(0).getPixel(0, 0));
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<Bitmap> getPhotos() {
		return photos;
	}

	public void setPhotos(ArrayList<Bitmap> photos) {
		this.photos = photos;
	}
	
	public boolean isReminder(){
		return reminder;
	}
	
	public void setReminder(boolean reminder){
		this.reminder = reminder;
	}
	
	
}
