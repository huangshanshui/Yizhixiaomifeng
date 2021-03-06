package com.yizhixiaomifeng;

import java.io.File;
import java.io.FileOutputStream;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class SetHead extends Activity {
	private static final String IMAGE_FILE_NAME = "head.jpg";
	private String[] items = new String[] { "选择本地图片", "拍照" };
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	ImageView head ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_head);
		head = (ImageView) findViewById(R.id.staff_head);
		Bitmap bm = null;
		if ((bm = haveHead()) != null) {
			//head.setImageBitmap(bm);
			//Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.bt);
		      Bitmap output=toRoundBitmap(bm);
		      head.setImageBitmap(output);
		}
		head.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				showDialog();
			}
		});
	}
/**********************控制头像******************************/
	
	public static Bitmap haveHead() {
		Bitmap bitmap = BitmapFactory
				.decodeFile("data/data/com.yizhixiaomifeng/files/head.jpg");
		return bitmap;
	}

	public void showDialog() {
		new AlertDialog.Builder(this)
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent fileIntent = new Intent(
									Intent.ACTION_GET_CONTENT);
							//fileIntent.setClassName("com.android.gallery", "com.android.camera.CropImage"); 
							fileIntent.setType("image/*");
							fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
							startActivityForResult(fileIntent,
									IMAGE_REQUEST_CODE);
							break;
						case 1:
							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE); // 相机资源
							if (hasSdcard()) {
								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(new File(Environment
												.getExternalStorageDirectory(),
												IMAGE_FILE_NAME)));
							}
							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) // 处理返回的结果
	{
		
		if(resultCode!=0)
		{
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (hasSdcard()) {
					File tempFile = new File(
							Environment.getExternalStorageDirectory(),
							IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(SetHead.this, "未找到存储卡，无法存储照片！",
							Toast.LENGTH_LONG).show();
				}
				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
				}
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	//头像的截取
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);//RESULT_REQUEST_CODE
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			//转成圆形
			Bitmap roundBitmap = toRoundBitmap(photo);
			@SuppressWarnings("deprecation")
			Drawable drawable = new BitmapDrawable(roundBitmap);
			saveHead(roundBitmap);
			head.setImageDrawable(drawable);

		}
	}
	
	//保存头像，在根目录想的head.jpg

	public void saveHead(Bitmap bitmap) {
		FileOutputStream fos = null;
		try {
			fos = openFileOutput("head.jpg", 0);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * 检查是否存在SDCard
	 * 
	 * @return
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 返回圆形的bitmap
	 * @param bitmap
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {  
        //圆形图片宽高  
        int width = bitmap.getWidth();  
        int height = bitmap.getHeight();  
        //正方形的边长  
        int r = 0;  
        //取最短边做边长  
        if(width > height) {  
            r = height;  
        } else {  
            r = width;  
        }  
        //构建一个bitmap  
        Bitmap backgroundBmp = Bitmap.createBitmap(width,  
                 height, Config.ARGB_8888);  
        //new一个Canvas，在backgroundBmp上画图  
        Canvas canvas = new Canvas(backgroundBmp);  
        Paint paint = new Paint();  
        //设置边缘光滑，去掉锯齿  
        paint.setAntiAlias(true);  
        //宽高相等，即正方形  
        RectF rect = new RectF(0, 0, r, r);  
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，  
        //且都等于r/2时，画出来的圆角矩形就是圆形  
        canvas.drawRoundRect(rect, r/2, r/2, paint);  
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉  
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        //canvas将bitmap画在backgroundBmp上  
        canvas.drawBitmap(bitmap, null, rect, paint);  
        //返回已经绘画好的backgroundBmp  
        return backgroundBmp;  
    }
	
}
