package doext.implement;

import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import doext.define.do_IRecord;

public class RecorderBase implements do_IRecord{
	
	//Hz压缩的频率SAMPLERATE
	private final static int AUDIO_SAMPLE_RATE_HIGH = 44100;
	private final static int AUDIO_SAMPLE_RATE_NORMAL = 22050;
	private final static int AUDIO_SAMPLE_RATE_LOW = 8000;
	
	protected boolean isRecording = true;
	protected OnRecordListener onRecordListener;
	protected long totalTimeMillis;
	protected Timer timer;
	
	protected void startTimer(Timer timer){
		this.timer = timer;
		TimerTask task = new TimerTask(){
			@Override
			public void run() {
				onRecordListener.onRecordTimeChange(totalTimeMillis);
			}
	    };
    	timer.schedule(task, 1000, 1000);
	}
	
	protected AudioRecord mAudioRecord;
	protected int mSampleRate;
    private short mAudioFormat;
    private short mChannelConfig;

    protected short[] mBuffer;
    protected int mBufferSize = AudioRecord.ERROR_BAD_VALUE;
    protected void createAudioRecord() {
        if (mSampleRate > 0 && mAudioFormat > 0 && mChannelConfig > 0) {
            mAudioRecord = new AudioRecord(AudioSource.MIC, mSampleRate, mChannelConfig, mAudioFormat, mBufferSize);

            return;
        }

        // Find best/compatible AudioRecord
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
                        mAudioRecord = new AudioRecord(AudioSource.MIC, sampleRate, channelConfig, audioFormat,
                                mBufferSize);

                        if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                            mSampleRate = sampleRate;
                            mAudioFormat = audioFormat;
                            mChannelConfig = channelConfig;

                            return;
                        }

                        mAudioRecord.release();
                        mAudioRecord = null;
                    }
                    catch (Exception e) {
                        // Do nothing
                    }
                }
            }
        }
    }
	
	protected int getSampleRate(String quality){
		if("high".equalsIgnoreCase(quality)){
			return AUDIO_SAMPLE_RATE_HIGH;
		}else if("normal".equalsIgnoreCase(quality)){
			return AUDIO_SAMPLE_RATE_NORMAL;
		}
		return AUDIO_SAMPLE_RATE_LOW;
	}
	
	

	@Override
	public void startRecord(int time, String quality, String outPath) {
		
	}

	@Override
	public void stopRecord() {
		isRecording = false;
	}

	@Override
	public void setOnRecordListener(OnRecordListener onRecordListener) {
		this.onRecordListener = onRecordListener;
	}

}
