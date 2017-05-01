package com.jahs.glide.launchpoint;

import java.util.ArrayList;
import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class LogActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	private LogFile logFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log);
		
		// Initialize the LogFile if we don't already have one.
		logFile = new LogFile(this);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		logFile.StartDataTimer();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		logFile.StopDataTimer();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			switch (position) {
			case 0:
				return new LogFileFragment();
			case 1:
				return new LaunchViewFragment();
			case 2:
				return new AboutFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.log_file_section).toUpperCase(l);
			case 1:
				return getString(R.string.launch_view_section).toUpperCase(l);
			case 2:
				return getString(R.string.about_section).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class LogFileFragment extends Fragment implements DataWatcher {
		
		private static final String TAG = "LogFileFragment";

		private ArrayList<FlightTableRow> ftr;
		
		public LogFileFragment() {
			ftr = new ArrayList<FlightTableRow>();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_log_file,
					container, false);
			Log.i(TAG, "OnCreateView called");
			
			LinearLayout table = (LinearLayout)rootView.findViewById(R.id.logTable);
			TableRow row;
			
			// Query the Datastore for existing Flights and add them as FlightTableRows.
			Datastore datastore = Datastore.GetInstance();
			int flightNum = 0;
			Flight flight = datastore.GetFlight(flightNum);
			while (flight != null) {
				row = (TableRow)inflater.inflate(R.layout.log_row, table, false);
				ftr.add(new FlightTableRow(row, flight));
				table.addView(row);
				flightNum++;
				flight = datastore.GetFlight(flightNum);
			}
			
			Log.i(TAG, "Added " + flightNum + " flights");
			
			// Register with the Datastore for callbacks.
			datastore.Register(this);
			
			// Add one more empty FlightTableRow.
			// TODO JAHS I think the 3rd parameter should be true, but that gives a TableLayout instead.
			row = (TableRow)inflater.inflate(R.layout.log_row, table, false);
			ftr.add(new FlightTableRow(row));
			table.addView(row);
						
			return rootView;
		}
		
		@Override
		public void onResume() {
			Log.i(TAG, "onResume called");
			super.onResume();
			
			for (FlightTableRow row: ftr) {
				row.WriteToRow();
				row.RegisterCallbacks();
			}
		}
		
		public void onPause() {
			Log.i(TAG, "onPause called");

			for (FlightTableRow row: ftr) {
				row.UnregisterCallbacks();
			}
			super.onPause();			
		}
		
		@Override
		public void onDestroyView() {
			// Unregister from the Datastore.
			Log.i(TAG, "onDestroyView called");
			Datastore datastore = Datastore.GetInstance();
			datastore.Unregister(this);
			
			for (FlightTableRow row: ftr) {
				row.UnregisterCallbacks();
			}
			//LinearLayout table = (LinearLayout)rootView.findViewById(R.id.logTable);
			//table.removeAllViews();
			
			super.onDestroyView();
		}

		/*
		 * (non-Javadoc)
		 * @see com.jahs.glide.launchpoint.DataWatcher#onNewFlight(com.jahs.glide.launchpoint.Flight)
		 */
		@Override
		public void onNewFlight(Flight flight) {
			// Inflate another FlightTableRow and add it to the view.
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			TableLayout table = (TableLayout)rootView.findViewById(R.id.logTable);
			TableRow row = (TableRow)inflater.inflate(R.layout.log_row, table, false);
			FlightTableRow flightTableRow = new FlightTableRow(row);
			ftr.add(flightTableRow);
			table.addView(row);
			flightTableRow.RegisterCallbacks();
		}
		
		private View rootView;
	}

	/**
	 * Fragment for the launchpoint view.
	 */
	public static class LaunchViewFragment extends Fragment {

		public LaunchViewFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_launch_view,
					container, false);

			return rootView;
		}
	}
	
	/**
	 * Fragment for the about page.
	 */
	public static class AboutFragment extends Fragment {

		public AboutFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_about,
					container, false);

			return rootView;
		}
	}
}
