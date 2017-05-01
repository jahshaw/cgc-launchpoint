/**
 * 
 */
package com.jahs.glide.launchpoint;

import java.util.ArrayList;
import java.util.Date;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;

/**
 * @author James Shaw
 *
 */
public class FlightTableRow {

	/**
	 * @param context
	 */
	public FlightTableRow(TableRow newRow) {
		row = newRow;
		list = new ArrayList<LogRowWatcher>();
	}
	
	public FlightTableRow(TableRow newRow, Flight existingFlight) {
		row = newRow;
		flight = existingFlight;
		list = new ArrayList<LogRowWatcher>();
	}
	
	@SuppressWarnings("deprecation")
	public void WriteToRow() {
		
		// If we don't have flight data we can't do much; just drop out.
		if (flight == null) {
			return;
		}
		
		// Setup the fields from the logged data.
		Glider glider = flight.getGlider();
		if (glider != null) {
			((EditText)row.findViewById(R.id.edit_registration)).setText(glider.GetReg());
			if (!glider.IsTwoSeater()) {
				DisableP2();
			}
		}
		
		Pilot p1 = flight.getP1();
		if (p1 != null) {
			if (p1.GetNumber() != 0) {
				((EditText)row.findViewById(R.id.edit_p1Num)).setText(Integer.toString(p1.GetNumber()));
			}
			((EditText)row.findViewById(R.id.edit_p1Name)).setText(p1.GetName());
		}
		
		Pilot p2 = flight.getP2();
		if (p2 != null) {
			assert(glider.IsTwoSeater());
			if (p2.GetNumber() != 0) {
				((EditText)row.findViewById(R.id.edit_p2Num)).setText(Integer.toString(p2.GetNumber()));
			}
			((EditText)row.findViewById(R.id.edit_p2Name)).setText(p2.GetName());
		}
		
		Date takeOff = flight.getTakeOffTime();
		if (takeOff != null) {
			((EditText)row.findViewById(R.id.edit_toTime)).setText(String.format("%02d", takeOff.getHours()) + String.format("%02d", takeOff.getMinutes()));
		}
		
		Date land = flight.getLandTime();
		if (land != null) {
			((EditText)row.findViewById(R.id.edit_landTime)).setText(String.format("%02d" ,land.getHours()) + String.format("%02d" ,land.getMinutes()));
		}
		
		Launch lm = flight.getLaunchMethod();
		if (lm == Launch.AEROTOW) {
			((EditText)row.findViewById(R.id.edit_launch)).setText("A");
		}
		else if (lm == Launch.WINCH) {
			((EditText)row.findViewById(R.id.edit_launch)).setText("W");
		}
		else if (lm == Launch.BUNGEE) {
			((EditText)row.findViewById(R.id.edit_launch)).setText("B");
		}		
		((EditText)row.findViewById(R.id.edit_notes)).setText(flight.getNotes());
	}
	
	public void RegisterCallbacks() {
		procCallback = false;
		
		// Add listeners to all the EditTexts.
		for (int viewNum = 0; viewNum < row.getChildCount(); viewNum++) {
			EditText editText = (EditText)row.getChildAt(viewNum);
			LogRowWatcher lrw = new LogRowWatcher(editText);
			list.add(viewNum, lrw);
			editText.addTextChangedListener(lrw);
		}
	}
	
	public void UnregisterCallbacks() {
		// TODO remove callbacks when going out of focus.
		for (int viewNum = 0; viewNum < row.getChildCount(); viewNum++) {
			try {
				EditText editText = (EditText)row.getChildAt(viewNum);
				editText.removeTextChangedListener(list.get(viewNum));				
			}
			catch (IndexOutOfBoundsException e) {
				// Just ignore this and continue trying to remove the next listener.
			}
		}
	}
	
	private void EnableP2() {
		EditText p2NumText = ((EditText)row.findViewById(R.id.edit_p2Num));
		EditText p2NameText = ((EditText)row.findViewById(R.id.edit_p2Name));		
		p2NumText.setClickable(true);
		p2NumText.setFocusable(true);
		p2NumText.setBackgroundColor(0xFFFFFF);
		p2NameText.setClickable(true);
		p2NameText.setFocusable(true);
		p2NameText.setBackgroundColor(0xFFFFFF);
		// TODO redraw EditText fields at this point		
	}
	
