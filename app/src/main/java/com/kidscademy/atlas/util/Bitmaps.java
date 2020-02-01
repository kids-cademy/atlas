package com.kidscademy.atlas.util;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import js.log.Log;
import js.log.LogFactory;
import js.util.Files;

public final class Bitmaps {
    private static final Log log = LogFactory.getLog(Bitmaps.class);

    /**
     * Convenient alternative for {@link #load(File, int)} when sample size is not used.
     *
     * @param imageFile image file from application storage.
     * @return newly created bitmap or null.
     */
    public static Bitmap load(File imageFile) {
        return load(imageFile, 1);
    }

    /**
     * Attempt to load bitmap from absolute path, returning newly created bitmap instance or null if file not found. This
     * method allocates memory for and creates a new bitmap instance. It is caller responsibility to ensure returned
     * instance reference is not held indefinitely in order to allow garbage collector to reclaim allocated memory.
     *
     * @param imageFile  image file from application storage,
     * @param sampleSize sub-sampling value, 1 for keeping original image size.
     * @return newly created bitmap or null.
     */
    public static Bitmap load(File imageFile, int sampleSize) {
        InputStream stream = null;
        try {
            stream = new FileInputStream(imageFile);
            return load(stream, sampleSize);
        } catch (FileNotFoundException e) {
            return null;
        } finally {
            Files.close(stream);
        }
    }

    public static Bitmap load(InputStream stream, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = sampleSize;
        return BitmapFactory.decodeStream(stream, null, options);
    }

    private static String FILE_URI = "file";
    private static String CONTENT_URI = "content";

    public static Bitmap decodeFile(File file, float width, float height) {
        Source source = new FileSource(file);
        return decode(source, width, height);
    }

    public static Bitmap decodeUri(Context context, Uri uri, float width, float height) throws IOException {
        final String scheme = uri.getScheme().toLowerCase(Locale.getDefault());
        if (FILE_URI.equals(scheme)) {
            return decodeFile(new File(uri.getPath()), width, height);
        }
        Source source = null;
        if (CONTENT_URI.equals(scheme)) {
            source = new ContentSource(context, uri);
        } else {
            source = new StreamSource(context, uri);
        }
        return decode(source, width, height);
    }

