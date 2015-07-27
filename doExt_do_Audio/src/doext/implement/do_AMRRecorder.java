package doext.implement;

import android.media.MediaRecorder;
import core.DoServiceContainer;

public class do_AMRRecorder extends RecorderBase implements Runnable{
	
	@Override
	public void startRecord(final int time, final String quality, final String outPath) {
		super.startRecord(time, quality, outPath);
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		mediaRecorder = new MediaRecorder();
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风  
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);// 设置输出文件格式  
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);// 设置编码格式  
		mediaRecorder.setOutputFile(outPath);// 使用绝对路径进行保存文件  
		mediaRecorder.setAudioSamplingRate(8000);
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {
			// 开始录音
			mediaRecorder.prepare();
			mediaRecorder.start();
			onRecordTimeChangeTask();
			onRecordListener.onStart();
			long startTimeMillis = System.currentTimeMillis();
			while (isRecording) {
				long endTimeMillis = System.currentTimeMillis();
				totalTimeMillis = endTimeMillis - startTimeMillis;
				if (((int) (totalTimeMillis) / 1000) > time && time != -1) {
					stopRecord();
				}
			}
		}catch(Exception e){
			stopRecord();
			onRecordListener.onError();
			DoServiceContainer.getLogEngine().writeError("AMR录音写入失败：", e);
			e.printStackTrace();
		}
	}
}
