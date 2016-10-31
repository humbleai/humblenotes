package com.humbleai.humblenotes;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class ItemsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private final List<SetItem> myDataset = new ArrayList<>();
    private SetItemSQLiteHelper db;
    private int setId, setIcon;
    private String setDescription, setTitle, setPrime, setSetType;
    private int lastItemId = 0;
    private int itemCount = -1;
    private StaggeredGridLayoutManager mLayoutManagerStaggered;
    private CoordinatorLayout mCoordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        mCoordLayout = (CoordinatorLayout) findViewById(R.id.coord);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewItemList);
        mRecyclerView.setHasFixedSize(true);

        arrangeColumns();

        mAdapter = new SetItemAdapter(myDataset, this);
        mRecyclerView.setAdapter(mAdapter);

        db = new SetItemSQLiteHelper(this);

        onNewIntent(getIntent());


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean loading = true;
            int pastVisiblesItems, visibleItemCount, totalItemCount;
            int[] visItems;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManagerStaggered.getChildCount();
                    totalItemCount = mLayoutManagerStaggered.getItemCount();

                    mLayoutManagerStaggered.findLastVisibleItemPositions(visItems);
                    if(visItems != null && visItems.length > 0) {
                        pastVisiblesItems = visItems[0];
                    }

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {

                            loading = false;

                            dbisleri();

                            loading = true;

                        }
                    }
                }
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onNewIntent(Intent intent){


        Bundle extras = intent.getExtras();

        setTitle= extras.getString("setTitle");
        setId = Integer.parseInt(extras.getString("setID"));
        setIcon = Integer.parseInt(extras.getString("setIcon"));
        setPrime= extras.getString("setPrime");
        setSetType= extras.getString("setSetType");
        setDescription = extras.getString("setDescription");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //toolbar.setLogo(setIcon);
       // toolbar.getLogo().setAlpha(60);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAppCaption));
        toolbar.setSubtitleTextColor(ContextCompat.getColor(this, R.color.colorDescription));
        toolbar.setTitle(setTitle);
        toolbar.setSubtitle(setDescription);

        checkCounts();

        dbisleri();

        setSupportActionBar(toolbar);

        // if (savedInstanceState == null) {
        if ( intent.getStringExtra("recievedIntent") != null) {
            new insertIntentText(ItemsActivity.this).execute(intent.getStringExtra("recievedIntent"));
        }
        // }

    }

    private void arrangeColumns() {
        SharedPreferences settings = getSharedPreferences(ScrollingActivity.PREFS_NAME, 0);

        //eğer en az 2 notebook seçilmişse min 2 kolon değilse min 1 kolon

        int count;
        if (settings.getBoolean("action_settings_min_two_notes", false)) {
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

        mLayoutManagerStaggered = new StaggeredGridLayoutManager(count, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManagerStaggered);
    }

    private boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
            case R.id.action_add_new_item:

                Intent newintent = new Intent(this, SingleItemActivity.class);
                SetItem sendSetItem = new SetItem(setId, R.color.color_card_bg, "", "");
                newintent.putExtra("setItem", sendSetItem);
                newintent.putExtra("position", -1);
                startActivityForResult(newintent, 555);

                return true;
            case R.id.action_share:
                String textToShare = "";
                for (int i = 0; i < myDataset.size(); i++) {
                    textToShare += myDataset.get(i).getTitle() + "\n";
                }

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share)));

                return true;

            case R.id.action_import_file:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");
                startActivityForResult(intent, 42);
                return true;
            case R.id.action_backup:

                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    // external storage writablec mounted
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
                    String strDate = sdf.format(c.getTime());
                    String filename = setTitle  + " " + getString(R.string.app_name) + " " +  getString(R.string.backupfilename)  + " " +  strDate + ".txt";

                    File root = Environment.getExternalStorageDirectory();

                    File file = new File(root, filename);

                    try {
                        FileWriter filewriter = new FileWriter(file);
                        BufferedWriter out = new BufferedWriter(filewriter);


                        for (int i = 0; i < myDataset.size(); i++) {
                            out.write(myDataset.get(i).getTitle() + "\n");
                        }

                        out.close();
                        Snackbar.make(mRecyclerView, R.string.backupok, Snackbar.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Snackbar.make(mRecyclerView, e.toString(), Snackbar.LENGTH_SHORT).show();
                    }


                } else {
                    Snackbar.make(mRecyclerView, R.string.externalerror, Snackbar.LENGTH_SHORT).show();
                }

                return true;
            case R.id.action_settings:
                Intent settingsintent = new Intent(this, SettingsActivity.class);
                settingsintent.putExtra("type", "notes");
                startActivityForResult(settingsintent, 777);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkCounts(){
        itemCount = db.getCount(setId).intValue();
       // db.close();
    }


    private void dbisleri() {
       if (itemCount <= myDataset.size()) return;

        myDataset.addAll(db.getAllSetItems(setId, lastItemId, false));

        mAdapter.notifyDataSetChanged();
        lastItemId = myDataset.get(myDataset.size() - 1).getId();
        checkCounts();
       // db.close();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == 42 && data != null) {
                // Get the Uri of the selected file
                InputStream inputStream;
                Uri uri = data.getData();
                try {
                    inputStream = getContentResolver().openInputStream(uri);
                } catch (FileNotFoundException e) {
                    Snackbar.make(mCoordLayout, R.string.newListFail, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (inputStream != null) {
                    Scanner s = new Scanner(inputStream).useDelimiter("\\A");

                    if (s.hasNext()) {
                        new insertIntentText(ItemsActivity.this).execute(s.next());
                    }
                } else {
                    Snackbar.make(mCoordLayout, R.string.newListFail, Snackbar.LENGTH_SHORT).show();
                }

            }


            if (requestCode == 555) {
                int pos = data.getIntExtra("position", -2);
                SetItem setItem = (SetItem) data.getSerializableExtra("setItem");

                if (pos == -1) {
                    myDataset.add(setItem);
                    mAdapter.notifyItemInserted(myDataset.indexOf(setItem));
                    mRecyclerView.scrollToPosition(myDataset.indexOf(setItem));

                } else if (pos == -2){
                    // do nothing
                } else {
                    myDataset.set(pos, setItem);
                    mAdapter.notifyItemChanged(pos, setItem);
                }


            }

            if (requestCode == 777) {
                if (data.getBooleanExtra("isUpdated", false)) arrangeColumns();
            }

        } /*else {
            Snackbar.make(mCoordLayout, R.string.newListFail, Snackbar.LENGTH_SHORT).show();
        }*/
    }


    private class insertIntentText extends AsyncTask<Object, Integer, Integer> {

        private final Activity activity;
        private final NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        private Notification n;

        public insertIntentText(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Integer doInBackground(Object... params) {

            String incoming = (String) params[0];
            String[] lines = incoming.split(System.getProperty("line.separator"));




            if (lines.length > 0) {
                int c=0;
                for (String line:lines) {
                    if (line.trim().length() > 0) {
                        SetItem newItem = new SetItem(setId, R.color.color_card_bg, line.trim(), "");
                        db.addSetItem(newItem);
                        c++;
                        publishProgress(c, lines.length);
                    }
                   // db.close();
                }
                return 1;
            }
            return 0;

        }

        @Override
        protected void onPreExecute() {


            n  = new Notification.Builder(getApplicationContext())
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.importing))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker(getString(R.string.importing))
                    .setAutoCancel(true)
                    .build();
            notificationManager.notify(setId, n);

            finish();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            super.onProgressUpdate(values);

            if ((values[0] % 100) == 0) {
                n  = new Notification.Builder(getApplicationContext())
                        .setContentTitle(getString(R.string.importing))
                        .setContentText(String.valueOf(values[0]) + " " + getString(R.string.of) + " " + String.valueOf(values[1]) + " " + getString(R.string.imported))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setOngoing(true)
                        .build();
                notificationManager.notify(setId, n);
            }

        }

        protected void onPostExecute(Integer values) {

            super.onProgressUpdate(values);

            dbisleri();

            Intent intent = new Intent(ItemsActivity.this, ItemsActivity.class);

            intent.putExtra("setID", String.valueOf(setId));
            intent.putExtra("setTitle", setTitle);
            intent.putExtra("setIcon", String.valueOf(setIcon));
            intent.putExtra("setDescription", setDescription);
            intent.putExtra("setPrime", setPrime);
            intent.putExtra("setSetType", setSetType);
            PendingIntent pIntent = PendingIntent.getActivity(ItemsActivity.this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT );

            n  = new Notification.Builder(getApplicationContext())
                    .setContentTitle(getString(R.string.app_name) + " - " + setTitle)
                    .setContentText(getString(R.string.imported))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker(getString(R.string.imported))
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .build();
            notificationManager.notify(setId, n);



        }

    }




}


