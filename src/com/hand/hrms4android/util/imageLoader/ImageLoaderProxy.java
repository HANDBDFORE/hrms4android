package com.hand.hrms4android.util.imageLoader;

import java.io.IOException;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * [简要描述]:图片加载器的代理类 [详细描述]:代理了从网络加载图片的方法，在加载图片后配合缓存管理器进行缓存管理
 * 
 * @author [Emerson Zhang]
 * 
 * @version [版本号,Aug 23, 2012]
 * @see [ImageLoaderProxy]
 * @package [utils.imageloader]
 */
public class ImageLoaderProxy implements ImageLoader {
	/**
	 * 从网络加载资源，此对象为真实调用对象
	 */
	private ImageLoader internetImageLoader;
	private ImageLoader assetsImageLoader;
	private CacheImageManager cacheImageManager;

	public ImageLoaderProxy() {
		this.internetImageLoader = new InternetImageLoader();
		this.assetsImageLoader = new AssetsImageLoader();
		cacheImageManager = CacheImageManager.getCacheImageManager();
	}

	@Override
	public Bitmap getImage(String imageUrl) throws IOException {
		return getImage(imageUrl, 0, 0);
	}

	@Override
	public Bitmap getImage(String imageUrl, int width, int height) throws IOException {

		Uri imageUri = Uri.parse(imageUrl);
		String scheme = imageUri.getScheme();

		Bitmap resultBitmap = null;

		if (scheme.equals("bundle")) {
			// 调用内部图像
			resultBitmap = assetsImageLoader.getImage(imageUri.getHost());
		}

		else if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
			// 尝试从网络读取
			resultBitmap = internetImageLoader.getImage(imageUrl, width, height);
			// 更新缓存
			updateCache(resultBitmap, imageUrl);
		} else {
			throw new IOException("The scheme:" + scheme + " is not supported");
		}
		return resultBitmap;
	}

	private void updateCache(Bitmap bitmap, String url) {
		if (bitmap != null) {
			cacheImageManager.updateDrawableCache(url, bitmap, true);
		}
	}
}