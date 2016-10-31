package com.humbleai.humblenotes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;


public class SetItemAdapter extends RecyclerView.Adapter<SetItemAdapter.ViewHolder> {

    private final List<SetItem> mDataset;
    private final Context mContext;
    //private int currentColor;

    public static class ViewHolder extends RecyclerView.ViewHolder {


        public final CardView mCard;
        public final TextView mTextViewTitle;
        public final ImageView mImageViewImage;
        public final ImageButton mImageButton;

        public ViewHolder(View v, int viewType) {
            super(v);

            switch (viewType) {
                case 1: // sonuc

                default: //liste

                    mCard = (CardView) v.findViewById(R.id.card_view_view_set);
                    mTextViewTitle = (TextView) v.findViewById(R.id.textViewTitle_item);
                    mImageViewImage = (ImageView) v.findViewById(R.id.imageViewImage);
                    mImageButton = (ImageButton) v.findViewById(R.id.imageButtonDeleteItem);

            }


        }


    }


    public SetItemAdapter(List<SetItem> mDataset, Context mContext ) {
        this.mDataset = mDataset;
        this.mContext = mContext;
    }


    @Override
    public SetItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v ;


        switch (viewType) {
            case 1: // pick


            default: //liste
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycle_set_item, parent, false);
        }

        return new ViewHolder(v, viewType);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final SetItem setItem = mDataset.get(position);

        switch (getItemViewType(position)) {
            case 1: // sonuc

        break;
        default: //liste

            float itemAlfa = 1f;
            if (setItem.getExcluded() == 1) itemAlfa = 0.3f;

            holder.mCard.setAlpha(0);
            holder.mCard.animate().setDuration(800);
            holder.mCard.animate().alpha(itemAlfa);
            holder.mCard.setAlpha(itemAlfa);

            SharedPreferences settings = mContext.getSharedPreferences(ScrollingActivity.PREFS_NAME, 0);

            if ( settings.getBoolean("action_settings_small_font_notes", false)) {
                holder.mTextViewTitle.setTextSize(2, 14f);
            } else {
                holder.mTextViewTitle.setTextSize(2, 16f);
            }

            holder.mTextViewTitle.setText(setItem.getTitle());

            if (!setItem.getImage().equals("")) {
               // Log.e("getImage",setItem.getImage());
                Bitmap thumbnail;
                try {
                    File filePath = mContext.getFileStreamPath(setItem.getImage());
                    //FileInputStream fi = new FileInputStream(filePath);
                    thumbnail = tools.decodeSampledBitmap(filePath.getPath(), 200, 200);
                    RoundedBitmapDrawable dr =
                            RoundedBitmapDrawableFactory.create(null, thumbnail);
                    dr.setCornerRadius(1);

                    holder.mImageViewImage.setImageDrawable(dr);
                    holder.mImageViewImage.setVisibility(View.VISIBLE);
                } catch (Exception ex) {
                  //  Log.e("exeption", "exxx");
                    holder.mImageViewImage.setImageBitmap(null);
                    holder.mImageViewImage.setVisibility(View.GONE);
                }


            } else {
                holder.mImageViewImage.setImageDrawable(null);
                holder.mImageViewImage.setVisibility(View.GONE);
            }


            if(ContextCompat.getColor(mContext, setItem.getIcon()) != 0 ){
                holder.mCard.setCardBackgroundColor(ContextCompat.getColor(mContext, setItem.getIcon()));
            } else {
                holder.mCard.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.color_card_bg));
            }


            holder.mCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    editItem(setItem, position);
                }
            });

            holder.mTextViewTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    editItem(setItem, position);
                }
            });

            holder.mImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View sv = v;
                    PopupMenu popup = new PopupMenu(mContext, v);
                    if (setItem.getSetType().equals("user"))
                        popup.getMenu().add(R.string.delete);
                    if (setItem.getSetType().equals("user")) popup.getMenu().add(R.string.edit);
                    if (setItem.getSetType().equals("user")) popup.getMenu().add(R.string.share);
                    if (setItem.getExcluded() == 1) popup.getMenu().add(R.string.include);
                    if (setItem.getExcluded() == 0) popup.getMenu().add(R.string.exclude);
                    final SetItemSQLiteHelper db = new SetItemSQLiteHelper(mContext);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {


                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            if (item.getTitle().toString().contentEquals(mContext.getString(R.string.delete))) {

                                tools.deleteImage(mContext, setItem);

                                db.deleteSetItem(setItem);
                                // db.close();
                                mDataset.remove(setItem);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, getItemCount());

                            }

                            if (item.getTitle().toString().contentEquals(mContext.getString(R.string.exclude)) || item.getTitle().toString().contentEquals(mContext.getString(R.string.include))) {
                                if (setItem.getExcluded() == 1) {
                                    setItem.setExcluded(0);
                                } else {
                                    setItem.setExcluded(1);
                                }

                                if (db.updateSetItem(setItem) > 0) {

                                    mDataset.set(position, setItem);
                                    notifyItemChanged(position, setItem);

                                } else {
                                    Snackbar.make(sv, R.string.newListFail, Snackbar.LENGTH_SHORT).show();
                                    if (setItem.getExcluded() == 1) {
                                        setItem.setExcluded(0);
                                    } else {
                                        setItem.setExcluded(1);
                                    }
                                }
                                // db.close();

                            }

                            if (item.getTitle().toString().contentEquals(mContext.getString(R.string.share))) {

                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, setItem.getTitle());
                                sendIntent.setType("text/plain");
                                mContext.startActivity(Intent.createChooser(sendIntent, mContext.getResources().getText(R.string.share)));

                            }

                            if (item.getTitle().toString().contentEquals(mContext.getString(R.string.edit))) {

                                editItem(setItem, position);

                            }

                            return false;
                        }
                    });
                    popup.show();
                }
            });


         }




    }

    @Override
    public int getItemViewType(int position) {

        SetItem setItem = mDataset.get(position);
        return setItem.getItemViewType();
    }


    private void editItem(SetItem item, int pos) {

        Intent intent = new Intent(mContext, SingleItemActivity.class);
        intent.putExtra("setItem", item);
        intent.putExtra("position", pos);

        ((Activity) mContext).startActivityForResult(intent, 555);

    }


    @Override
    public int getItemCount() {

        return (null != mDataset ? mDataset.size() : 0);
    }



}