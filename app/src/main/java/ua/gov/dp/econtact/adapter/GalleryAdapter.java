package ua.gov.dp.econtact.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.interfaces.ListListener;
import ua.gov.dp.econtact.model.TicketFiles;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private List<TicketFiles> mTicketsList;
    private Context mContext;
    private ListListener mListListener;
    private Picasso mPicasso;

    public GalleryAdapter(final List<TicketFiles> data, final Context context, final ListListener listListener) {
        final int cacheSize = 51200;
        mTicketsList = data;
        mContext = context;
        mListListener = listListener;
        mPicasso = new Picasso.Builder(context).memoryCache(new LruCache(cacheSize)).build();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_gallery_photo, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bindData(position);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mListListener.onListItemClick(position);
            }
        });
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                return mListListener.onListItemLongClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTicketsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ViewHolder(final View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.one_photo);
        }

        void bindData(final int position) {
            mPicasso.load(Const.URL_IMAGE + mTicketsList.get(position).getFilename())
                    .placeholder(R.drawable.placeholder)
                    .resizeDimen(R.dimen.image_preview_size, R.dimen.image_preview_size)
                    .config(Bitmap.Config.RGB_565).into(imageView);
        }
    }
}
