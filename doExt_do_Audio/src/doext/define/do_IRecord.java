package doext.define;


public interface do_IRecord {
	
	interface OnRecordListener {
		
		void onStart();
		
		void onRecordTimeChange(long totalTimeMillis);
		
		void onError();
		
		void onFinished();
	}
	
	void startRecord (int time, String quality, String outPath);
	
	void stopRecord();
	
	void setOnRecordListener (OnRecordListener onRecordListener);
	
}
