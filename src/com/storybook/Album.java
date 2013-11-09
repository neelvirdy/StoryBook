package com.storybook;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class Album{

	private String title;
	private ArrayList<Bitmap> photos;
	
	public Album(String t, ArrayList<Bitmap> phts){
		title = t;
		photos = phts;
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
	
	
}
