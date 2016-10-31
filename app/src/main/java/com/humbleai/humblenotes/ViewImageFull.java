package com.humbleai.humblenotes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;


public class ViewImageFull extends AppCompatActivity {

    private int currentX, currentY = 0;
    //Rect rectf = new Rect();
    private float currentZoom; // geçerli zoom seviyesi
    final private float startingZoom = 1; // ilk zoom seviyesi

    private RelativeLayout container;
    private GestureDetectorCompat mDetector;
    private ScaleGestureDetector mScaleDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_image_full);
        container = (RelativeLayout)findViewById(R.id.container);

        currentZoom = startingZoom;

        currentY = 0; currentX = 0;

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        mScaleDetector = new ScaleGestureDetector(this, new ScaleListener());

        setZoom();

        Bundle extras = getIntent().getExtras();


        String imagename = extras.getString("imagename");

        ImageView imageViewAddedImage = (ImageView) findViewById(R.id.imageViewImage);

        if (!(imagename != null ? imagename.equals("") : false)) {

            Bitmap thumbnail;
            try {
                File filePath = getFileStreamPath(imagename);
                //FileInputStream fi = new FileInputStream(filePath);
                thumbnail = BitmapFactory.decodeFile(filePath.getAbsolutePath());
                imageViewAddedImage.setImageBitmap(thumbnail);
            } catch (Exception ex) {
                imageViewAddedImage.setVisibility(View.GONE);
            }


        }

    }



    private void setZoom() {

        // containerın ve içindeki entitylerin uygun şekille scalelenmesi lazım

        container.setScaleX(currentZoom);
        container.setScaleY(currentZoom);

        // childlar her halükarda starting zooma bölünmeli
        // çünkü starting zoom kadar büyük başlıyorlar

        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);

            child.setScaleX(1/startingZoom);
            child.setScaleY(1/startingZoom);
        }
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onDown(MotionEvent event) {

            // dokunulan koordinatları alıyoruz

            currentX = (int) event.getRawX();
            currentY = (int) event.getRawY();

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                float distanceY) {

            // scrollu yapıyoruz

            container.scrollBy(Math.round((currentX - e2.getRawX())/ currentZoom) , Math.round((currentY - e2.getRawY()) / currentZoom));


            // current pozisyonu güncelliyoruz
            currentX = Math.round(e2.getRawX());
            currentY = Math.round(e2.getRawY());

            return true;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            // zoom in out işleri.
            currentZoom = currentZoom * detector.getScaleFactor();

            // min zoom 1f
            if (currentZoom > 1) {
                setZoom();
            } else {
                currentZoom = 1f;
            }

            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        this.mScaleDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
