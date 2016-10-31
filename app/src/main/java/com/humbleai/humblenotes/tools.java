package com.humbleai.humblenotes;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.util.Random;


class tools {

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmap(String filePath,
                                             int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }




    public static int randomColor(Context context) {
        TypedArray colors_icons = context.getResources().obtainTypedArray(R.array.colors_icons);
        final Random random = new Random();
        final int randColor = random.nextInt(colors_icons.length());
        final int iconRes = colors_icons.getResourceId(randColor, R.color.colorMainBg);
        colors_icons.recycle();
        return iconRes;
    }

    public static void deleteNotebook (Context context, SetList notebook) {
        SetListSQLiteHelper db = new SetListSQLiteHelper(context);
        db.deleteSetList(notebook.getId());

        SetItemSQLiteHelper db2 = new SetItemSQLiteHelper(context);

        for (SetItem item : db2.getAllSetItems(notebook.getId(),0, true))  {
            tools.deleteImage(context, item);
            db2.deleteSetItem(item);
        }

    }

    public static SetItem deleteImage(Context context, SetItem setItem) {
        if (!setItem.getImage().equals("")) {
            String dir = context.getFilesDir().getAbsolutePath();
            File file = new File(dir, setItem.getImage());

            file.delete();

            setItem.setImage("");
        }

        return setItem;
    }


}
