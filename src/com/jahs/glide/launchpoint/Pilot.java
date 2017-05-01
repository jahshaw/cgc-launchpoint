package com.jahs.glide.launchpoint;

public class Pilot {

	private String pilot_name;
	private int pilot_number;
	
	public Pilot(String entered_name, int entered_number) {
		// Create awesome pilot.
	
		pilot_name=entered_name;
		pilot_number=entered_number;	
		
	}
	
	public String GetName(){
		
		return pilot_name;
	}
	
	
	public int GetNumber(){
		
		return pilot_number;
	}
	
	
}