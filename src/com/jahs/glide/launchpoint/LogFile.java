package com.jahs.glide.launchpoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

public class LogFile {

	private FileOutputStream logFileStream;
	private String fileName;
	private CountDownTimer timer;
	private Context context;
	private static String TAG = "LogFile";
	
	public LogFile(Context context) {
		this.context = context;
		Datastore datastore = Datastore.GetInstance();

		// Get today's date and check for an existing log file.
		GregorianCalendar calendar = new GregorianCalendar();
		fileName = "CGC_Log_" + calendar.get(Calendar.DAY_OF_MONTH) + 
				                calendar.get(Calendar.MONTH) + 
				                calendar.get(Calendar.YEAR) + ".log";
		
		// If we already have flights, don't bother reading any new ones into the database.
		if (datastore.GetFlight(0) != null) {
			return;
		}
		
		File logFile = new File(context.getFilesDir().getPath(), fileName);
		
		if (logFile.exists()) {
			// Read entries from the log file into local memory.
			String fileContents = "";
			try {
				BufferedReader reader = new BufferedReader(new FileReader(logFile));
				fileContents = reader.readLine();
				reader.close();
			}
			catch (Exception e) {
				// TODO JAHS improve this
				Log.e(TAG, "Crap!  Error in LogFile()");
				e.printStackTrace();
			}
			
			if (fileContents != null) {
				Log.i(TAG, "Log file contains " + fileContents);
				String[] logLines = fileContents.split(";");
				
				for (String line: logLines) {
					Log.i(TAG, "Creating flight from " + line);
					datastore.AddFlight(new Flight(line));
				}
			}
		}
	}
	
	/**
	 * Writes all the current log data to today's log file.  Could perhaps be improved to only
	 * write data when updates are required.
	 */
	public void WriteData()	{
		try {
			Log.i(TAG, "Opening file with name " + fileName);
			logFileStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			int i = 0;
			Flight flight;
			Datastore datastore = Datastore.GetInstance();
			
			// Spin through all the flights in the datastore, writing them all to the log.
			flight = datastore.GetFlight(i);
			while (flight != null) {
				String logString = flight.GetLogString();
				Log.i(TAG, "Writing log string " + logString);
				logFileStream.write(logString.getBytes());
				i++;
				flight = datastore.GetFlight(i);
			}
			
			logFileStream.close();
		}
		catch (Exception e)	{
			//TODO JAHS handle exception better	
			Log.e(TAG, "Crap!  Error in WriteData() " + e.getMessage());
			e.printStackTrace();
		}
    }
	
	public void StartDataTimer() {
		timer = new DataTimer(60000, 60000);
		timer.start();
	}
	
	public void StopDataTimer() {
		timer.cancel();
		WriteData();
	}
	
	public class DataTimer extends CountDownTimer {

		public DataTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}
		
		@Override
		public void onFinish() {
			LogFile.this.WriteData();
			LogFile.this.StartDataTimer();
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}		
	}

}
