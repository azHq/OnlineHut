package com.ecommerce.onlinehut;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeConverter {
    private static DateTimeConverter dateTimeConverter=new DateTimeConverter();
    Date date;
    String format="dd-MM-yyyy hh:mm aa";
    SimpleDateFormat formatter;
    private  DateTimeConverter(){
        date=new Date();
        formatter = new SimpleDateFormat(format, Locale.US);
    }
    public static DateTimeConverter getInstance(){
        if(dateTimeConverter==null){
            dateTimeConverter=new DateTimeConverter();
        }

        return dateTimeConverter;
    }

    public String get_current_data_time(){

        String time=formatter.format(date);
        return  time;
    }
    public String toDateStr(long milliseconds)
    {
        Date date = new Date(milliseconds);
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.US);
        return formatter.format(date);
    }
}
