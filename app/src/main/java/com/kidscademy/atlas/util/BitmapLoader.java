package com.kidscademy.atlas.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.animation.Animation;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import js.lang.Callback;
import js.log.Log;
import js.log.LogFactory;
import js.util.Params;

public class BitmapLoader extends AsyncTask<Bitmap> {
    private static final Log log = LogFactory.getLog(BitmapLoader.class);

    /**
     * Source image stream.
     */
    private InputStream stream;

    /**
     * Target image view is where bitmap is actually loaded.
     */
    private ImageView imageView;

    /**
     * Bitmap sub-sampling value, used by BitmapFactory.Options.inSampleSize.
     */
    private int sampleSize;

    private Animation animation;

    private Callback<Bitmap> callback;
    private Runnable runnable;

    /**
     * Load bitmap from drawable resource to target image view.
     *
     * @param resources execution context resources,
     * @param resId     source drawable resource ID,
     * @param imageView target image view.
     */
    public BitmapLoader(Resources resources, int resId, ImageView imageView) {
        this(resources, resId, imageView, 1);
    }

    /**
     * Load sub-sampled bitmap from drawable resource to target image view.
     *
     * @param resources  execution context resources,
     * @param resId      source drawable resource ID,
     * @param imageView  target image view,
     * @param sampleSize sub-sampling value.
     */
    public BitmapLoader(Resources resources, int resId, ImageView imageView, int sampleSize) {
        super();
        Params.notNull(resources, "Resources");
        Params.notNull(imageView, "Target image view");
        this.stream = resources.openRawResource(resId);
        this.imageView = imageView;
        this.sampleSize = sampleSize;
    }

    /**
     * Load bitmap from assets to target image view.
     *
     * @param context   execution context,
     * @param assetPath path relative to assets,
     * @param imageView target image view.
     */
    public BitmapLoader(Context context, String assetPath, ImageView imageView) {
        this(context, assetPath, imageView, 1);
    }

    /**
     * Load sub-sampled bitmap from assets to target image view.
     *
     * @param context    execution context,
     * @param assetPath  image path relative to assets,
     * @param imageView  target image view,
     * @param sampleSize sub-sampling value.
     */
    public BitmapLoader(Context context, String assetPath, ImageView imageView, int sampleSize) {
        super();
        Params.notNull(context, "Context");
        Params.notNull(assetPath, "Asset file path");
        Params.notNull(imageView, "Target image view");
        this.stream = openStream(context, assetPath);
        this.imageView = imageView;
        this.sampleSize = sampleSize;
    }

    /**
     * Load bitmap from external storage to callback.
     *
     * @param imageFile source image path, absolute to external storage,
     * @param callback  target callback for loaded bitmap.
     */
    public BitmapLoader(File imageFile, Callback<Bitmap> callback) {
        super();
        this.stream = openStream(imageFile);
        this.callback = callback;
    }

    /**
     * Load bitmap from external storage to image view.
     *
     * @param imageFile source image path, absolute to external storage,
     * @param imageView target image view.
     */
    public BitmapLoader(File imageFile, ImageView imageView) {
        this(imageFile, imageView, 1);
    }

    /**
     * Load sub-sampled bitmap from external storage to image.
     *
     * @param imageFile  source image path, absolute to external storage,
     * @param imageView  target image view,
     * @param sampleSize sub-sampling value.
     */
    public BitmapLoader(File imageFile, ImageView imageView, int sampleSize) {
        super();
        Params.notNull(imageFile, "Source image file");
        Params.isFile(imageFile, "Source image file");
        Params.notNull(imageView, "Target image view");
        this.stream = openStream(imageFile);
        this.imageView = imageView;
        this.sampleSize = sampleSize;
    }

    public void setAnimation(Animation animation) {
        Params.notNull(animation, "Animation");
        this.animation = animation;
    }

    public void setCallback(Callback<Bitmap> callback) {
        this.callback = callback;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    protected Bitmap execute() throws Throwable {
        if (stream == null) {
            throw new IOException("Unable to open bitmap stream.");
        }
        try {
            return Bitmaps.load(stream, sampleSize);
        } finally {
            stream.close();
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
        if (animation != null) {
            if (imageView == null) {
                throw new IllegalStateException("Bitmap loader animation on null view.");
            }
            imageView.startAnimation(animation);
        }
        if (callback != null) {
            callback.handle(bitmap);
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    private static InputStream openStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            log.error(e);
        }
        log.debug("Unable to load bitmap from file |%s|.", file);
        return null;
    }

    private static InputStream openStream(Context context, String assetPath) {
        try {
            return context.getResources().getAssets().open(assetPath);
        } catch (IOException e) {
            log.error(e);
        }
        log.debug("Unable to load bitmap from asset |%s|.", assetPath);
        return null;
    }
}