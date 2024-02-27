package uqac.dim.rse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.model.Marker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import uqac.dim.rse.fragments.CustomAdapter;
import uqac.dim.rse.objects.Picto;

public class DownloadImageTask extends AsyncTask<Picto, Void, Bitmap> {
    private ImageView bmImage;
    private Context context;

    public DownloadImageTask(ImageView bmImage, Context context) {
        this.bmImage = bmImage;
        this.context = context;
    }

    protected Bitmap doInBackground(Picto... pictos) {
        Picto picto = pictos[0];

        File dir = context.getDir("rse_dir", Context.MODE_PRIVATE);
        File imageFile = new File(dir, picto.filename);

        if (imageFile.exists()) {
            return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        }

        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(picto.url).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception ex) {
            Log.e("DIM", ex.getMessage());
            ex.printStackTrace();
        }

        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            mIcon11.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        if (bmImage == null) {
            return;
        }
        bmImage.setImageBitmap(result);
    }
}
