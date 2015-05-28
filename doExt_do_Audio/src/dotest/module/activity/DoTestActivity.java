package dotest.module.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.doext.module.activity.R;

import core.DoServiceContainer;
import core.helper.DoIOHelper;
import core.object.DoModule;
import dotest.module.frame.debug.DoDataFS;
import dotest.module.frame.debug.DoPageViewFactory;
import dotest.module.frame.debug.DoService;

/**
 * 测试扩展组件Activity需继承此类，并重写相应测试方法；
 */
public class DoTestActivity extends Activity {
	
	protected DoModule model;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deviceone_test);
		DoService.Init(this);
		DoPageViewFactory doPageViewFactory = (DoPageViewFactory)DoServiceContainer.getPageViewFactory();
		doPageViewFactory.setCurrentActivity(this);
		try {
			initModuleModel();
			initUIView();
			initData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		onEvent();
	}

	/**
	 * 初始化UIView，扩展组件是UIModule类型需要重写此方法；
	 */
	protected void initUIView() throws Exception{

	}

	/**
	 * 初始化Model对象
	 */
	protected void initModuleModel() throws Exception {

	}

	/**
	 * 测试属性
	 * 
	 * @param view
	 */
	public void doTestProperties(View view) {

	}

	/**
	 * 测试（同步/异步）方法
	 * 
	 * @param view
	 */
	public void doTestMethod(View view) {
		doTestSyncMethod();
		doTestAsyncMethod();
	}

	/**
	 * 测试同步方法
	 */
	protected void doTestSyncMethod() {

	}

	/**
	 * 测试异步方法
	 */
	protected void doTestAsyncMethod() {

	}

	/**
	 * 测试Module订阅事件消息
	 */
	protected void onEvent() {

	}

	/**
	 * 测试模拟触发一个Module消息事件
	 * 
	 * @param view
	 */
	public void doTestFireEvent(View view) {

	}
	
	private void initData(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				AssetManager assetManager = getAssets();
				String[] files = null;
			    try {
			        files = assetManager.list("");
			        for (String fileName : files) {
			        	InputStream in = assetManager.open(fileName);
			        	copyFile(DoDataFS.getDataRootPath(DoTestActivity.this) + "/dotest", fileName,in);
					}
			    } catch (Exception e) {
			        Log.e("tag", e.getMessage());
			    }
			}
		}).start();
	}
	
	private static void copyFile(String fileToPath, String fileName,InputStream in)
			throws Exception {
		OutputStream out = null;
		try {
			DoIOHelper.createDirectory(fileToPath);
			
			out = new FileOutputStream(fileToPath + File.separator + fileName);
			byte[] buffer = new byte[1024];
			while (true) {
				int ins = in.read(buffer);
				if (ins == -1) {
					break;
				}
				out.write(buffer, 0, ins);
			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}
}
