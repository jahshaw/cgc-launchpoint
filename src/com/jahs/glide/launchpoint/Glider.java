package com.jahs.glide.launchpoint;

import java.util.ArrayList;
import java.util.List;


public class Glider {

	private String glider_type;
	private String glider_reg;

	
	private boolean glider_two_seater;
	private boolean glider_club;
	private ArrayList<Pilot> owners;
	
   	
	public Glider(String entered_reg, String entered_type, boolean two_seater, boolean club) {
		
		
		glider_type=entered_type;
		glider_reg=entered_reg;
		glider_two_seater=two_seater;
		glider_club=club;
		owners = new ArrayList<Pilot>();
	}

	
	public String GetType(){
		
		return glider_type;
					
	}
	
	public String GetReg(){
		
		return glider_reg;
	}
	
	public boolean IsTwoSeater(){
	
		return glider_two_seater;
	}

	public boolean IsClub(){
		
		return glider_club;
	}
	
	public List<Pilot> GetOwners(){
	
		return owners;
		
	}
	
	public void AddOwner(Pilot new_owner){
			
		owners.add(new_owner);
	}	
	
	
}