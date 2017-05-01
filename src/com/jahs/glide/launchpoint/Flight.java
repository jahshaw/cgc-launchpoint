/**
 * 
 */
package com.jahs.glide.launchpoint;


import java.util.Date;

/**
 * @author James Shaw 
 *
 */
public class Flight {

	/**
	 * Constructor for Flight.
	 */
	public Flight() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Construct a Flight class from a pre-defined log string.
	 * @param logString
	 */
	@SuppressWarnings("deprecation")
	public Flight(String logString) {
		//TODO JAHS parse the string to get the parameters.
	
		Datastore datastore = Datastore.GetInstance();
		
		String[] log_break = logString.split(",", -1);
		String glider_reg;
		
		int mem_number_p1;
		int mem_number_p2;
		
		glider_reg=log_break[0];
		if (log_break[2].equals("")){
		mem_number_p1=0;}
		else {
		mem_number_p1=Integer.parseInt(log_break[2]);
		}
		
		if (log_break[4].equals("")){
		mem_number_p2=0;}
		else {
		mem_number_p2=Integer.parseInt(log_break[4]);
		}
		
		Pilot magnificent_men=datastore.GetPilot(mem_number_p1);
		Pilot dubious_student=datastore.GetPilot(mem_number_p2);
		Glider flying_machine=datastore.GetGlider(glider_reg); 
		
		if (magnificent_men!=null){
			p1=magnificent_men;
		}
		else{
			p1=new Pilot(log_break[3],mem_number_p1);
		}
	
		if (dubious_student!=null){
			p2=dubious_student;
		}
		else if(mem_number_p2==0){
			p2=null;}
		else {
			p2=new Pilot(log_break[5],mem_number_p2);
		}
	
		if (flying_machine!=null){
		glider=flying_machine;
		}
		else{
			//Not sure how to decide if it's a two-seater for creating a new glider so assume it's not a club glider and it's a two seater to avoid errors
			glider = new Glider(log_break[0], log_break[1], true, false);	
		}
		
		//Set Launch Type
		if (log_break[6].equals("W"))
		{
		launchMethod = Launch.WINCH;
		}
		else if(log_break[6].equals("A"))
		{
		launchMethod = Launch.AEROTOW;
		}
		else if(log_break[6].equals("B"))
		{
		launchMethod = Launch.BUNGEE;	
		}
		
		//Set take off and launch time
		if (log_break[7].length() == 4) {
			takeOffTime = new Date();
			takeOffTime.setHours(Integer.parseInt(log_break[7].substring(0, 2)));
			takeOffTime.setMinutes(Integer.parseInt(log_break[7].substring(2, 4)));
		}
		
		if (log_break[8].length() == 4) {
			landTime = new Date();
			landTime.setHours(Integer.parseInt(log_break[8].substring(0, 2)));
			landTime.setMinutes(Integer.parseInt(log_break[8].substring(2, 4)));
		}
		
		// Set notes
		notes=log_break[9];
	
	}
	
	@SuppressWarnings("deprecation")
	public String GetLogString() {
		// Build a comma-separated string with the flight details.
		//TODO JAHS this is horrible.  Fix it.
		
		String logString = ""; 
		
		if (glider != null) {
			logString += glider.GetReg() + ',' + glider.GetType() + ',';
		}
		else {
			logString += ",,";
		}
		
		if (p1 != null) {
			logString += Integer.toString(p1.GetNumber()) + ',' + p1.GetName() + ',';
		}
		else {
			logString += ",,";
		}
		
		if (p2 != null) {
			logString += Integer.toString(p2.GetNumber()) + ',' + p2.GetName() + ',';
		}
		else {
			logString += ",,";
		}
		
		if (launchMethod != null) {
			if (launchMethod.equals(Launch.AEROTOW)) {
				logString += "A,";
			}
			else if (launchMethod.equals(Launch.WINCH)) {
				logString += "W,";
			}
			else if (launchMethod.equals(Launch.BUNGEE)){
				logString += "B,";
			}
			else {
				logString += ',';
			}
		}
		else {
			logString += ',';
		}
		
		if (takeOffTime != null) {
		
			logString += String.format("%02d", takeOffTime.getHours()) + String.format("%02d", takeOffTime.getMinutes()) + ',';
		
		}
		else {
			logString += ',';
		}
		
		if (landTime != null) {
			logString += String.format("%02d", landTime.getHours()) + String.format("%02d", landTime.getMinutes()) + ',';
		}
		else {
			logString += ',';
		}
			
		if (notes != null) {
			logString += notes;
		}
		logString += ';';

		return logString;		
	}
	
