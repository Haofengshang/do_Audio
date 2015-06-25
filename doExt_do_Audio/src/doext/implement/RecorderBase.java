package doext.implement;

import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.os.storage.StorageManager;
import doext.define.do_IRecord;

public class RecorderBase implements do_IRecord{
	
	protected boolean isRecording = true;
	protected OnRecordListener onRecordListener;
	protected long totalTimeMillis;
	private Timer timer;
	protected AudioRecord mAudioRecord;
	protected MediaRecorder mediaRecorder;
	protected int mSampleRate;
    protected short[] mBuffer;
    protected int mBufferSize = AudioRecord.ERROR_BAD_VALUE;
    protected int time;
    protected String outPath;

	protected void onRecordTimeChangeTask(){
		timer = new Timer();
		TimerTask task = new TimerTask(){
			@Override
			public void run() {
				onRecordListener.onRecordTimeChange(totalTimeMillis);
			}
	    };
    	timer.schedule(task, 1000, 1000);
	}
    
    protected void init(Runnable run) {
        for (int sampleRate : new int[] { 8000, 11025, 16000, 22050, 32000, 44100, 47250, 48000 }) {
            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_8BIT }) {
                for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO,
                        AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.CHANNEL_CONFIGURATION_STEREO }) {
                    // Try to initialize
                    try {
                        mBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
                        if (mBufferSize < 0) {
                            continue;
                        }
                        mBuffer = new short[mBufferSize];
                        mAudioRecord = new AudioRecord(AudioSource.MIC, sampleRate, channelConfig, audioFormat, mBufferSize);
                        if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                            mSampleRate = sampleRate;
                            new Thread(run).start();
                            return;
                        }
                        mAudioRecord.release();
                        mAudioRecord = null;
                    }
                    catch (Exception e) {
                        // Do nothing
                    	e.printStackTrace();
                    }
                }
            }
        }
    }

	@Override
	public void startRecord(int time, String quality, String outPath) {
		this.time = time;
		this.outPath = outPath;
	}

	@Override
	public void stopRecord() {
		isRecording = false;
		if (mAudioRecord != null) {
			mAudioRecord.stop();
			mAudioRecord.release();
			mAudioRecord = null;
		}
		if (mediaRecorder != null) {
			mediaRecorder.stop();
			mediaRecorder.release();
			onRecordListener.onFinished();
		}
		if(timer != null){
			timer.cancel();
		}
	}

	@Override
	public void setOnRecordListener(OnRecordListener onRecordListener) {
		this.onRecordListener = onRecordListener;
	}

}
