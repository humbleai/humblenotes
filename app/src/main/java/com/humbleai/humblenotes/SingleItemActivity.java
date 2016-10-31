package com.humbleai.humblenotes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class SingleItemActivity extends AppCompatActivity {

   // private String selectedImageUriString;
    private int currentColor;
    private RelativeLayout relativeLayout;
    private SetItemSQLiteHelper db;
    private int position;
    private ImageView imageViewAddedImage;
    private SetItem setItem;
    private Button removeImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_single_item);

        db = new SetItemSQLiteHelper(this);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeview);
        final Button addButton = (Button) findViewById(R.id.buttonAdd);
        removeImageButton = (Button) findViewById(R.id.buttonRemoveImage);
        final ImageButton changeColorButton = (ImageButton) findViewById(R.id.buttonChangeColor);
        final ImageButton addImageButton = (ImageButton) findViewById(R.id.buttonAddImage);
        final ImageButton addDateTime = (ImageButton) findViewById(R.id.buttonAddDateTime);
        final EditText editText = (EditText) findViewById(R.id.edittext);
        imageViewAddedImage = (ImageView) findViewById(R.id.imageViewImage);

        position = getIntent().getIntExtra("position", -1);

        currentColor = R.color.color_card_bg;

      //  selectedImageUriString = "";

        setItem = (SetItem) getIntent().getSerializableExtra("setItem"); // editse note geliyor değilse yeni not cardbg


        // gelen setitema göre notu gösterlerim
        editText.setText(setItem.getTitle());  // metni ayarla
        currentColor = setItem.getIcon(); // rengi ayarla
        relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), currentColor));

        // varsa resmi ayarla
        if (!setItem.getImage().equals("")) {

            try {
                File filePath = getFileStreamPath(setItem.getImage());
                imageViewAddedImage.setImageBitmap(tools.decodeSampledBitmap(filePath.getPath(), 200, 200));
                imageViewAddedImage.setVisibility(View.VISIBLE);
                removeImageButton.setVisibility(View.VISIBLE);
               // Log.e("resim", "var");
            } catch (Exception ex) {
               // Log.e("resim", "exception");
                imageViewAddedImage.setVisibility(View.GONE);
                removeImageButton.setVisibility(View.GONE);
            }
        } else { // resim yoksa viewı ve removeu gizle
           // Log.e("resim", "yok");
           // Log.e("resim", setItem.toString());
            imageViewAddedImage.setVisibility(View.GONE);
            removeImageButton.setVisibility(View.GONE);
        }



        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItem.setTitle(editText.getText().toString());

                if (position == -1) {
                    Long inserted = db.addSetItem(setItem);
                    setItem.setId(inserted.intValue());
                }
                setItem = saveImage();
                int updated = db.updateSetItem(setItem);
                setResult(setItem);
            }
        });


        changeColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentColor = tools.randomColor(v.getContext());
                relativeLayout.setBackgroundColor(ContextCompat.getColor(v.getContext(), currentColor));
                setItem.setIcon(currentColor);
            }
        });

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        getString(R.string.selectimage)), 1);


            }
        });

        addDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String now = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                editText.setText(editText.getText() + "\n\n" + now);
            }
        });

        removeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // resim kaldırıldığında siliyoruz

                imageViewAddedImage.setVisibility(View.GONE);
                imageViewAddedImage.setImageBitmap(null);
                setItem.setImage("");
               // setItem = tools.deleteImage(v.getContext(), setItem);
             //   selectedImageUriString = "";
                removeImageButton.setVisibility(View.GONE);
                Snackbar.make(v, getString(R.string.removed), Snackbar.LENGTH_SHORT);
            }
        });

        imageViewAddedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // resmi full screen aç

                Intent newintent = new Intent(v.getContext(), ViewImageFull.class);
                newintent.putExtra("imagename", setItem.getImage());
                startActivity(newintent);
            }
        });

        
    }

    private SetItem saveImage() {
        try {
            Bitmap bitmap = ((BitmapDrawable)imageViewAddedImage.getDrawable()).getBitmap();
            if (bitmap != null) {
                String imageName = String.valueOf(setItem.getId()) + ".png";
                FileOutputStream fos = openFileOutput(imageName, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.WEBP, 100, fos);
                fos.close();
                setItem.setImage(imageName);
               // Log.e("resim", "kaydedildi");
            } else {
               setItem = tools.deleteImage(this, setItem);
               // Log.e("resim", "vardı silindi");
            }

        } catch (Exception e) {
           // Log.e("hata", "burda");
            Snackbar.make(relativeLayout, R.string.newListFail, Snackbar.LENGTH_SHORT).show();
        }
        return setItem;
    }

    private void setResult(SetItem item)
    {
        Intent intent=new Intent();
        intent.putExtra("position", position);
        intent.putExtra("setItem", item);
        setResult(RESULT_OK, intent);

        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                // resim seçildiğinde kaydediyoruz

                Uri selectedImageUri =  Uri.parse(data.getDataString());
               // selectedImageUriString = selectedImageUri.toString();
                imageViewAddedImage.setImageURI(selectedImageUri);
                imageViewAddedImage.setVisibility(View.VISIBLE);
                removeImageButton.setVisibility(View.VISIBLE);
            }

        } else {
           // selectedImageUriString ="";
            if (setItem.getImage().equals("")) removeImageButton.setVisibility(View.GONE);
        }
    }


}
