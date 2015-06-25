package doext.implement;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.deviceone.lame.DoMP3lame;

import core.DoServiceContainer;
import core.helper.DoIOHelper;

public class do_MP3Recorder extends RecorderBase implements Runnable{
	
	@Override
	public void startRecord(final int time, final String quality, final String outPath) {
		super.startRecord(time, quality, outPath);
		init(this);
	}

	@Override
	public void run() {
		String temp = outPath + ".raw";
		DataOutputStream output = null;
		try {
			output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(temp)));
		} catch (FileNotFoundException e) {
		}
		try {
			mAudioRecord.startRecording(); // 开启录音获取音频数据
			onRecordTimeChangeTask();
			onRecordListener.onStart();
			// 开始录音
			long startTimeMillis = System.currentTimeMillis();
			int readSize = 0;
			while (isRecording) {
				long endTimeMillis = System.currentTimeMillis();
				totalTimeMillis = endTimeMillis - startTimeMillis;
				if (((int) (totalTimeMillis) / 1000) > time && time != -1) {
					stopRecord();
				}
				readSize = mAudioRecord.read(mBuffer, 0, mBufferSize);
				for (int i = 0; i < readSize; i++) {
					output.writeShort(mBuffer[i]);
				}
			}
			output.flush();
			DoMP3lame lame = new DoMP3lame(1, mSampleRate, 96);
			lame.toMP3(temp, outPath);
			DoIOHelper.deleteFile(temp);
			onRecordListener.onRecordTimeChange(totalTimeMillis);
			onRecordListener.onFinished();
		}catch(Exception e){
			stopRecord();
			onRecordListener.onError();
			DoServiceContainer.getLogEngine().writeError("录音失败：startRecord", e);
			e.printStackTrace();
		} finally {
			if(output != null){
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
