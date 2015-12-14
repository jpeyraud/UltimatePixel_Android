package com.altarrys.ultimatepixel;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;


public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
	public static String PREFERENCES_ID = "myPref";

	private static final String TAG = "TrivialQuest";

	// Request code used to invoke sign in user interactions.
	private static final int RC_SIGN_IN = 9001;

	// Client used to interact with Google APIs.
	private GoogleApiClient mGoogleApiClient;

	// Request code to use when launching the resolution activity
	private static final int REQUEST_RESOLVE_ERROR = 1001;
	// Unique tag for the error dialog fragment
	private static final String DIALOG_ERROR = "dialog_error";
	// Bool to track whether the app is already resolving an error
	private boolean mResolvingError = false;

	//-----------------------------------------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the Google Api Client with access to Plus and Games
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
				.addApi(Games.API).addScope(Games.SCOPE_GAMES)
				.build();


		if (savedInstanceState == null) 
		{
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		//Games.signOut(mGoogleApiClient);
		//mGoogleApiClient.disconnect();
	}

	//-----------------------------------------------------------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements View.OnTouchListener
	{
		public PlaceholderFragment() 
		{

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
		{
			View rootView = inflater.inflate(R.layout.fragment_main, container,	false);
			
			// Set OnClickListener for all buttons
			((Button) rootView.findViewById(R.id.playButton)).setOnTouchListener(this);
			((Button) rootView.findViewById(R.id.ScoreButton)).setOnTouchListener(this);
			
			return rootView;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			Log.d("TAG", "on press");
			if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				// put on pressed background on press
			//	if (v.getId() == R.id.playButton)
			//		((Button) getActivity().findViewById(R.id.playButton)).setBackgroundResource(R.drawable.blue_pressed_button);
			//	else if (v.getId() == R.id.modeArcadeButton)
			//		((Button) getActivity().findViewById(R.id.modeArcadeButton)).setBackgroundResource(R.drawable.red_pressed_button);
			//	else if (v.getId() == R.id.ScoreButton)
			//		((Button) getActivity().findViewById(R.id.ScoreButton)).setBackgroundResource(R.drawable.green_pressed_button);
			}
			else if (event.getAction() == MotionEvent.ACTION_UP)
			{
				// come back to original background on release
				//((Button) getActivity().findViewById(R.id.playButton)).setBackgroundResource(R.drawable.blue_button);
				//((Button) getActivity().findViewById(R.id.modeArcadeButton)).setBackgroundResource(R.drawable.red_button);
				//((Button) getActivity().findViewById(R.id.ScoreButton)).setBackgroundResource(R.drawable.green_button);

				if (v.getId() == R.id.playButton)
				{
					Intent intent = new Intent(getActivity(), AGameEngine.class);

					// Add the index of the checked hardness to the intent
					intent.putExtra("hardness", "hard");//((RadioGroup)getActivity().findViewById(R.id.Hardness)).getCheckedRadioButtonId());

					startActivity(intent);
					return true;
				}
				else if (v.getId() == R.id.ScoreButton)
				{
					Intent intent = new Intent(getActivity(), AScore.class);

					//startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,LEADERBOARD_ID), REQUEST_LEADERBOARD);

					startActivity(intent);
				}
			}

			return true;
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------------
	@Override
	protected void onStart() {
		super.onStart();
		if (!mResolvingError) {  // more about this later
			mGoogleApiClient.connect();
		}
	}

	@Override
	protected void onStop() {
		mGoogleApiClient.disconnect();
		super.onStop();
	}

	// Shows the "sign in" bar (explanation and button).
	private void showSignInBar() {
		Log.d(TAG, "Showing sign in bar");
		//findViewById(R.id.sign_in_bar).setVisibility(View.VISIBLE);
		//findViewById(R.id.sign_out_bar).setVisibility(View.GONE);
	}

	// Shows the "sign out" bar (explanation and button).
	private void showSignOutBar() {
		Log.d(TAG, "Showing sign out bar");
		//findViewById(R.id.sign_in_bar).setVisibility(View.GONE);
		//findViewById(R.id.sign_out_bar).setVisibility(View.VISIBLE);
	}


	@Override
	public void onConnected(Bundle bundle) {
		Log.d(TAG, "onConnected() called. Sign in successful!");
		showSignOutBar();
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d(TAG, "onConnectionSuspended() called. Trying to reconnect.");
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (mResolvingError) {
			// Already attempting to resolve an error.
			return;
		} else if (result.hasResolution()) {
			try {
				mResolvingError = true;
				result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
			} catch (IntentSender.SendIntentException e) {
				// There was an error with the resolution intent. Try again.
			}
		} else {
			// Show dialog using GoogleApiAvailability.getErrorDialog()
			showErrorDialog(result.getErrorCode());
			mResolvingError = true;
		}
	}
	// The rest of this code is all about building the error dialog

	/* Creates a dialog for an error message */
	private void showErrorDialog(int errorCode) {
		// Create a fragment for the error dialog
		ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
		// Pass the error that should be displayed
		Bundle args = new Bundle();
		args.putInt(DIALOG_ERROR, errorCode);
		dialogFragment.setArguments(args);
		dialogFragment.show(getFragmentManager(), "errordialog");
	}

	/* Called from ErrorDialogFragment when the dialog is dismissed. */
	public void onDialogDismissed() {
		mResolvingError = false;
	}

	/* A fragment to display an error dialog */
	public static class ErrorDialogFragment extends DialogFragment {
		public ErrorDialogFragment() { }

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Get the error code and retrieve the appropriate dialog
			int errorCode = this.getArguments().getInt(DIALOG_ERROR);
			return GoogleApiAvailability.getInstance().getErrorDialog(
					this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			((MainActivity) getActivity()).onDialogDismissed();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_RESOLVE_ERROR) {
			mResolvingError = false;
			if (resultCode == RESULT_OK) {
				// Make sure the app is not already connected or attempting to connect
				if (!mGoogleApiClient.isConnecting() &&
						!mGoogleApiClient.isConnected()) {
					mGoogleApiClient.connect();
				}
			}
		}
	}
}
