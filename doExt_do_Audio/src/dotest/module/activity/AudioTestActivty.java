package dotest.module.activity;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import core.DoServiceContainer;
import doext.implement.do_Audio_Model;
import dotest.module.frame.debug.DoService;

public class AudioTestActivty extends DoTestActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void initModuleModel() throws Exception {
		this.model = new do_Audio_Model();
	}
	
	
	public void doplay(View view) {
		Map<String, String> _paras_back = new HashMap<String, String>();
		//http://staff2.ustc.edu.cn/~wdw/softdown/index.asp/0042515_05.ANDY.mp3
		_paras_back.put("path", "data://bb.mp3");
		_paras_back.put("point", "0");
        DoService.syncMethod(this.model, "play", _paras_back);
	}
	
	public void dostop(View view) {
		Map<String, String> _paras_back = new HashMap<String, String>();
        DoService.syncMethod(this.model, "stop", _paras_back);
	}
	
	public void doresume(View view) {
		Map<String, String> _paras_back = new HashMap<String, String>();
        DoService.syncMethod(this.model, "resume", _paras_back);
	}
	
	public void dopause(View view) {
		Map<String, String> _paras_back = new HashMap<String, String>();
        DoService.syncMethod(this.model, "pause", _paras_back);
	}
	
	public void startMP3Record(View view) {
		Map<String, String> _paras_back = new HashMap<String, String>();
		_paras_back.put("path", "data://audio");
		_paras_back.put("type", "mp3");
		_paras_back.put("quality", "low");
		_paras_back.put("limit", "-1");
        DoService.syncMethod(this.model, "startRecord", _paras_back);
	}
	
	public void startAACRecord(View view) {
		Map<String, String> _paras_back = new HashMap<String, String>();
		_paras_back.put("path", "data://audio");
		_paras_back.put("type", "aac");
		_paras_back.put("quality", "low");
		_paras_back.put("limit", "-1");
        DoService.syncMethod(this.model, "startRecord", _paras_back);
	}
	
	public void startAMRRecord(View view) {
		Map<String, String> _paras_back = new HashMap<String, String>();
		_paras_back.put("path", "data://audio");
		_paras_back.put("type", "amr");
		_paras_back.put("quality", "low");
		_paras_back.put("limit", "-1");
        DoService.syncMethod(this.model, "startRecord", _paras_back);
	}
	
	public void stopRecord(View view) {
		Map<String, String> _paras_back = new HashMap<String, String>();
        DoService.syncMethod(this.model, "stopRecord", _paras_back);
	}

	@Override
	protected void onEvent() {
		DoService.subscribeEvent(this.model, "playFinished", new DoService.EventCallBack() {
			@Override
			public void eventCallBack(String _data) {
				DoServiceContainer.getLogEngine().writeDebug("事件回调：" + _data);
			}
		});
		DoService.subscribeEvent(this.model, "recordProgress", new DoService.EventCallBack() {
			@Override
			public void eventCallBack(String _data) {
				DoServiceContainer.getLogEngine().writeDebug("事件回调：" + _data);
			}
		});
		DoService.subscribeEvent(this.model, "playProgress", new DoService.EventCallBack() {
			@Override
			public void eventCallBack(String _data) {
				DoServiceContainer.getLogEngine().writeDebug("事件回调：" + _data);
			}
		});
		DoService.subscribeEvent(this.model, "recordFinished", new DoService.EventCallBack() {
			@Override
			public void eventCallBack(String _data) {
				DoServiceContainer.getLogEngine().writeDebug("事件回调：" + _data);
			}
		});
		
		
	}

}
