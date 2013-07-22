package com.hand.hrms4android.util.imageLoader;

import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Handler;

/**
 * [简要描述]:异步图片加载管理器 [详细描述]:用于管理异步的图片加载，内部采用多线程进行图片下载
 * 
 * @author [Emerson Zhang]
 * 
 * @version [版本号,Aug 23, 2012]
 * @see [AsyncImageManager]
 * @package [utils.imageloader]
 * 
 */
public class AsyncListImageManager {
	/**
	 * 用于下载图片
	 */
	private ImageLoader imageLoader;

	final Handler handler = new Handler();

	public AsyncListImageManager() {
		// 使用代理
		imageLoader = new ImageLoaderProxy();
	}

	/**
	 * [简要描述]:新建一个线程进行图片加载。
	 * 
	 * @author [Emerson]
	 * 
	 * @method [prepareLoadImageThread]
	 * @param rowPosition
	 *            要加载图像的行位置
	 * @param imageUrls
	 *            期望得到的图像URL
	 * @param width
	 *            期望得到的图像宽
	 * @param height
	 *            期望得到的图像高
	 * @param listener
	 *            图像加载完成后的回调
	 */
	public void prepareLoadImageThread(final Integer rowPosition, final String imageUrl,
	        final ScrollableViewImageLoadListener listener) {
		
		Thread workerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// 执行加载。
				loadImage(imageUrl, rowPosition, listener);
			}
		});

		workerThread.start();

	}

	/**
	 * [简要描述]:尝试读取图片，结束后通知主线程 [详细描述]:
	 * 
	 * 
	 * 
	 * @date [Aug 23, 2012]
	 * @method [loadImage]
	 * @param imageUrl
	 * @param width
	 * @param height
	 * @param rowPosition
	 * @param listener
	 */
	private void loadImage(final String imageUrl, final Integer rowPosition,
	        final ScrollableViewImageLoadListener listener) {
		// System.out.println("loadImage calling. position is:" + rowPosition);
		try {
			final Bitmap drawable = imageLoader.getImage(imageUrl);
			if (drawable != null) {
				// 通知主线程图像已经加载完成
				handler.post(new Runnable() {
					@Override
					public void run() {
						// System.out.println("really do notify here, position:"
						// + rowPosition);
						listener.onImageLoad(rowPosition, drawable);
					}
				});

				// 因为已经加载到图像，就跳出循环
				return;
			}
		} catch (IOException e) {
			// 遇到IO问题，通知主线程读取出错
			handler.post(new Runnable() {
				@Override
				public void run() {
					listener.onError(rowPosition);
				}
			});
			e.printStackTrace();
		}
	}
}