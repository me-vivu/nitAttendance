package com.nitap.attende.pages;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nitap.attende.Adapters.RollListAdapter;
import com.nitap.attende.ClickListener;
import com.nitap.attende.model.RollListModel;
import com.ttv.facerecog.MainActivity;
import com.ttv.facerecog.R;
import com.ttv.facerecog.databinding.ActivityDisplayAttendanceBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableMap;

public class DisplayAttendance extends AppCompatActivity implements ClickListener {

    RollListModel  rollListModel;
    ArrayList<RollListModel> absenteesList = new ArrayList<>();
    ArrayList<RollListModel> presentList = new ArrayList<>();
    List<String> rollList;
    String sectionId, subject;
    String sectionCode;
    RecyclerView rollView;
    RollListAdapter adapter;


    ActivityDisplayAttendanceBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDisplayAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        rollView = findViewById(R.id.absentees_list);
        rollList = intent.getStringArrayListExtra("rollList");
        sectionId = intent.getStringExtra("sectionId");
        sectionCode = intent.getStringExtra("sectionCode");
        subject = intent.getStringExtra("subject");

        Toast.makeText(this, sectionCode, Toast.LENGTH_SHORT).show();

        getAttendance();

        binding.submitAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog  = new AlertDialog.Builder(DisplayAttendance.this);
                dialog.setMessage("Are you sure?");
                dialog.setCancelable(true);

                dialog.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                uploadData();

                                startActivity(new Intent(DisplayAttendance.this, HomeActivity.class));




                            }
                        });

                dialog.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = dialog.create();
                alert.show();


            }
        });

        enableSwipeToDeleteAndUndo();



    }

    void getAttendance(){

        DatabaseReference secRef = FirebaseDatabase.getInstance().getReference().child("sections");
        final String[] strtRoll = new String[1];
        final String[] endRoll = new String[1];

        secRef.child(sectionCode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    strtRoll[0] = snapshot.child("startRollno").getValue().toString();
                    endRoll[0] = snapshot.child("endRollno").getValue().toString();

                    int start = Integer.valueOf(strtRoll[0]);
                    int end = Integer.valueOf(endRoll[0]);

                    int size = rollList.size();

                    if(size >0){

                        for(int i = start; i <= end; i++){

                            RollListModel rollListModel = new RollListModel();

                            if(!rollList.contains(String.valueOf(i))){

                                rollListModel.setRoll_num(String.valueOf(i));

                                absenteesList.add(rollListModel);

                            }else if(rollList.contains(String.valueOf(i))){
                                rollListModel.setRoll_num(String.valueOf(i));

                                presentList.add(rollListModel);
                            }


                        }

                        LinearLayoutManager layoutManager = new LinearLayoutManager(DisplayAttendance.this);
                        rollView.setLayoutManager(layoutManager);

                        adapter = new RollListAdapter(absenteesList, DisplayAttendance.this, DisplayAttendance.this);
                        rollView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }else{
                        Toast.makeText(DisplayAttendance.this, "The given List is empty", Toast.LENGTH_SHORT).show();
                    }


                }else{
                    Toast.makeText(DisplayAttendance.this, "Section Not Found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    @Override
    public void giveAttendanceTo(int position) {

        RollListModel model = new RollListModel();

        model = absenteesList.get(position);
        absenteesList.remove(position);
        presentList.add(model);
//        rollView.setLayoutManager(new LinearLayoutManager(this));
//        rollView.setHasFixedSize(true);

        rollView.getAdapter().notifyDataSetChanged();

    }


    public void uploadData(){





        final DatabaseReference attendance_ref = FirebaseDatabase.getInstance().getReference().child("attendance_record");

        final String saveCurrentDate, saveCurrentMonth, saveCurrentTime;


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMMM,dd yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentMonth = new SimpleDateFormat("MMMM");
        saveCurrentMonth = currentMonth.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hmma");
        saveCurrentTime = currentTime.format(calendar.getTime());

        String sessionId = saveCurrentDate+saveCurrentTime;


        final HashMap sessionMap = new HashMap<>();


        sessionMap.put("session_id", sectionId+ "" + saveCurrentTime );
        sessionMap.put("date", saveCurrentDate);
        sessionMap.put("absentees", absenteesList);
        sessionMap.put("presentess", presentList);


        attendance_ref.child(saveCurrentMonth).child(subject).child(sectionCode).child(saveCurrentDate).child(sessionId).updateChildren(sessionMap)
                           .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    Toast.makeText(DisplayAttendance.this, "Submitted Sucessfully", Toast.LENGTH_SHORT).show();

                                }
                            });


    }

    private void enableSwipeToDeleteAndUndo() {

        //Toast.makeText(this, "Entered swipe to delete and Undo", Toast.LENGTH_SHORT).show();
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final RollListModel item = adapter.getData().get(position);

                adapter.removeItem(position);


                Snackbar snackbar = Snackbar
                        .make(binding.coordinatorLayout, item.getRoll_num()+" was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        adapter.restoreItem(item, position);
                        rollView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(rollView);
    }
}