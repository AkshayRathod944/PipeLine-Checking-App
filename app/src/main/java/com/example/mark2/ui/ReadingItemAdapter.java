package com.example.mark2.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mark2.R;
import com.example.mark2.modal.Reading;
import com.example.mark2.util.TimeConverter;

import java.util.List;

public class ReadingItemAdapter extends RecyclerView.Adapter<ReadingItemAdapter.ViewHolder> {


    private Context context;
    private List<Reading> readingList;
    private String username;


    public ReadingItemAdapter(Context context, List<Reading> readingList,String userName) {
        this.context = context;
        this.readingList = readingList;
        this.username=userName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from ( parent.getContext () )
                .inflate ( R.layout.reading_item,parent,false );
        return new ViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reading reading = readingList.get ( position );
        holder.locationNameTextView.setText ( reading.getLocationTitle () );
        holder.locationCoordnateTextView.setText ( "Lat "+String.valueOf (reading.getLatitude ()  )+" Long "+String.valueOf (reading.getLongitude ()) );
        holder.usernameTextView.setText ( reading.getUserName () );
        holder.currentTextView.setText ( String.valueOf (reading.getCurrent ()  ) );
        holder.voltageTextView.setText ( String.valueOf (reading.getVoltage()  ) );
        long time =Long.parseLong ( reading.getTimeStamp ()  );
        holder.timestampTextView.setText ( TimeConverter.getDate ( time,"dd-MM-yyyy HH:mm:ss" ) );

        if (reading.getStatus () == 0){
            holder.locationStatus.setBackgroundResource(R.drawable.cancel);
        } else{
            holder.locationStatus.setBackgroundResource(R.drawable.success);
        }
    }

    @Override
    public int getItemCount() {
        return readingList.size ();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView locationCoordnateTextView,usernameTextView,currentTextView,voltageTextView,timestampTextView;
        public TextView locationNameTextView;
        public ImageView locationStatus;

        public ViewHolder(@NonNull View itemView) {
            super ( itemView );
            locationStatus=itemView.findViewById ( R.id.statusImageView );
            locationNameTextView=itemView.findViewById ( R.id.Location_title );
            locationCoordnateTextView=itemView.findViewById ( R.id.Location_Co_ordnates );
            usernameTextView=itemView.findViewById ( R.id.user_name_textview );
            currentTextView=itemView.findViewById ( R.id.current_textview );
            voltageTextView=itemView.findViewById ( R.id.voltage_textview );
            timestampTextView=itemView.findViewById ( R.id.timestamp_textView );
        }
    }
}
