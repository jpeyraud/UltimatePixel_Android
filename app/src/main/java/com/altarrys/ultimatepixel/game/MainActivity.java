package com.altarrys.ultimatepixel.game;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.altarrys.ultimatepixel.R;
import com.altarrys.ultimatepixel.opengl.MenuGLSurface;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.plus.Plus;



public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
	public static String PREFERENCES_ID = "myPref";

	private static final String TAG = "MainActivity";



	// Client used to interact with Google APIs.
	private GoogleApiClient mGoogleApiClient;

	// Request code to use when launching the iResolution activity
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

		// Create the Google Api Client with access to Plus and Games
		/*mGoogleApiClient = new GoogleApiClient.Builder(this)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
			.addApi(Games.API).addScope(Games.SCOPE_GAMES)
			.build();*/

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);



		//mGlMenuBackground.setZOrderOnTop(true);

		//setContentView(mGlMenuBackground);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null)
		{
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------
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
	GoogleApiClient getGoogleApiClient(){
		return mGoogleApiClient;
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	@Override
	protected void onStart() {
		super.onStart();
		if (!mResolvingError) {  // more about this later
			Log.d(TAG, "pending connection");
			//mGoogleApiClient.connect();
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	@Override
	protected void onStop() {
		//mGoogleApiClient.disconnect();
		super.onStop();
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.d(TAG, "onConnected() called. Sign in successful!");
	}

	//-----------------------------------------------------------------------------------------------------------------------------
	@Override
	public void onConnectionSuspended(int i) {
		Log.d(TAG, "onConnectionSuspended() called. Trying to reconnect.");
		//mGoogleApiClient.connect();
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	@Override
	public void onConnectionFailed(ConnectionResult result)
	{
		Log.d(TAG, "onConnectionFailed() called:" + result);
		if (mResolvingError)
		{
			Log.d(TAG, "resolving");
			// Already attempting to resolve an error.
			return;
		}
		else if (result.hasResolution())
		{
			try
			{
				Log.d(TAG, "try resolving");
				mResolvingError = true;
				result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
			}
			catch (Exception e) //(IntentSender.SendIntentException e)
			{
				// There was an error with the iResolution intent. Try again.
				Log.d(TAG, "error resolving: "+ e);
			}
		}
		else
		{
			Log.d(TAG, "show error");
			// Show dialog using GoogleApiAvailability.getErrorDialog()
			showErrorDialog(result.getErrorCode());
			mResolvingError = true;
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	// building the error dialog
	/* Creates a dialog for an error message */
	private void showErrorDialog(int errorCode)
	{
		Log.d(TAG, "ERROR DIALOG");
		// Create a fragment for the error dialog
		ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
		// Pass the error that should be displayed
		Bundle args = new Bundle();
		args.putInt(DIALOG_ERROR, errorCode);
		dialogFragment.setArguments(args);
		dialogFragment.show(getFragmentManager(), "errordialog");
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	/* Called from ErrorDialogFragment when the dialog is dismissed. */
	public void onDialogDismissed() {
		mResolvingError = false;
	}

	/* A fragment to display an error dialog */
	public static class ErrorDialogFragment extends DialogFragment
	{
		public ErrorDialogFragment() { }

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			// Get the error code and retrieve the appropriate dialog
			int errorCode = this.getArguments().getInt(DIALOG_ERROR);
			return GoogleApiAvailability.getInstance().getErrorDialog(this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
		}

		@Override
		public void onDismiss(DialogInterface dialog)
		{
			((MainActivity) getActivity()).onDialogDismissed();
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*Log.d(TAG, "onActivityResult");
		if (requestCode == REQUEST_RESOLVE_ERROR)
		{
			mResolvingError = false;
			Log.d(TAG, "result code:"+resultCode);
			if (resultCode == RESULT_OK)
			{
				Log.d(TAG, "result ok");
				// Make sure the app is not already connected or attempting to connect
				if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected())
				{
					Log.d(TAG, "reconnect");
					mGoogleApiClient.connect();
				}
			}
			else if (resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED)
			{
				Log.d(TAG, "Reconnection required");
				mGoogleApiClient.connect();
			}
			else if (resultCode == GamesActivityResultCodes.RESULT_SIGN_IN_FAILED)
			{
				Log.d(TAG, "sign in failed");
				mGoogleApiClient.connect();
			}
		}*/
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements View.OnTouchListener
	{
		private MenuGLSurface mGlMenuBackground;

		public PlaceholderFragment()
		{

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_main, container,	false);

			// Add opengl background
			mGlMenuBackground = new MenuGLSurface(this.getActivity(), R.raw.cubes_frag_shader);
			((FrameLayout)rootView.findViewById(R.id.main_fragment_framelayout)).addView(mGlMenuBackground,0);

			// Set OnClickListener for all buttons
			Button ButtonPlay = (Button) rootView.findViewById(R.id.playButton);
			Button ButtonScore = (Button) rootView.findViewById(R.id.ScoreButton);
			ButtonPlay.setOnTouchListener(this);
			ButtonScore.setOnTouchListener(this);

			return rootView;
		}
		//-----------------------------------------------------------------------------------------------------------------------------
		@Override
		public void onResume() {
			super.onResume();
			mGlMenuBackground.onResume();
		}
		//-----------------------------------------------------------------------------------------------------------------------------
		@Override
		public void onPause() {
			super.onPause();
			mGlMenuBackground.onPause();
		}

		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			Log.d(TAG, "on press");
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
					//GoogleApiClient googleApi = ((MainActivity)getActivity()).getGoogleApiClient();

					//if (googleApi.isConnected())
						//startActivityForResult(Games.Leaderboards.getLeaderboardIntent(googleApi,getString(R.string.leaderboard_id)), 1224);
				}
			}

			return true;
		}
	}
}