	private void DisableP2() {
		EditText p2NumText = ((EditText)row.findViewById(R.id.edit_p2Num));
		EditText p2NameText = ((EditText)row.findViewById(R.id.edit_p2Name));
		p2NumText.setClickable(false);
		p2NumText.setFocusable(false);
		p2NumText.setBackgroundColor(0xCCCCCC);
		p2NumText.setText("");
		p2NameText.setClickable(false);
		p2NameText.setFocusable(false);
		p2NameText.setBackgroundColor(0xCCCCCC);
		p2NameText.setText("");
	}
	
	public class LogRowWatcher implements TextWatcher {

		private static final String TAG = "LogRowWatcher";

		/**
		 * LogRowWatcher implementation
		 */
		public LogRowWatcher(View view) {
			watchedView = view;
		}

		/* (non-Javadoc)
		 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
		 */
		@Override
		public void afterTextChanged(Editable textField) {
			Log.i(TAG, "Callback called for " + watchedView.getId());
			
			// If we're already processing a callback on this FlightTableRow, ignore this one.
			if (procCallback == true) {
				 return;
			}
			
			procCallback = true;
			
			// If no Flight currently exists for this row, create one now.
			if (flight == null) {
				Datastore datastore = Datastore.GetInstance();
				flight = new Flight();
				Log.i(TAG, "Adding new flight");
				datastore.AddFlight(flight);
			}
			
			// Switch on the ID of this control to determine what to do with the Editable.
			switch(watchedView.getId()) {
				case R.id.edit_registration:
					flight.SetGliderbyReg(textField.toString());
					// TODO JAHS implement various popups from this method.
					
					// If this is a two seater, enable the P2 fields.  Otherwise, disable and clear them.
					if (flight.getGlider().IsTwoSeater()) {
						EnableP2();
					}
					else {
						DisableP2();
					}
					break;
					
				case R.id.edit_p1Num:
					try {
						flight.Setp1ByNumber(Integer.parseInt(textField.toString()));
					}
					catch (NumberFormatException e) {
						flight.Setp1ByNumber(0);
					}
					((EditText)row.findViewById(R.id.edit_p1Name)).
						setText(flight.getP1().GetName());
					break;		
					
				case R.id.edit_p1Name:
					flight.Setp1ByName(textField.toString());
					((EditText)row.findViewById(R.id.edit_p1Num)).
						setText(Integer.toString(flight.getP1().GetNumber()));
					
				/*	if (textField.toString().equalsIgnoreCase(flight.getP1().GetName()))
					{
					((EditText)row.findViewById(R.id.edit_p1Name)).
						setText(flight.getP1().GetName());
										}*/
					
					break;
					
				case R.id.edit_p2Num:
					try {
						flight.Setp2ByNumber(Integer.parseInt(textField.toString()));
					}
					catch (NumberFormatException e) {
						flight.Setp2ByNumber(0);
					}
					((EditText)row.findViewById(R.id.edit_p2Name)).
						setText(flight.getP2().GetName());					
					break;		
					
				case R.id.edit_p2Name:
					flight.Setp2ByName(textField.toString());
					((EditText)row.findViewById(R.id.edit_p2Num)).
						setText(Integer.toString(flight.getP2().GetNumber()));					
				/*	((EditText)row.findViewById(R.id.edit_p2Name)).
						setText(flight.getP2().GetName());
					((EditText)row.findViewById(R.id.edit_p2Name)).
						setSelection(flight.getP2().GetName().length());
				*/	break;
					
				case R.id.edit_launch:
					flight.SetLaunchMethod(textField.toString());
					break;
					
				case R.id.edit_toTime:
					if (textField.length() == 4) {
						flight.SetTakeOffTime(textField.toString());
					}
					break;
					
				case R.id.edit_landTime:
					if (textField.length() == 4) {
						flight.SetLandTime(textField.toString());
					}
					break;
					
				case R.id.edit_notes:
					flight.SetNotes(textField.toString());
					break;
					
				default:
					//throw new Exception("Callback to non-existent EditText");
			}
			
			// No longer processing this callback.
			procCallback = false;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
		
		private View watchedView;		
	}
	
	private boolean procCallback;
	private TableRow row;
	private Flight flight;
	private ArrayList<LogRowWatcher> list;
}
