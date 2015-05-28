package doext.implement;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.sinaapp.bashell.VoAACEncoder;

import core.DoServiceContainer;

public class do_AACRecorder extends RecorderBase{
	
	@Override
	public void startRecord(final int time, final String quality, final String outPath) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				int sampleRate = getSampleRate(quality);
				FileOutputStream output = null;
				try {
					output = new FileOutputStream(outPath);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				VoAACEncoder vo = new VoAACEncoder();
				vo.Init(sampleRate, 16000, (short) 1, (short) 1);// 采样率:16000,bitRate:32k,声道数:1，编码:0.raw
				// 1.ADTS
				int min = AudioRecord.getMinBufferSize(sampleRate,
						AudioFormat.CHANNEL_IN_MONO,
						AudioFormat.ENCODING_PCM_16BIT);
				if (min < 2048) {
					min = 2048;
				}
				byte[] tempBuffer = new byte[2048];
				AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC,
						sampleRate, AudioFormat.CHANNEL_IN_MONO,
						AudioFormat.ENCODING_PCM_16BIT, min);
				startTimer(new Timer());
				try{
					record.startRecording();
					long startTimeMillis = System.currentTimeMillis();
					while (isRecording) {
						long endTimeMillis = System.currentTimeMillis();
						totalTimeMillis = endTimeMillis - startTimeMillis;
						if (((int) (totalTimeMillis) / 1000) > time && time != -1) {
							break;
						}
						int bufferRead = record.read(tempBuffer, 0, 2048);
						byte[] ret = vo.Enc(tempBuffer);
						if (bufferRead > 0) {
							try {
								output.write(ret);
							} catch (IOException _err) {
								DoServiceContainer.getLogEngine().writeError("AAC录音写入失败：", _err);
								_err.printStackTrace();
								break;
							}
						}
					}
					onRecordTimeChangeListener.onRecordTimeChange(totalTimeMillis);
				}catch(Exception e){
					DoServiceContainer.getLogEngine().writeError("录音失败：startRecord", e);
					e.printStackTrace();
				}finally{
					record.stop();
					record.release();
					record = null;
					vo.Uninit();
					try {
						output.close();
					} catch (IOException e) {
						e.printStackTrace();
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
