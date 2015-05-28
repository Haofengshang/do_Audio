package doext.define;


public interface do_IRecord {
	
	interface OnRecordTimeChangeListener {
		void onRecordTimeChange(long totalTimeMillis);
	}
	
	void startRecord (int time, String quality, String outPath);
	
	void stopRecord();
	
	void setOnRecordTimeChangeListener (OnRecordTimeChangeListener onRecordTimeChangeListener);
	
}
