package com.hdf.easytools.config;

import java.io.File;

import com.hdf.easytools.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import android.content.Context;
import android.graphics.Bitmap;

public class ImageLoaderConfig {
	// EXACTLY :图像将完全按比例缩小的目标大小
	// EXACTLY_STRETCHED:图片会缩放到目标大小完全
	// IN_SAMPLE_INT:图像将被二次采样的整数倍
	// IN_SAMPLE_POWER_OF_2:图片将降低2倍，直到下一减少步骤，使图像更小的目标大小
	// NONE:图片不会调整

	/**
	 * 配置ImageLoder
	 */
	@SuppressWarnings("unused")
	private static DisplayImageOptions initDisplayOptions(boolean isShowDefault) {
		// 初始化ImageLoader
		@SuppressWarnings("deprecation")
		DisplayImageOptions options = new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.EXACTLY)
				.showStubImage(R.drawable.icon_stub) // 设置图片下载期间显示的图片
				.showImageForEmptyUri(R.drawable.icon_empty) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.icon_error) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true) // 设置图片下载前的延迟
				.delayBeforeLoading(100)// delayInMillis为你设置的延迟时间
				.displayer(new FadeInBitmapDisplayer(100))// 淡入
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				.bitmapConfig(Bitmap.Config.RGB_565)
				// .displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
				.build(); // 创建配置过得DisplayImageOption对象
		return options;
	}

	/**
	 * 设置缓存
	 * 
	 * @param context
	 * @param cacheFile
	 */
	public static void initImageLoader(Context context, File cacheFile) {
		// 获取本地缓存的目录， 该目录在sd卡的根目
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context);
		// 线程池加载数量
		builder.threadPoolSize(3);
		// 设定线程优先级
		builder.threadPriority(Thread.NORM_PRIORITY - 2);
		// 设定内存为弱缓存
		// builder.memoryCache(new WeakMemoryCache());
		// 设定内存图片缓存大小的限制，不设置默认为屏幕的宽,即保存的每个缓存文件的最大长宽
		builder.memoryCacheExtraOptions(480, 800);
		// 设置硬盘缓存100mb
		builder.discCacheSize(100 * 1024 * 1024);
		// builder.memoryCacheSize(1 * 1024 * 1024);
		builder.denyCacheImageMultipleSizesInMemory();
		// 设定缓存的路径
		builder.discCache(new UnlimitedDiscCache(cacheFile));
		// 设定网络连接超时timeout:10s 读取网络连接超时read timeout 60s
		builder.imageDownloader(new BaseImageDownloader(context, 10 * 1000, 60 * 1000));
		// 设置ImageLoader的配置参
		builder.defaultDisplayImageOptions(initDisplayOptions(true));

		// 初始化imageLoader
		ImageLoader.getInstance().init(builder.build());

	}
}
