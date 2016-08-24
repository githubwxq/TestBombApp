package com.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import com.ftp.FTP.DeleteFileProgressListener;
import com.ftp.FTP.DownLoadProgressListener;
import com.ftp.FTP.UploadProgressListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	public static final String FTP_CONNECT_SUCCESSS = "ftp连接成功";
	public static final String FTP_CONNECT_FAIL = "ftp连接失败";
	public static final String FTP_DISCONNECT_SUCCESS = "ftp断开连接";
	public static final String FTP_FILE_NOTEXISTS = "ftp上文件不存在";

	public static final String FTP_UPLOAD_SUCCESS = "ftp文件上传成功";
	public static final String FTP_UPLOAD_FAIL = "ftp文件上传失败";
	public static final String FTP_UPLOAD_LOADING = "ftp文件正在上传";

	public static final String FTP_DOWN_LOADING = "ftp文件正在下载";
	public static final String FTP_DOWN_SUCCESS = "ftp文件下载成功";
	public static final String FTP_DOWN_FAIL = "ftp文件下载失败";

	public static final String FTP_DELETEFILE_SUCCESS = "ftp文件删除成功";
	public static final String FTP_DELETEFILE_FAIL = "ftp文件删除失败";
	Button btn;
	String filePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		btn = (Button) findViewById(R.id.button_save);
		initView();
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveFile("第一次测试上传====wqeqweqweqweqweqwe");
			}
		});
	}

	private void initView() {

		//上传功能
		//new FTP().uploadMultiFile为多文件上传
		//new FTP().uploadSingleFile为单文件上传
		Button buttonUpload = (Button) findViewById(R.id.button_upload);
		buttonUpload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				new Thread(new Runnable() {
					@Override
					public void run() {

						// 上传
						File file = new File(filePath);
						try {

							//单文件上传
							new FTP().uploadSingleFile(file, "/fff", new UploadProgressListener() {

								@Override
								public void onUploadProgress(String currentStep, long uploadSize, File file) {
									// TODO Auto-generated method stub
									Log.d(TAG, currentStep);
									if (currentStep.equals(MainActivity.FTP_UPLOAD_SUCCESS)) {
										Log.d(TAG, "-----shanchuan--successful");
									} else if (currentStep.equals(MainActivity.FTP_UPLOAD_LOADING)) {
										long fize = file.length();
										float num = (float) uploadSize / (float) fize;
										int result = (int) (num * 100);
										Log.d(TAG, "-----shangchuan---" + result + "%");
									}
								}
							});
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}).start();

			}
		});

		//下载功能
		Button buttonDown = (Button) findViewById(R.id.button_down);
		buttonDown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				new Thread(new Runnable() {
					@Override
					public void run() {

						// 下载
						try {

							//单文件下载
							new FTP().downloadSingleFile("/fff/ftpTest.docx", "/mnt/sdcard/download/", "ftpTest.docx", new DownLoadProgressListener() {

								@Override
								public void onDownLoadProgress(String currentStep, long downProcess, File file) {
									Log.d(TAG, currentStep);
									if (currentStep.equals(MainActivity.FTP_DOWN_SUCCESS)) {
										Log.d(TAG, "-----xiazai--successful");
									} else if (currentStep.equals(MainActivity.FTP_DOWN_LOADING)) {
										Log.d(TAG, "-----xiazai---" + downProcess + "%");
									}
								}

							});

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}).start();

			}
		});

		//删除功能
		Button buttonDelete = (Button) findViewById(R.id.button_delete);
		buttonDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				new Thread(new Runnable() {
					@Override
					public void run() {

						// 删除
						try {

							new FTP().deleteSingleFile("/fff/ftpTest.docx", new DeleteFileProgressListener() {

								@Override
								public void onDeleteProgress(String currentStep) {
									Log.d(TAG, currentStep);
									if (currentStep.equals(MainActivity.FTP_DELETEFILE_SUCCESS)) {
										Log.d(TAG, "-----shanchu--success");
									} else if (currentStep.equals(MainActivity.FTP_DELETEFILE_FAIL)) {
										Log.d(TAG, "-----shanchu--fail");
									}
								}

							});

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}).start();

			}
		});

	}

	public  void saveFile(String str) {
		boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if (hasSDCard) {
			filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "hello.txt";
		} else
			filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "hello.txt";
System.out.println("filepath:"+filePath);
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				File dir = new File(file.getParent());
				dir.mkdirs();
				file.createNewFile();
			}

			FileOutputStream outStream = new FileOutputStream(file);
			outStream.write(str.getBytes());
			outStream.close();
			System.out.println("文件已经存在===="+file.exists());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
