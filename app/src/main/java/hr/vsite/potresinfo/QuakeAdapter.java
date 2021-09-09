package hr.vsite.potresinfo;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class QuakeAdapter extends  ArrayAdapter<Quake> {


    private int mResource;

    public QuakeAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Quake> objects) {
        super(context, resource, objects);
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // return super.getView(position, convertView, parent);
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(mResource, parent, false);
        }

        Quake currentQuake = getItem(position);

        TextView magnitude = (TextView) listItemView.findViewById(R.id.magnitude_textView);
        DecimalFormat formatter = new DecimalFormat("0.0");
        magnitude.setText(formatter.format(currentQuake.getmMagnitude()));
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitude.getBackground();
        int magnitudeColor = ContextCompat.getColor(getContext(),getMagnitudeColor(currentQuake.getmMagnitude()));
        magnitudeCircle.setColor(magnitudeColor);

        String primaryLocation =  currentQuake.getmLocation();
        String locationOffset =  currentQuake.getmOffset();

        TextView primaryLocationView = (TextView) listItemView.findViewById(R.id.primary_location);
        primaryLocationView.setText(primaryLocation);
        TextView locationOffsetView = (TextView) listItemView.findViewById(R.id.location_offset);
        locationOffsetView.setText(locationOffset);

        Date dateObject = new Date(currentQuake.getmDate());
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date_textView);
        dateTextView.setText(formatDate(dateObject));
        TextView timeTextView = (TextView) listItemView.findViewById(R.id.time_textView);
        timeTextView.setText(formatTime(dateObject));

        return listItemView;
    }

    public String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d.M.yyyy.");
        return dateFormat.format(dateObject);
    }

    public String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("H:mm:ss");
        return timeFormat.format(dateObject);
    }

    public static int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.lvl1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.lvl2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.lvl3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.lvl4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.lvl5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.lvl6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.lvl7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.lvl8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.lvl9;
                break;
            default:
                magnitudeColorResourceId = R.color.lvl10;
                break;
        }
        return  magnitudeColorResourceId;
    }

}
