package com.example.myapplication.RecyclerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Meeting;
import com.example.myapplication.R;

import java.util.ArrayList;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MyViewHolder> {
    private ArrayList<Meeting> mDataset;
    private static Boolean orientationLand;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView duration;
        public TextView description;
        public TextView starttime;
        public TextView endtime;
        public TextView attendees;


        public MyViewHolder(View itemView) {
            super(itemView);
            if (!orientationLand)
            {
                duration = (TextView)itemView.findViewById(R.id.duration);
                description = (TextView)itemView.findViewById(R.id.description);
            } else {
                description = (TextView)itemView.findViewById(R.id.description);
                starttime = (TextView)itemView.findViewById(R.id.starttime);
                endtime = (TextView)itemView.findViewById(R.id.endtime);
                attendees = (TextView)itemView.findViewById(R.id.attendees);
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MeetingAdapter(ArrayList<Meeting> myDataset,Boolean land) {
        mDataset = myDataset;
        orientationLand = land;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MeetingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.singlescheduleitem, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Meeting currentItem = mDataset.get(position);
        if (orientationLand)
        {
            StringBuilder attendees =  new StringBuilder("");
            ArrayList<String> attendes =  currentItem.getAttendees();
            try {
                for (int i=0;i<attendes.size();i++)
                {
                    attendees.append(attendes.get(i));
                    if (i!=(attendes.size()-1))
                    {
                        attendees.append(",");
                    }
                }
                holder.attendees.setText(attendees.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.starttime.setText(currentItem.getStartTime());
            holder.description.setText(currentItem.getAgenda());
            holder.endtime.setText(currentItem.getEndTime());

        }
        else {

            String durationtext = currentItem.getStartTime() + " - " + String.valueOf(currentItem.getEndTime());
            holder.duration.setText(durationtext);
            holder.description.setText(currentItem.getAgenda());
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}