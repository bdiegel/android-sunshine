package com.example.diegelb.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.diegelb.sunshine.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String mForecast;

    private ShareActionProvider mShareActionProvider;

    private ImageView mIconView;
    private TextView mDayTv;
    private TextView mDateTv;
    private TextView mHighTv;
    private TextView mLowTv;
    private TextView mForecastTv;
    private TextView mHumidityTv;
    private TextView mWindTv;
    private TextView mPressureTv;

    private static final int DETAILS_LOADER = 1;

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private static final String[] FORECAST_COLUMNS = {
          WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
          WeatherContract.WeatherEntry.COLUMN_DATE,
          WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
          WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
          WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
          WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
          WeatherContract.WeatherEntry.COLUMN_PRESSURE,
          WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
          WeatherContract.WeatherEntry.COLUMN_DEGREES,
          WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
          WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_PRESSURE = 6;
    private static final int COL_WEATHER_WIND_SPEED = 7;
    private static final int COL_WEATHER_DEGREES = 8;
    private static final int COL_WEATHER_WEATHER_ID = 9;
    private static final int COL_LOCATION_SETTING = 10;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflate the fragment menu
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem shareItem = menu.findItem(R.id.action_share);

        // Now get the ShareActionProvider from the item
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createtShareIntent());
        } else {
            Log.d(LOG_TAG, "Problem finding ShareActionProvider");
            //shareActionProvider = new ShareActionProvider(getActivity());
            //MenuItemCompat.setActionProvider(shareItem, shareActionProvider);
        }


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mForecast = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        mIconView = (ImageView) rootView.findViewById(R.id.list_item_icon);
        mDayTv = (TextView) rootView.findViewById(R.id.detail_day_textview);
        mDateTv = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mHighTv = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowTv = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mForecastTv = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mHumidityTv = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mWindTv = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mPressureTv = (TextView) rootView.findViewById(R.id.detail_pressure_textview);

        return rootView;
    }

    /**
     * Loaders are initialized here (not when fragment is created) because
     * their lifecycle is bound to the activity
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAILS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
              getActivity(),
              intent.getData(),
              FORECAST_COLUMNS,
              null,
              null,
              null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");

        if (data.moveToFirst()) {


        }

        String dateString = Utility.formatDate(
              data.getLong(COL_WEATHER_DATE));

        String weatherDescription =
              data.getString(COL_WEATHER_DESC);

        boolean isMetric = Utility.isMetric(getActivity());

        String high = Utility.formatTemperature(
              this.getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);

        String low = Utility.formatTemperature(
              this.getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);


        //TextView detailTextView = (TextView)getView().findViewById(R.id.detail_text);
        //detailTextView.setText(mForecast);

        // Read weather icon ID from cursor
        int weatherId = data.getInt(ForecastFragment.COL_WEATHER_ID);

        mIconView.setImageResource(R.drawable.ic_launcher);
        mDayTv.setText(Utility.getDayName(getActivity(), data.getLong(COL_WEATHER_DATE)));
        mDateTv.setText(Utility.getFormattedMonthDay(getActivity(), data.getLong(COL_WEATHER_DATE)));
        mHighTv.setText(high);
        mLowTv.setText(low);
        mForecastTv.setText(weatherDescription);
        mHumidityTv.setText(getActivity().getString(R.string.format_humidity, data.getFloat(COL_WEATHER_HUMIDITY)));
        mWindTv.setText(Utility.getFormattedWind(getActivity(), data.getFloat(COL_WEATHER_WIND_SPEED), data.getFloat(COL_WEATHER_DEGREES)));
        mPressureTv.setText(getActivity().getString(R.string.format_pressure, data.getFloat(COL_WEATHER_PRESSURE)));

        mForecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast );
        return shareIntent;
    }

    private Intent createtShareIntent() {
//        TextView textView =  (TextView) getActivity().findViewById(R.id.detail_text);
//        String forecast = textView.getText().toString();
//        Log.d(LOG_TAG, "Forecast: " + forecast);

        Intent intent = new Intent(Intent.ACTION_SEND);
        // prevents Activity selected for sharing from being placed on app stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mForecast);
        return intent;
    }
}
