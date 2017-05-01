/**
 * 
 */
package com.jahs.glide.launchpoint;

import java.util.ArrayList;

/**
 * @author James Shaw
 * Datastore implements the Singleton pattern.
 */
public class Datastore {

	public static Datastore GetInstance() {
		if (instance == null) {
			instance = new Datastore();
		}
		return instance;
	}
	
	public Pilot GetPilot(int memberNum) {
		// Spin through the list of Pilots to find the one with this member number.
		for (Pilot pilot: clubMembers) {
			if (pilot.GetNumber() == memberNum)
				return pilot;
		}
		return null;
	}
	
	public Pilot GetPilot(String pilotName) {
		// Spin through the list of Pilots to find the one with this exact name.
		for (Pilot pilot: clubMembers) {
			if (pilot.GetName().equalsIgnoreCase(pilotName))
				return pilot;
		}
		return null;
	}
	
	public Glider GetGlider(String gliderReg) {
		// Spin through the list of Gliders to find the one with this registration.
		for (Glider glider: localGliders) {
			if (glider.GetReg().equals(gliderReg))
				return glider;
		}
		return null;
	}
	
	public void AddFlight(Flight flight) {
		todaysFlights.add(flight);
		// set flight number
		for (DataWatcher dw: watchers) {
			dw.onNewFlight(flight);
		}
	}
	
	public Flight GetFlight(int flightNum) {
		Flight flight = null;
		try {
			flight = todaysFlights.get(flightNum);
		}
		catch (IndexOutOfBoundsException exception) {
			// Suppress exception and just return null.
		}
		return flight;
	}
	
	public void Register(DataWatcher watcher) {
		watchers.add(watcher);
	}
	
	public void Unregister(DataWatcher watcher) {
		watchers.remove(watcher);
	}
	
	/**
	 * Constructor for Datastore.  Accessed via GetInstance above.
	 */
	private Datastore() {
		// TODO Read the list from file.
		// Populate the list of members and gliders.
		clubMembers = new ArrayList<Pilot>();
		localGliders = new ArrayList<Glider>();
		todaysFlights = new ArrayList<Flight>();
		watchers = new ArrayList<DataWatcher>();
		
		clubMembers.add(new Pilot("James Shaw", 1304));
		clubMembers.add(new Pilot("Graham Spelman", 1375));
		localGliders.add(new Glider("KFY", "K-21", true, true));
		localGliders.add(new Glider("CU", "ASW-19B", false, true));
	}
	
	private static Datastore instance;
	// TODO JAHS these lists are inefficient - the Pilot list at least should be keyed off member number.
	private ArrayList<Pilot> clubMembers;
	private ArrayList<Glider> localGliders;
	private ArrayList<Flight> todaysFlights;

	private ArrayList<DataWatcher> watchers;
}
