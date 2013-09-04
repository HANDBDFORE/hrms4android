package com.hand.hrms4android.util.imageLoader;

import java.io.IOException;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import com.hand.hrms4android.application.HrmsApplication;

public class AssetsImageLoader implements ImageLoader {
	private AssetManager assetManager;

	public AssetsImageLoader() {
		assetManager = HrmsApplication.getApplication().getAssets();
	}

	@Override
	public Bitmap getImage(String iamgeFileName) throws IOException {
		Bitmap bitmap = BitmapFactory.decodeStream(assetManager.open(iamgeFileName));
		
		return bitmap;
	}

	@Override
	public Bitmap getImage(String imageUrl, int width, int height) throws IOException {
		throw new RuntimeException("Not supported");
	}

}
