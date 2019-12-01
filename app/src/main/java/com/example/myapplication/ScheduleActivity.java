package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.myapplication.Model.Meeting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScheduleActivity extends AppCompatActivity {

    Button DateButton,starttimeButton,endtimeButton,submitButton,backButton;
    private Integer mYear, mMonth, mDay, mHour, mMinute,currentday;
    ArrayList<Meeting> meetings;
    Dialog d;
    Calendar c,currentcal;
    private Integer selectedYear, selectedMonth, selectedDay;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        DateButton = (Button)findViewById(R.id.dateButton);
        starttimeButton = (Button)findViewById(R.id.StartButton);
        endtimeButton = (Button)findViewById(R.id.EndButton);
        submitButton = (Button)findViewById(R.id.submitbutton);
        backButton = (Button)findViewById(R.id.backbutton);
        meetings =  new ArrayList<>();

//date is passed from parent activity

        if (getIntent().getSerializableExtra("Calender")!=null)
        {
            c= (Calendar) getIntent().getSerializableExtra("Calender");
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            DateButton.setText(mDay + "-" + (mMonth+1) + "-" + mYear);
            selectedYear = mYear;
            selectedMonth =  mMonth;
            selectedDay = mDay;
        }
//if orientation change
        if (savedInstanceState!=null && savedInstanceState.getSerializable("Calender")!=null)
        {
            c= (Calendar) savedInstanceState.getSerializable("Calender");
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            DateButton.setText(mDay + "-" + (mMonth+1) + "-" + mYear);
        }


        //checking if date is before current date

        currentcal = Calendar.getInstance();
        // set the calendar to start of today
        currentcal.set(Calendar.HOUR_OF_DAY, 0);
        currentcal.set(Calendar.MINUTE, 0);
        currentcal.set(Calendar.SECOND, 0);
        currentcal.set(Calendar.MILLISECOND, 0);
        // and get that as a Date
        Date today = currentcal.getTime();

        // and get that as a Date
        Date dateSpecified = c.getTime();

// test your condition
        if (dateSpecified.before(today)) {
            submitButton.setBackgroundColor(getResources().getColor(R.color.gray));
            submitButton.setEnabled(false);
        }



        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mYear==null)
                {
                    Toast.makeText(ScheduleActivity.this,"Please Fill Date",Toast.LENGTH_LONG).show();
                    return;
                }

                if (starttimeButton.getText().toString().equalsIgnoreCase(""))
                {
                    Toast.makeText(ScheduleActivity.this,"Please Fill Start Time ",Toast.LENGTH_LONG).show();
                    return;

                }
                if (endtimeButton.getText().toString().equalsIgnoreCase(""))
                {
                    Toast.makeText(ScheduleActivity.this,"Please Fill End Time",Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    //getting if schedule is valid
                    getValidSchedule(selectedDay,selectedMonth+1,selectedYear);
                    return;
                }

            }
        });
        DateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ScheduleActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                DateButton.setText(dayOfMonth + "-" + monthOfYear+1 + "-" + year);
                                //next and previous will be now of new selected day
                                selectedYear = year;
                                selectedMonth =  monthOfYear;
                                selectedDay = dayOfMonth;


                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        starttimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showtimerdialog((Button)v);
            }
        });

        endtimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showtimerdialog((Button)v);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void showtimerdialog(final Button b)
    {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if (minute<10)
                        {
                            b.setText(hourOfDay + ":" + minute+"0");
                        }
                        else {
                            b.setText(hourOfDay + ":" + minute);
                        }
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    public  void getValidSchedule(int day,int month,int year)
    {   d = showdialog();
        d.show();
        String datetosend = String.valueOf(day)+"/"+String.valueOf(month)+"/"+String.valueOf(year);
        AndroidNetworking.get("http://fathomless-shelf-5846.herokuapp.com/api/schedule?")
                .addQueryParameter("date", datetosend)
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        Boolean meetingscheduled = false;
                        d.cancel();
                        Log.d("response",response.toString());
                        meetings.clear();
                        for (int i=0;i<response.length();i++)
                        {
                            try {
                                JSONObject meetingjson = response.getJSONObject(i);
                                String starttime = meetingjson.getString("start_time");
                                String endtime = meetingjson.getString("end_time");
                                String description = meetingjson.getString("description");

                                JSONArray participantjson =  meetingjson.getJSONArray("participants");
                                ArrayList<String> participants =  new ArrayList<>();
                                for (int k =0 ;i<participantjson.length();k++)
                                {
                                    participants.add(participantjson.getString(k));
                                }
                                Meeting thismeeting = new Meeting(description,starttime,endtime,participants);
                                meetings.add(thismeeting);
                                //checking time strings
                                if (starttime.equalsIgnoreCase(starttimeButton.getText().toString()) && endtime.equalsIgnoreCase(endtimeButton.getText().toString()))
                                {
                                    meetingscheduled=true;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(ScheduleActivity.this,"INCORRECT JSON",Toast.LENGTH_LONG).show();
                            }
                        }
                        if (meetingscheduled)
                        {
                            Toast.makeText(ScheduleActivity.this,"Slot Availaible",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(ScheduleActivity.this,"Slot Not Availaible!",Toast.LENGTH_LONG).show();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        Log.d("error",error.toString());
                        // handle error
                    }
                });
    }

    public Dialog showdialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleActivity.this);
        builder.setMessage(R.string.FetchingData);

        // Create the AlertDialog object and return it
        return builder.create();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("Calender", c);


        super.onSaveInstanceState(outState);
    }
}
