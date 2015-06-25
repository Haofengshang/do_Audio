package doext.implement;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.sinaapp.bashell.VoAACEncoder;

import core.DoServiceContainer;

public class do_AACRecorder extends RecorderBase implements Runnable{
	
	@Override
	public void startRecord(int time, String quality, String outPath) {
		super.startRecord(time, quality, outPath);
		init(this);
	}

	@Override
	public void run() {
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(outPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		VoAACEncoder vo = new VoAACEncoder();
		vo.Init(mSampleRate, 16000, (short) 1, (short) 1);// 采样率:16000,bitRate:32k,声道数:1，编码:0.raw
		try{
			mAudioRecord.startRecording();
			onRecordTimeChangeTask();
			onRecordListener.onStart();
			long startTimeMillis = System.currentTimeMillis();
			while (isRecording) {
				long endTimeMillis = System.currentTimeMillis();
				totalTimeMillis = endTimeMillis - startTimeMillis;
				if (((int) (totalTimeMillis) / 1000) > time && time != -1) {
					stopRecord();
				}
				byte[] mBuffer = new byte[mBufferSize];
				int bufferRead = mAudioRecord.read(mBuffer, 0, mBufferSize);
				byte[] ret = vo.Enc(mBuffer);
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
			onRecordListener.onRecordTimeChange(totalTimeMillis);
			onRecordListener.onFinished();
		}catch(Exception e){
			onRecordListener.onError();
			DoServiceContainer.getLogEngine().writeError("录音失败：startRecord", e);
			e.printStackTrace();
		}finally{
			vo.Uninit();
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
