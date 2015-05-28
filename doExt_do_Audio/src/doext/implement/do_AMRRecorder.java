package doext.implement;

import java.util.Timer;

import android.media.MediaRecorder;
import core.DoServiceContainer;

public class do_AMRRecorder extends RecorderBase {
	
	@Override
	public void startRecord(final int time, final String quality, final String outPath) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				int sampleRate = getSampleRate(quality);
				MediaRecorder mediaRecorder = new MediaRecorder();
				android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
				startTimer(new Timer());
				try {
					// 开始录音
					mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风  
					mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);// 设置输出文件格式  
					mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);// 设置编码格式  
					mediaRecorder.setOutputFile(outPath);// 使用绝对路径进行保存文件  
					mediaRecorder.setAudioSamplingRate(sampleRate);
					mediaRecorder.prepare();
					mediaRecorder.start();
					long startTimeMillis = System.currentTimeMillis();
					while (isRecording) {
						long endTimeMillis = System.currentTimeMillis();
						totalTimeMillis = endTimeMillis - startTimeMillis;
						if (((int) (totalTimeMillis) / 1000) > time && time != -1) {
							break;
						}
					}
					onRecordTimeChangeListener.onRecordTimeChange(totalTimeMillis);
				}catch(Exception e){
					DoServiceContainer.getLogEngine().writeError("AMR录音写入失败：", e);
					e.printStackTrace();
				}finally {
					if (mediaRecorder != null) {
						mediaRecorder.stop();
						mediaRecorder.release();
					}
					if(timer != null){
						timer.cancel();
					}
				}
			}
		};
		new Thread(runnable).start();
	}
}
