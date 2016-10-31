package com.humbleai.humblenotes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class SetListAdapter extends RecyclerView.Adapter<SetListAdapter.ViewHolder> {

    private final List<SetList> mDataset;
    private final Context mContext;
    private SetListSQLiteHelper db;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final CardView mCard;
        public final TextView mTextViewTitle;
        public final TextView mTextViewDescription;
        public final ImageView mImageViewSetIcon;
        public final ImageButton mImageButton;

        public ViewHolder(View v, int viewType) {
            super(v);
            mCard = (CardView) v.findViewById(R.id.card_view);

            mTextViewDescription = (TextView) v.findViewById(R.id.textViewDescription);
            mTextViewTitle = (TextView) v.findViewById(R.id.textViewTitle);
            mImageViewSetIcon = (ImageView) v.findViewById(R.id.imageViewSetIcon);
            mImageButton = (ImageButton) v.findViewById(R.id.imageButtonDeleteSet);

        }
    }

    public SetListAdapter(List<SetList> mDataset, Context mContext ) {
        this.mDataset = mDataset;
        this.mContext =mContext;

    }


    @Override
    public SetListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        View v ;


        switch (viewType) {
            case 1: // pick
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycle_pick_notebook, parent, false);

                break;
            default: //liste
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycle_item, parent, false);
        }


        return new ViewHolder(v, viewType);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
         final SetList setListItem = mDataset.get(position);
        SharedPreferences settings = mContext.getSharedPreferences(ScrollingActivity.PREFS_NAME, 0);

        if ( settings.getBoolean("action_settings_small_font_notebooks", false)) {
            holder.mTextViewTitle.setTextSize(2, 16f);
            holder.mTextViewDescription.setTextSize(2, 12f);
        } else {
            holder.mTextViewTitle.setTextSize(2, 18f);
            holder.mTextViewDescription.setTextSize(2, 14f);
        }

        holder.mTextViewTitle.setText(setListItem.getTitle());
        if (setListItem.getDescription().length() > 0) {
            holder.mTextViewDescription.setText(setListItem.getDescription());
        } else {
            holder.mTextViewDescription.setVisibility(View.GONE);
        }

        holder.mImageViewSetIcon.setImageResource(setListItem.getIcon());

        float itemAlfa = 1f;

        holder.mCard.setAlpha(0);
        holder.mCard.animate().setDuration(800);
        holder.mCard.animate().alpha(itemAlfa);
        holder.mCard.setAlpha(itemAlfa);
        switch (getItemViewType(position)) {
            case 1: // sonuc

                holder.mCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String intentString =null;
                        Intent intentIncoming = ((Activity) mContext).getIntent();
                        String action = intentIncoming.getAction();
                        String type = intentIncoming.getType();

                        if (Intent.ACTION_SEND.equals(action) && type != null) {
                            if ("text/plain".equals(type)) {
                                intentString=intentIncoming.getStringExtra(Intent.EXTRA_TEXT); // Handle text being sent
                            }
                        }
                        ((Activity) mContext).finish();

                        SetItemSQLiteHelper dbInsNote = new SetItemSQLiteHelper(mContext);
                        TypedArray colors_icons = mContext.getResources().obtainTypedArray(R.array.colors_icons);
                       // final Random random = new Random();
                      //  final int randColor = random.nextInt(colors_icons.length());
                        final int iconRes = R.color.color_card_bg;
                        colors_icons.recycle();
                        SetItem newItem = new SetItem(setListItem.getId(), iconRes, intentString, "");
                        Long inserted = dbInsNote.addSetItem(newItem);
                        // db.close();
                        if (inserted == -1) {
                            Snackbar.make(v, R.string.newListFail, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(mContext, ItemsActivity.class);
                            intent.putExtra("setID", String.valueOf(setListItem.getId()));
                            intent.putExtra("setTitle", String.valueOf(setListItem.getTitle()));
                            intent.putExtra("setIcon", String.valueOf(setListItem.getIcon()));
                            intent.putExtra("setDescription", setListItem.getDescription());
                            intent.putExtra("setPrime", "Title");
                            intent.putExtra("setSetType", "user");
                            mContext.startActivity(intent);
                        }




                    }
                });


                break;
            default: //liste



                holder.mCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent;
                        intent = new Intent(mContext, ItemsActivity.class);
                        intent.putExtra("setID", String.valueOf(setListItem.getId()));
                        intent.putExtra("setTitle", String.valueOf(setListItem.getTitle()));
                        intent.putExtra("setIcon", String.valueOf(setListItem.getIcon()));
                        intent.putExtra("setDescription", String.valueOf(setListItem.getDescription()));
                        intent.putExtra("setPrime", String.valueOf(setListItem.getPrime()));
                        intent.putExtra("setSetType", String.valueOf(setListItem.getSetType()));
                        mContext.startActivity(intent);

                    }
                });

                holder.mImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final View sv = v;
                        PopupMenu popup = new PopupMenu(mContext, v);
                        popup.getMenu().add(R.string.delete);
                        popup.getMenu().add(R.string.edit);

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                if (item.getTitle().toString().contentEquals(mContext.getString(R.string.delete))) {

                                    tools.deleteNotebook(mContext, setListItem);

                                    // db.close();
                                    mDataset.remove(setListItem);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, getItemCount());
                                }

                                if (item.getTitle().toString().contentEquals(mContext.getString(R.string.edit))) {

                                    LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                                    final View promptView = layoutInflater.inflate(R.layout.input_dialog_new_set, null, false);
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                                    alertDialogBuilder.setView(promptView);

                                    final EditText editTextNewTitle = (EditText) promptView.findViewById(R.id.editTextNewTitle);
                                    final EditText editTextNewDescription = (EditText) promptView.findViewById(R.id.editTextNewDescription);

                                    final ImageView imageViewSetIconPreview = (ImageView) promptView.findViewById(R.id.imageViewSetIconPreview);
                                    final TextView textViewtitlenewset = (TextView) promptView.findViewById(R.id.textViewtitlenewset);

                                    textViewtitlenewset.setText(R.string.edit);

                                    editTextNewTitle.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                                            String titleText = editTextNewTitle.getText().toString();
                                            int letterId = R.drawable.ik_0_0;
                                            if (titleText.length() > 0) {
                                                String initialLetter = titleText.substring(0, 1).toLowerCase();
                                                String alphabet = "abcdefghijklmnopqrstuvwxyz1234567890";

                                                if (alphabet.contains(initialLetter)) {
                                                    letterId = mContext.getResources().getIdentifier("ik_" + initialLetter + "_48", "drawable", mContext.getPackageName());
                                                }
                                            }

                                            imageViewSetIconPreview.setImageResource(letterId);

                                        }

                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                            // TODO Auto-generated method stub
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {

                                            // TODO Auto-generated method stub
                                        }
                                    });




                                    editTextNewTitle.setText(setListItem.getTitle());
                                    editTextNewDescription.setText(setListItem.getDescription());

                                    alertDialogBuilder.setCancelable(true)
                                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int id) {

                                                    if (TextUtils.isEmpty(editTextNewTitle.getText()))
                                                        editTextNewTitle.setText(R.string.untitled);
                                                    String titleText = editTextNewTitle.getText().toString();
                                                    String initialLetter = titleText.substring(0, 1).toLowerCase();
                                                    String alphabet = "abcdefghijklmnopqrstuvwxyz1234567890";
                                                    int letterId;
                                                    if (alphabet.contains(initialLetter)) {
                                                        letterId = mContext.getResources().getIdentifier("ik_" + initialLetter + "_48", "drawable", mContext.getPackageName());
                                                    } else {
                                                        letterId = R.drawable.ik_0_0;
                                                    }


                                                    setListItem.setIcon(letterId);
                                                    setListItem.setTitle(titleText);
                                                    setListItem.setDescription(editTextNewDescription.getText().toString());

                                                    db = new SetListSQLiteHelper(mContext);
                                                    int insertedId = db.updateSetList(setListItem);
                                                    //  db.close();
                                                    if (insertedId == -1) {
                                                        Snackbar.make(sv, R.string.newListFail, Snackbar.LENGTH_SHORT).show();
                                                    } else {
                                                        notifyItemChanged(position);
                                                    }


                                                }
                                            })
                                            .setNegativeButton(R.string.cancel,
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
                                                        }
                                                    });

                                    AlertDialog alert = alertDialogBuilder.create();
                                    alert.show();


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
    public int getItemCount() {
        return (null != mDataset ? mDataset.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {

        SetList setList = mDataset.get(position);
        return setList.getItemViewType();
    }
}