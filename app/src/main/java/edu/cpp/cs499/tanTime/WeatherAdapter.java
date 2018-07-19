package edu.cpp.cs499.tanTime;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;


public class WeatherAdapter extends ArrayAdapter<Weather> {
    public WeatherAdapter(@NonNull Context context, ArrayList<Weather> weatherArrayList) {
        super(context, 0, weatherArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Weather weather = getItem(position);//this

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        //TextView dateTextView = convertView.findViewById(R.id.Date);
        //TextView minTextView = convertView.findViewById(R.id.LowTemperature);
        //TextView maxTextView = convertView.findViewById(R.id.HighTemperature);
        //TextView linkTextView = convertView.findViewById(R.id.Link);

        //dateTextView.setText(weather.getDate());//these
        //minTextView.setText(weather.getMinTemp());
        //maxTextView.setText(weather.getMaxTemp());
        //linkTextView.setText(weather.getLink());

/*        TextView day1TextView = convertView.findViewById(R.id.day1);                    //Day
        day1TextView.setText("SUN");
        TextView day1tempTextView = convertView.findViewById(R.id.day1temp);            //temperature
        day1tempTextView.setText(minTemperature+"/"+maxTemperature);
        ImageView day1iconImageView = convertView.findViewById(R.id.day1icon);          //icon
        day1iconImageView.setImageDrawable(getDrawable(R.drawable.sunny));
        TextView day1conditionTextView = convertView.findViewById(R.id.day1condition);  //condition
        day1conditionTextView.setText(iconPhrase);
        break;
        case 1: //day 2
        TextView day2TextView = convertView.findViewById(R.id.day2);                    //Day
        day2TextView.setText("MON");
        TextView day2tempTextView = convertView.findViewById(R.id.day2temp);            //temperature
        day2tempTextView.setText(minTemperature+"/"+maxTemperature);
        ImageView day2iconImageView = convertView.findViewById(R.id.day2icon);          //icon
        day2iconImageView.setImageDrawable(getDrawable(R.drawable.sunny));
        TextView day2conditionTextView =convertView. findViewById(R.id.day2condition);  //condition
        day2conditionTextView.setText(iconPhrase);
        break;
        case 2: //day 3
        TextView day3TextView = convertView.findViewById(R.id.day3);                    //Day
        day3TextView.setText("TUE");
        TextView day3tempTextView = convertView.findViewById(R.id.day3temp);            //temperature
        day3tempTextView.setText(minTemperature+"/"+maxTemperature);
        ImageView day3iconImageView = convertView.findViewById(R.id.day1icon);          //icon
        day3iconImageView.setImageDrawable(getDrawable(R.drawable.sunny));
        TextView day3conditionTextView = convertView.findViewById(R.id.day3condition);  //condition
        day3conditionTextView.setText(iconPhrase);
        break;
        case 3: //day 4
        TextView day4TextView = convertView.findViewById(R.id.day4);                    //Day
        day4TextView.setText("WED");
        TextView day4tempTextView = convertView.findViewById(R.id.day4temp);            //temperature
        day4tempTextView.setText(minTemperature+"/"+maxTemperature);
        ImageView day4iconImageView = convertView.findViewById(R.id.day4icon);          //icon
        day4iconImageView.setImageDrawable(getDrawable(R.drawable.sunny));
        TextView day4conditionTextView = convertView.findViewById(R.id.day4condition);  //condition
        day4conditionTextView.setText(iconPhrase);*/

        return convertView;

    }
}
