package com.altarrys.ultimatepixel.game;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.altarrys.ultimatepixel.R;

import java.util.List;

public class AScoreScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_screen);
    }

    public void backToMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void shareOnFacebook(View view) {
        String urlToShare = "OMG";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        // intent.putExtra(Intent.EXTRA_SUBJECT, "Foo bar"); // NB: has no effect!
        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);

        // See if official Facebook app is found
        boolean facebookAppFound = false;
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) { //orca  katana
                intent.setPackage(info.activityInfo.packageName);
                facebookAppFound = true;
                break;
            }
        }
        // As fallback, launch sharer.php in a browser
        if (!facebookAppFound) {
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
        }

        startActivity(intent);
    }

    public void shareOnTwitter(View view) {
        String urlToShare = "TESTESTESTESTESTEST";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        // intent.putExtra(Intent.EXTRA_SUBJECT, "Foo bar"); // NB: has no effect!
        intent.putExtra(Intent.EXTRA_TEXT, urlToShare);

        // See if official Facebook app is found
        boolean twitterAppFound = false;
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter.android")) {
                intent.setPackage(info.activityInfo.packageName);
                twitterAppFound = true;
                break;
            }
        }
        // As fallback, launch sharer.php in a browser
        if (!twitterAppFound) {
            String sharerUrl = "http://twitter.com/share?text=" + urlToShare;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
        }

        startActivity(intent);
    }

    public void shareOnGooglePlus(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "The status update text");
        startActivity(Intent.createChooser(intent, "Dialog title text"));
    }

}
