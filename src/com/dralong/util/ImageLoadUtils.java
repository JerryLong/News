package com.dralong.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jerry on 2015/8/26.
 */
public class ImageLoadUtils {

    private static final int CORE_POOL_SIZE = 15;
    private static final int MAXIMUM_POOL_SIZE = 150;
    private static final int KEEP_ALIVE_TIME = 10;
    /**
     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
     */
    private LruCache<String, Bitmap> mMemoryCache;
    private Executor mExec = null;

    private static ImageLoadUtils mImageLoadUtils = null;

    public static ImageLoadUtils getInstanceAsycnImage() {
        if (mImageLoadUtils == null) {
            mImageLoadUtils = new ImageLoadUtils();
        }
        return mImageLoadUtils;
    }

    public ImageLoadUtils() {
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        // 设置图片缓存大小为程序最大可用内存的1/8
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };


    }

    /**
     * 将一张图片存储到LruCache中。
     *
     * @param key    LruCache的键，这里传入图片的URL地址。
     * @param bitmap LruCache的键，这里传入从网络上下载的Bitmap对象。
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
//            Logs.v("添加到内存 ");
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     *
     * @param key LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    public Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * 设置AsyncTask线程池中并发执行线程个数
     */
    public void setThreadPoolExecutor() {
        setThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE);
    }

    public void setThreadPoolExecutor(int corePoolSize, int maximumPoolSize) {
        mExec = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    /**
     * 通常类似 ListView 与 GridView 等视图组件在使用上面演示的AsyncTask 方法时会同时带来另外一个问题。
     * 为了更有效的处理内存，那些视图的子组件会在用户滑动屏幕时被循环使用。如果每一个子视图都触发一个AsyncTask ，
     * 那么就无法确保当前视图在结束task时，分配的视图已经进入循环队列中给另外一个子视图进行重用。 而且, 无法确保所有的
     * 异步任务能够按顺序执行完毕。
     *
     * @param imageUrl
     * @param imageView
     * @param resId     默认图片资源
     * @param width
     * @param height    width和height为0表示按原图大小返回
     */
    public void loadBitmap(Resources res, String imageUrl, ImageView imageView, int resId,
                           int width, int height) {

        Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
        if (bitmap != null) {
//            Logs.v("从内存取图片 " + imageUrl);
            imageView.setImageBitmap(bitmap);
            return;
        }

        if (cancelPotentialWork(imageUrl, imageView)) {
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);

            AsyncDrawable asyncDrawable = new AsyncDrawable(res, BitmapFactory.decodeResource(res, resId), task);
            imageView.setImageDrawable(asyncDrawable);

            if (mExec == null) {
                task.execute(imageUrl, String.valueOf(width), String.valueOf(height));
            } else {
                task.executeOnExecutor(mExec, imageUrl, String.valueOf(width),
                        String.valueOf(height));
            }

        }

    }

    public static boolean cancelPotentialWork(String imageUrl,
                                              ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.data;
            if (!bitmapData.equals(imageUrl)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was
        // cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    /**
     * 创建一个专用的Drawable的子类来储存返回工作任务的引用。在这种情况下，当任务完成时BitmapDrawable会被使用
     */
    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(
                    bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return (BitmapWorkerTask) bitmapWorkerTaskReference.get();
        }
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String data = "";

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage
            // collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            data = params[0];
            int widht = Integer.parseInt(params[1]);
            int height = Integer.parseInt(params[2]);
            return decodeSampledBitmapFromStream(data, widht, height);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = (ImageView) imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    addBitmapToMemoryCache(data, bitmap);
                }
            }
        }
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                         int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // width=0,height=0 表示返回原图
        if (reqWidth == 0 && reqHeight == 0) {
            return 1;
        }

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
//        Logs.v("calculateInSampleSize  height :" + height + " width :" + width);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromStream(String imageUrl, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        HttpURLConnection con = null;
        InputStream is = null;
        try {
            URL url = new URL(imageUrl);
            con = (HttpURLConnection) url.openConnection();
            is = con.getInputStream();
            BitmapFactory.decodeStream(is, null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);
//            Logs.v("options.inSampleSize  :" + options.inSampleSize);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            con.disconnect();
            is.close();

            con = (HttpURLConnection) url.openConnection();
            is = con.getInputStream();

            return BitmapFactory.decodeStream(is, null, options);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
                if (con != null) {
                    con.disconnect();
                    con = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
