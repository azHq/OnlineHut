package com.ecommerce.onlinehut;

import java.text.ParseException;
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
    public String get_current_data_time2(){
        String format="dd-MM-yyyy hh:mm:ss aa";
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat(format, Locale.US);
        String time=formatter.format(date);
        return  time;
    }
    public Date convert_string_to_date_time(String date_str){
        Date date=null;
        try {
            date = formatter.parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    public Date convert_string_to_date_time2(String date_str){
        String format="dd-MM-yyyy hh:mm:ss aa";
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat(format, Locale.US);
        Date date=null;
        try {
            date = formatter.parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    public String toDateStr(long milliseconds)
    {
        Date date = new Date(milliseconds);
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.US);
        return formatter.format(date);
    }
    public String toDateStr2(long milliseconds)
    {
        Date date = new Date(milliseconds);
        String format="dd-MM-yyyy hh:mm:ss aa";
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat(format, Locale.US);
        return formatter.format(date);
    }
}