    /**
     * Decode image source with rescale to requested dimensions. Takes care to rotate image if image source has
     * orientation non zero. If bitmap decoding fails returns null.
     *
     * @param source image source,
     * @param width  requested width,
     * @param height requested height.
     * @return decoded image bitmap, possible null.
     */
    private static Bitmap decode(Source source, float width, float height) {
        if (!source.isValid()) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        source.decode(options);

        float bitmapSmallestDimension = Math.min(options.outWidth, options.outHeight);
        float viewSmallestDimension = Math.min(width, height);

        options.inJustDecodeBounds = false;
        options.inSampleSize = roundToSampleSize(bitmapSmallestDimension / viewSmallestDimension);
        Bitmap bitmap = source.decode(options);
        assert bitmap != null;

        int orientation = source.getOrientation();
        if (orientation != 0) {
            Matrix matrix = new Matrix();
            matrix.preRotate(orientation);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        return bitmap;
    }

    private static int roundToSampleSize(double scale) {
        int sampleSize = 1;
        for (int i = 0; i < 8; ++i) {
            int j = sampleSize << 1;
            if (j > scale) {
                break;
            }
            sampleSize = j;
        }
        return sampleSize;
    }

    private static interface Source {
        boolean isValid();

        Bitmap decode(BitmapFactory.Options options);

        int getOrientation();
    }

    private static class FileSource implements Source {
        private File file;

        public FileSource(File file) {
            log.trace("FileSource(File)");
            this.file = file;
        }

        @Override
        public boolean isValid() {
            return file != null;
        }

        @Override
        public Bitmap decode(Options options) {
            assert file != null;
            return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        }

        @Override
        public int getOrientation() {
            try {
                ExifInterface exif = new ExifInterface(file.getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        return 90;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        return 180;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        return 270;
                }
            } catch (IOException e) {
                log.error(e);
            }
            return 0;
        }
    }

    private static class StreamSource extends FileSource {
        public StreamSource(Context context, Uri uri) {
            super(file(context, uri));
            log.trace("StreamSource(Context, Uri)");
        }

        private static File file(Context context, Uri uri) {
            try {
                File file = File.createTempFile(CONTENT_URI, null);
                file.deleteOnExit();
                Files.copy(context.getContentResolver().openInputStream(uri), file);
                return file;
            } catch (IOException e) {
                log.error(e);
            }
            return null;
        }
    }

    private static class ContentSource implements Source {
        private File file;
        private int orientation;

        public ContentSource(Context context, Uri uri) {
            log.trace("ContentSource(Context, Uri)");
            String[] projection =
                    {
                            // Path to the file on disk.
                            Images.ImageColumns.DATA,
                            // The orientation for the image expressed as degrees.
                            Images.ImageColumns.ORIENTATION
                    };
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (!cursor.moveToFirst()) {
                return;
            }
            file = new File(cursor.getString(0));
            orientation = cursor.getInt(1);
        }

        @Override
        public boolean isValid() {
            return file != null;
        }

        @Override
        public Bitmap decode(Options options) {
            return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        }

        @Override
        public int getOrientation() {
            return orientation;
        }
    }

    public static Bitmap getScalledBitmap(Resources resources, int resId, float size) {
        BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
        boundsOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, boundsOptions);

        int width = boundsOptions.outWidth;
        int height = boundsOptions.outHeight;
        float scale = Math.max(width, height) / size;

        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inSampleSize = roundToSampleSize(scale);
        Bitmap bitmap = BitmapFactory.decodeResource(resources, resId, decodeOptions);

        float scalledWidth = width / scale;
        float scalledHeight = height / scale;
        return getScalledBitmap(bitmap, scalledWidth, scalledHeight);
    }

    public static Bitmap getScalledBitmap(Bitmap bitmap, float width, float height) {
        // do not use Bitmap.createScaledBitmap because of poor results

        Bitmap scaledBitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);

        float ratioX = width / bitmap.getWidth();
        float ratioY = height / bitmap.getHeight();
        float middleX = width / 2.0f;
        float middleY = height / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

    public static Bitmap blurRenderScript(Context context, Bitmap smallBitmap, int radius) {
        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmap = Bitmap.createBitmap(smallBitmap.getWidth(), smallBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        // Get JPEG pixels. Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        // Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        // Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

    public static int getAverageColor(Bitmap bitmap) {
        if (null == bitmap) {
            return android.graphics.Color.TRANSPARENT;
        }

        int red = 0;
        int green = 0;
        int blue = 0;
        int alpha = 0;

        final boolean hasAlpha = bitmap.hasAlpha();

        int pixelsCount = bitmap.getWidth() * bitmap.getHeight();
        int[] pixels = new int[pixelsCount];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int y = 0, h = bitmap.getHeight(); y < h; y++) {
            for (int x = 0, w = bitmap.getWidth(); x < w; x++) {
                int color = pixels[x + y * w];

                red += (color >> 16) & 0xFF;
                green += (color >> 8) & 0xFF;
                blue += (color & 0xFF);

                if (hasAlpha) {
                    alpha += (color >>> 24);
                }
            }
        }

        return Color.argb((hasAlpha) ? (alpha / pixelsCount) : 255, red / pixelsCount, green / pixelsCount, blue / pixelsCount);
    }

    public static Bitmap changeImageColor(Bitmap sourceBitmap, int color) {
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        p.setColorFilter(filter);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
        return resultBitmap;
    }

    public static Drawable bitmap2Drawable(Context context, Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private Bitmaps() {
    }
}
