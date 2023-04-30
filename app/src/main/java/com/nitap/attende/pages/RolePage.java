package com.nitap.attende.pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nitap.attende.LoginActivity;
import com.ttv.facerecog.R;
import com.ttv.facerecog.databinding.ActivityRolePageBinding;

public class RolePage extends AppCompatActivity {

    ActivityRolePageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRolePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.studentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent studentIntent = new Intent(RolePage.this, LoginActivity.class);
                studentIntent.putExtra("role", "student");
                startActivity(studentIntent);
            }
        });

        binding.teacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent teacherIntent = new Intent(RolePage.this, LoginActivity.class);
                teacherIntent.putExtra("role", "teacher");
                startActivity(teacherIntent);
            }
        });
    }
}