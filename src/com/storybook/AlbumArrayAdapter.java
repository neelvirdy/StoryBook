package com.storybook;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AlbumArrayAdapter extends ArrayAdapter<Album> implements OnClickListener{
        private ArrayList<Album> items;
        private Context context;
        
        public AlbumArrayAdapter(Context context, int textViewResourceId,
                        ArrayList<Album> items) {
                super(context, textViewResourceId, items);
                this.context = context;
                this.items = items;
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
                Album item = items.get(position);
                ViewHolder holder;
                View view = convertView;
                if (view == null) {
                        LayoutInflater inflater = (LayoutInflater) context
                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.list_item, null);
                        holder = new ViewHolder();
                        holder.title_tv = (TextView) view.findViewById(R.id.title_tv);
                    holder.preview_iv = (ImageView) view.findViewById(R.id.preview_iv);
                    view.setTag(holder);  
                } else
                    holder = (ViewHolder) view.getTag();

                if(item != null){
                        holder.title_tv.setText(item.getTitle());
                        holder.photos = item.getPhotos();
                        if(item.getPhotos().size() > 0)
                                holder.preview_iv.setImageBitmap(item.getPhotos().get(0));
                        else
                                holder.preview_iv.setImageBitmap(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888));
                        holder.reminder = item.isReminder();
                }
                
                return view;
        }

        @Override
        public void onClick(View v) {
                // TODO Auto-generated method stub
                
        }
}
class ViewHolder{
        TextView title_tv;
        ImageView preview_iv;
        ArrayList<Bitmap> photos;
        boolean reminder;
}