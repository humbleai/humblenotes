package com.humbleai.humblenotes;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;


import java.util.ArrayList;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<SetList> myDataset = new ArrayList<>();
    private SetListSQLiteHelper db;
    public static final String PREFS_NAME = "uc_humblenotes_shared_prefs_file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            // Get intent, action and MIME type
            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    yeniSet(intent.getStringExtra(Intent.EXTRA_TEXT)); // Handle text being sent
                }
            }
        }

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (!settings.getBoolean("is_first_time", false)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("is_first_time", true);
            editor.putBoolean("action_settings_min_two_notebooks", true);
            editor.putBoolean("action_settings_min_two_notes", true);
            editor.putBoolean("action_settings_small_font_notebooks", false);
            editor.putBoolean("action_settings_small_font_notes", true);
            editor.apply();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        arrangeColumns();

        dbIsleri();

    }

    private void dbIsleri() {
        db = new SetListSQLiteHelper(this);

        myDataset = db.getAllSetLists();

        mAdapter = new SetListAdapter(myDataset, this);
        mRecyclerView.setAdapter(mAdapter);

      //  db.close();
    }

    private void arrangeColumns() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        //eğer en az 2 notebook seçilmişse min 2 kolon değilse min 1 kolon

        int count;
        if (settings.getBoolean("action_settings_min_two_notebooks", false)) {
            count = 2;
        } else
        {
            count = 1;
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE  && !isTablet(this)) {
            // landscape ve telse min kolon * 2
            count++;
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && isTablet(this)) {
            // landscapese ve tabletse min kolon * 3
            count = count * 3;
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT  && isTablet(this)) {
            // portrait ve tabletse min kolon * 2
            count = count * 2;
        }


        final StaggeredGridLayoutManager mLayoutManagerStaggered = new StaggeredGridLayoutManager(count, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManagerStaggered);
    }

    private boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    private void yeniSet(final String intentString) {

        LayoutInflater layoutInflater = LayoutInflater.from(ScrollingActivity.this);

        final View promptView = layoutInflater.inflate(R.layout.input_dialog_new_set, null, false);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ScrollingActivity.this);
        alertDialogBuilder.setView(promptView);



        final EditText editTextNewTitle = (EditText) promptView.findViewById(R.id.editTextNewTitle);

        final ImageView imageViewSetIconPreview = (ImageView) promptView.findViewById(R.id.imageViewSetIconPreview);


        editTextNewTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                String titleText = editTextNewTitle.getText().toString();
                int letterId = R.drawable.ik_0_0;
                if (titleText.length() > 0) {
                    String initialLetter = titleText.substring(0, 1).toLowerCase();
                    String alphabet = "abcdefghijklmnopqrstuvwxyz1234567890";

                    if (alphabet.contains(initialLetter)) {
                        letterId = getResources().getIdentifier("ik_" + initialLetter + "_48", "drawable", getPackageName());
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

        final EditText editTextNewDescription = (EditText) promptView.findViewById(R.id.editTextNewDescription);



        alertDialogBuilder.setCancelable(true)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        if (TextUtils.isEmpty(editTextNewTitle.getText()))
                            editTextNewTitle.setText(R.string.untitled);
                        String titleText = editTextNewTitle.getText().toString();
                        String initialLetter = titleText.substring(0, 1).toLowerCase();
                        String alphabet = "abcdefghijklmnopqrstuvwxyz1234567890";
                        int letterId;
                        if (alphabet.contains(initialLetter)) {
                            letterId = getResources().getIdentifier("ik_" + initialLetter + "_48", "drawable", getPackageName());
                        } else {
                            letterId = R.drawable.ik_0_0;
                        }

                        SetList eklenecek = new SetList(letterId, titleText, editTextNewDescription.getText().toString());

                        Long insertedId = db.addSetList(eklenecek);
                       // db.close();
                        if (insertedId == -1) {
                            Snackbar.make(promptView, R.string.newListFail, Snackbar.LENGTH_SHORT).show();
                        } else {
                            eklenecek.setId(insertedId.intValue());
                            myDataset.add(eklenecek);
                            mAdapter.notifyItemInserted(myDataset.indexOf(eklenecek));
                            mRecyclerView.scrollToPosition(myDataset.indexOf(eklenecek));

                            Intent intent = new Intent(promptView.getContext(), ItemsActivity.class);
                            intent.putExtra("setID", String.valueOf(insertedId));
                            intent.putExtra("setTitle", String.valueOf(titleText));
                            intent.putExtra("setIcon", String.valueOf(letterId));
                            intent.putExtra("setDescription", editTextNewDescription.getText().toString());
                            intent.putExtra("setPrime", "Title");
                            intent.putExtra("setSetType", "user");
                            intent.putExtra("recievedIntent", intentString);
                            promptView.getContext().startActivity(intent);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scrolling, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_add_new_set:
                yeniSet(null);

                return true;
            case R.id.action_settings:
                Intent newintent = new Intent(this, SettingsActivity.class);
                newintent.putExtra("type", "sets");
                startActivityForResult(newintent, 777);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == 777) {
                if (data.getBooleanExtra("isUpdated", false)) arrangeColumns();
            }

        } /*else {
            Snackbar.make(mCoordLayout, R.string.newListFail, Snackbar.LENGTH_SHORT).show();
        }*/
    }



}
