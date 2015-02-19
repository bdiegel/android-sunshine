package com.example.diegelb.sunshine.app;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private static final String LOG_TAG = "ForecastAdapter";

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;
    private boolean mUseTodayLayout;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private String formatTemp(double temp) {
        return Utility.formatTemperature(mContext, temp, Utility.isMetric(mContext));
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = (viewType == VIEW_TYPE_TODAY) ? R.layout.list_item_forecast_today :  R.layout.list_item_forecast;

        View view =  LayoutInflater.from(context).inflate(layoutId, parent, false);

        // Use ViewHolder pattern to save inflated views:
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    /*
                This is where we fill-in the views with the contents of the cursor.
             */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.
        Log.i(LOG_TAG, "bindView" );
        if ( !(view instanceof LinearLayout) ) {
            Log.i(LOG_TAG, "VIEW ID: " + view.getId() + " - " + view.getClass().getName() );
        }

        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);

        Log.i(LOG_TAG, "Weather ID: " + weatherId);

        // Use ViewHolder
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Icon is specific to type of view:
        int viewType = getItemViewType(cursor.getPosition());

        // set icon
        if (viewType == VIEW_TYPE_TODAY )
            viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
        else
            viewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(weatherId));

        //viewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(weatherId));
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, cursor.getLong(ForecastFragment.COL_WEATHER_DATE)));
        viewHolder.forecastView.setText(cursor.getString(ForecastFragment.COL_WEATHER_DESC));
        viewHolder.highTempView.setText(formatTemp(cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP)));
        viewHolder.lowTempView.setText(formatTemp(cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP)));

        // add content description to icon for accessibility
        viewHolder.iconView.setContentDescription(cursor.getString(ForecastFragment.COL_WEATHER_DESC));
    }

    public static class ViewHolder{
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView forecastView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView)view.findViewById(R.id.list_item_date_textview);
            forecastView = (TextView)view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView)view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView)view.findViewById(R.id.list_item_low_textview);
        }
    }
}