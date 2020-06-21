package com.example.channelinfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewModel> {
    private Context mContext;
    private Cursor mCursor;

    RecyclerViewAdapter(Context context, Cursor cursor) {
        Log.d("TESTINGG", "RecyclerViewAdapter Constructor: Called"+Thread.currentThread().getName());
        mContext = context;
        mCursor=cursor;
    }

    static class ViewModel extends RecyclerView.ViewHolder{
        ImageView image;
        TextView channel,category,price,language;
        ViewModel(@NonNull View itemView) {
            super(itemView);
            Log.d("TESTINGG", "View Model Constructor: Called"+Thread.currentThread().getName());
            image=itemView.findViewById(R.id.imageView_item);
            channel=itemView.findViewById(R.id.tv_item_channel);
            category=itemView.findViewById(R.id.tv_item_category);
            price=itemView.findViewById(R.id.tv_item_price);
            language=itemView.findViewById(R.id.language);

        }
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("TESTINGG","OnCreateViewHolder Called"+Thread.currentThread().getName());
        View view= LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item,parent,false);
        return new ViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewModel holder, int position) {
        Log.d("TESTINGG","OnBindViewHolder Called"+holder.getAdapterPosition()+Thread.currentThread().getName());
        if(!mCursor.moveToPosition(position)) {
            return;
        }
        long id=mCursor.getLong(mCursor.getColumnIndex(ItemContract.ChannelEntry._ID));
        String channel=mCursor.getString(mCursor.getColumnIndex(ItemContract.ChannelEntry.COLUMN_CHANNEL_NAME));
        String category=mCursor.getString(mCursor.getColumnIndex(ItemContract.ChannelEntry.COLUMN_CATEGORY));
        String imgUrl=mCursor.getString(mCursor.getColumnIndex(ItemContract.ChannelEntry.COLUMN_IMG_URL));
        String price=mCursor.getString(mCursor.getColumnIndex(ItemContract.ChannelEntry.COLUMN_PRICE));
        String language=mCursor.getString(mCursor.getColumnIndex(ItemContract.ChannelEntry.COLUMN_LANGUAGE));
        String hd=mCursor.getString(mCursor.getColumnIndex(ItemContract.ChannelEntry.COLUMN_HD));
        holder.itemView.setTag(id);

        Picasso.with(mContext).load(MainActivity.URL+""+imgUrl).fit().centerInside().into(holder.image);
        holder.channel.setText(channel);
        holder.category.setText(category);
        holder.price.setText("Rs. "+price);
        holder.language.setText(language);
    }

    @Override
    public int getItemCount() {
        Log.d("TESTINGG","GetItemCount Called"+Thread.currentThread().getName());
        return mCursor.getCount();
    }

//    @Override
//    public Filter getFilter() {
//        return filter;
//    }
//
//    private Filter filter=new Filter() {
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            Log.d("Example","Perform Filtering Invoked");
//            ArrayList<ItemModel> filteredList=new ArrayList<>();
//            if(constraint == null || constraint.length() == 0){
//                filteredList.addAll(mFullArrayList);
//                Log.d("Example",""+mFullArrayList.size());
//            }else {
//                String pattern=constraint.toString().toLowerCase().trim();
//                for(ItemModel item:mFullArrayList){
//                    if(item.getChannel().toLowerCase().contains(pattern)){
//                        filteredList.add(item);
//                    }
//                }
//            }
//
//            FilterResults filterResults=new FilterResults();
//            filterResults.values=filteredList;
//            Log.d("Example",""+mArrayList.size());
//            return filterResults;
//
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//            Log.d("Example","Publish Results Invoked "+results.values);
//            mArrayList.clear();
//            mArrayList.addAll((ArrayList)results.values);
//            notifyDataSetChanged();
//        }
//    };

    public void swapCursor(Cursor newCursor) {
        Log.d("TESTINGG", "swapCursor: Called"+Thread.currentThread().getName());
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
}
