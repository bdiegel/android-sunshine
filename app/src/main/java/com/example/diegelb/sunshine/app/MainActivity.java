package com.example.diegelb.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.diegelb.sunshine.app.sync.SunshineSyncAdapter;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String DETAIL_FRAGMENT_TAG = "DETAIL_FRAGMENT_TAG";

    private boolean mTwoPane;

    String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLocation = Utility.getPreferredLocation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        // determine if we are using master-detail flow:
//        mTwoPane = (findViewById(R.id.weather_detail_container) == null) ? false : true;
//
//        // add DetailFragment in master-flow:
//        if (mTwoPane) {
//            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction()
//                    .add(R.id.weather_detail_container, new DetailFragment(), DETAIL_FRAGMENT_TAG)
//                    .commit();
//            }
//        }
        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                      .replace(R.id.weather_detail_container, new DetailFragment(), DETAIL_FRAGMENT_TAG)
                      .commit();
            }
        } else {
            mTwoPane = false;
            //getSupportActionBar().setElevation(0f);
        }

        ForecastFragment forecastFragment = ((ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast));
        forecastFragment.setUseTodayLayout(!mTwoPane);

        // initialize the sync adapter
        SunshineSyncAdapter.initializeSyncAdapter(this);

        Log.d(LOG_TAG, "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        String locationSetting = Utility.getPreferredLocation(this);
        if (locationSetting != null && !locationSetting.equals(mLocation)) {
            mLocation = locationSetting;
            ForecastFragment forecastFragment = (ForecastFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if (forecastFragment != null)
                forecastFragment.onLocationChanged();
            DetailFragment detailFragment = (DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
            if (detailFragment != null) {
                detailFragment.onLocationChanged(mLocation);
            }
        }
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       switch (id) {
           case R.id.action_settings: {
               startActivity(new Intent(this, SettingsActivity.class));
               break;
           }
//           case R.id.action_viewmap: {
//               showPreferredLocation();
//               break;
//           }
       }

        return super.onOptionsItemSelected(item);
    }

//    public void showPreferredLocation() {
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//        String postcode = sharedPref.getString(getString(R.string.pref_location_key),
//                getString(R.string.pref_location_default));
//
//        Uri uri = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", postcode).build();
//
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(uri);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        } else {
//            Log.d(LOG_TAG, "Could not call: " + uri );
//        }
//    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                  .replace(R.id.weather_detail_container, fragment, DETAIL_FRAGMENT_TAG)
                  .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).setData(contentUri);
            startActivity(intent);
        }
    }

}
