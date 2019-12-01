package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.myapplication.Model.Meeting;
import com.example.myapplication.RecyclerAdapter.MeetingAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TodayActivity extends AppCompatActivity {

    TextView Date;
    private int mYear, mMonth, mDay, mHour, mMinute;
    ArrayList<Meeting> meetings;
    private RecyclerView recyclerView;
    private MeetingAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    Button prevButton,nextButton,schedulebutton;
    Calendar c;
    Dialog d;
    Boolean orientationLand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Date = (TextView) findViewById(R.id.Date);
        recyclerView = (RecyclerView) findViewById(R.id.meetingrecycler);
        prevButton = (Button)findViewById(R.id.previousbutton);
        nextButton = (Button)findViewById(R.id.nextbutton);
        schedulebutton = (Button)findViewById(R.id.schedulebutton);

        //getting old data if orientation change
        if (savedInstanceState!=null && savedInstanceState.getSerializable("meetings")!=null)
        {
            meetings = (ArrayList<Meeting>) savedInstanceState.getSerializable("meetings");
        }
        else {
            meetings = new ArrayList<>();
        }



        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            mAdapter = new MeetingAdapter(meetings,false);
        } else {
            // code for landscape mode
            mAdapter = new MeetingAdapter(meetings,true);
        }

        recyclerView.setAdapter(mAdapter);

        //next or previous day data
        if (getIntent().getSerializableExtra("Calender")!=null)
        {
            c= (Calendar) getIntent().getSerializableExtra("Calender");
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
        }
        //saved data in orientation change
        else if (savedInstanceState!=null && savedInstanceState.getSerializable("Calender")!=null)
        {
            c= (Calendar) savedInstanceState.getSerializable("Calender");
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
        }

        else {
            // Get Current Date
            c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
        }
            getMeetingData(mDay, mMonth+1, mYear);
            Date.setText(mDay + "-" + (mMonth + 1) + "-" + mYear);


        Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(TodayActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                Date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                meetings.clear();
                                //getting meeting is date changed
                                getMeetingData(dayOfMonth, monthOfYear+1, year);


                                //next and previous will be now of new selected day

                                String strThatDay = String.valueOf(year)+"/"+String.valueOf(monthOfYear+1)+"/"+String.valueOf(dayOfMonth);
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                                java.util.Date d = null;
                                try {
                                    d = formatter.parse(strThatDay);
                                    c = Calendar.getInstance();
                                    c.setTime(d);
                                    //catch exception
                                } catch (ParseException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }



                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.add(Calendar.DATE,-1);
                Intent inten = new Intent(TodayActivity.this,TodayActivity.class);
                inten.putExtra("Calender",c);
                startActivity(inten);

            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.add(Calendar.DATE,1);
                Intent inten = new Intent(TodayActivity.this,TodayActivity.class);
                inten.putExtra("Calender",c);
                startActivity(inten);


            }
        });

        schedulebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent inten = new Intent(TodayActivity.this,ScheduleActivity.class);
                inten.putExtra("Calender",c);
                startActivity(inten);
            }
        });



    }

    public  void getMeetingData(int day,int month,int year)
    {
        d = showdialog();
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
                        try {
                            d.cancel();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                                for (int k =0 ;k<participantjson.length();k++)
                                {
                                    participants.add(participantjson.getString(k));
                                }
                                Meeting thismeeting = new Meeting(description,starttime,endtime,participants);
                                meetings.add(thismeeting);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(TodayActivity.this,"INCORRECT JSON",Toast.LENGTH_LONG).show();
                            }
                        }
                        mAdapter.notifyDataSetChanged();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(TodayActivity.this);
        builder.setMessage(R.string.FetchingData);

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("Calender", c);
        outState.putSerializable("meetings",meetings);

        super.onSaveInstanceState(outState);
    }




}