	public void SetGliderbyReg(String log_reg) {
		Datastore datastore = Datastore.GetInstance();
		Glider storedGlider= datastore.GetGlider(log_reg); 
		
		if (storedGlider != null) {
			// Found a glider in the glider database.
			glider = storedGlider;
		}
		else if (glider != null){
			// Registration changed, but keep the type parameter.
			glider = new Glider(log_reg, glider.GetType(), true, false);
		}
		else {
			// Unknown glider - assume two seater.
			glider = new Glider(log_reg, "Unknown", true, false);	
		}
	}
	
	public void SetGliderbyType(String log_type) {
		
		if (glider != null) {
			// Type changed, but keep the other parameters that have been entered.
			glider = new Glider(glider.GetReg(), log_type, glider.IsTwoSeater(), glider.IsClub());
		}
		else {
			// Unknown glider - assume two seater.
			glider = new Glider("Unknown", log_type, true, false);	
		}
	}
	
	public void Setp1ByNumber(int log_p1_number) {
		Datastore datastore = Datastore.GetInstance();
		Pilot pilot = datastore.GetPilot(log_p1_number);
		
		if (pilot != null) {
			// Found a pilot with this number in the datastore.
			p1 = pilot;
		}
		else if (p1 != null) {
			// Pilot exists - create a new pilot with same name but different number.
			p1 = new Pilot(p1.GetName(), log_p1_number);
		}
		else {
			// New pilot - create with default name.
			p1 = new Pilot("", log_p1_number);
		}
	}

	public void Setp1ByName(String log_p1_name) {
		Datastore datastore = Datastore.GetInstance();
		Pilot pilot = datastore.GetPilot(log_p1_name); 

		if (pilot != null) {
			// Found a pilot with this name in the datastore.
			p1 = pilot;
		}
		else if (p1 != null) {
			// Pilot exists - create a new pilot with same number but different name.
			p1 = new Pilot(log_p1_name, p1.GetNumber());
		}
		else {
			// New pilot - create with default number.
			p1 = new Pilot(log_p1_name, 0);				
		}
	}

	public void Setp2ByNumber(int log_p2_number) {
		Datastore datastore = Datastore.GetInstance();
		Pilot pilot = datastore.GetPilot(log_p2_number);
		
		if (pilot != null) {
			// Found a pilot with this number in the datastore.
			p2 = pilot;
		}
		else if (p2 != null) {
			// Pilot exists - create a new pilot with same name but different number.
			p2 = new Pilot(p2.GetName(), log_p2_number);
		}
		else {
			// New pilot - create with default name.
			p2 = new Pilot("", log_p2_number);
		}
	}

	public void Setp2ByName(String log_p2_name) {
		Datastore datastore = Datastore.GetInstance();
		Pilot pilot = datastore.GetPilot(log_p2_name); 

		if (pilot != null) {
			// Found a pilot with this name in the datastore.
			p2 = pilot;
		}
		else if (p2 != null) {
			// Pilot exists - create a new pilot with same number but different name.
			p2 = new Pilot(log_p2_name, p2.GetNumber());
		}
		else {
			// New pilot - create with default number.
			p2 = new Pilot(log_p2_name, 0);				
		}
	}
		
	//Edit launchMethod
	public void SetLaunchMethod(String bungeeing_is_best){
		
		if (bungeeing_is_best.equals("W"))
		{
			launchMethod = Launch.WINCH;
		}
		else if(bungeeing_is_best.equals("A"))
		{
			launchMethod = Launch.AEROTOW;
		}
		else if(bungeeing_is_best.equals("B"))
		{
			launchMethod = Launch.BUNGEE;	
		}
	}		

//Edit takeOffTime

@SuppressWarnings("deprecation")
public void SetTakeOffTime(String log_take_off){
	takeOffTime = new Date();
	takeOffTime.setHours(Integer.parseInt(log_take_off.substring(0, 2)));
	takeOffTime.setMinutes(Integer.parseInt(log_take_off.substring(2, 4)));
	
}


//Edit landTime

@SuppressWarnings("deprecation")
public void SetLandTime(String log_land){
	landTime = new Date();
	landTime.setHours(Integer.parseInt(log_land.substring(0, 2)));
	landTime.setMinutes(Integer.parseInt(log_land.substring(2, 4)));
}


//Edit notes

public void SetNotes(String log_notes){
	notes=log_notes;
}
	
	/**
	 * Internal parameters to the application.
	 */
	//private int flightNum;
	
	public Glider getGlider() {
		return glider;
	}
	
	public Pilot getP1() {
		return p1;
	}
	
	public Pilot getP2() {
		return p2;
	}
	
	public Launch getLaunchMethod() {
		return launchMethod;
	}
	
	public Date getTakeOffTime() {
		return takeOffTime;
	}
	
	public Date getLandTime() {
		return landTime;
	}
	
	public String getNotes() {
		return notes;
	}

	/**
	 * Parameters corresponding to details entered on the log.
	 */
	private Glider glider;
	private Pilot p1;
	private Pilot p2;
	private Launch launchMethod;
	private Date takeOffTime;
	private Date landTime;
	private String notes;
}
