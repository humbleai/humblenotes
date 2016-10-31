package com.humbleai.humblenotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class IncomingActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private final List<SetList> myDataset = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);

        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAppCaption));

        toolbar.setTitle("  " + getString(R.string.pick_set));

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);



        if (savedInstanceState == null) {



            // Get intent, action and MIME type
            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {

                if ("text/plain".equals(type)) {


                    dbIsleri();

                }
            }
        }


    }

    private void dbIsleri() {
        SetListSQLiteHelper db = new SetListSQLiteHelper(this);

        for (SetList item: db.getAllSetLists()) {
            item.setItemViewType();
            myDataset.add(item);
        }
        RecyclerView.Adapter mAdapter = new SetListAdapter(myDataset, this);
        mRecyclerView.setAdapter(mAdapter);

        //  db.close();
    }

}
