package com.nitap.attende.pages;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.nitap.attende.EncryptActivity;
import com.nitap.attende.MyUtils;
import com.nitap.attende.models.MyConfiguration;
import com.ttv.facerecog.databinding.ActivityAttendanceBinding;

public class AttendanceActivity extends AppCompatActivity {

    ActivityAttendanceBinding binding;
    String bt_name ;
    BluetoothAdapter bluetoothAdapter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        progressDialog = new ProgressDialog(AttendanceActivity.this);

        MyConfiguration myConfiguration = MyUtils.getConfiguration(getApplicationContext());

        if(myConfiguration!= null){
            bt_name = myConfiguration.student.rollno;
            bt_name = EncryptActivity.encrypt(bt_name);

        }else{
            Toast.makeText(this, "can't access local storage", Toast.LENGTH_SHORT).show();
        }



        binding.attendanceBtn.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(AttendanceActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    ActivityCompat.requestPermissions(AttendanceActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                }

            }
            if (ContextCompat.checkSelfPermission(AttendanceActivity.this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_DENIED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    ActivityCompat.requestPermissions(AttendanceActivity.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 2);
                }

            }

            if (ContextCompat.checkSelfPermission(AttendanceActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED ) {
                if (ContextCompat.checkSelfPermission(AttendanceActivity.this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED ) {

                    if (!bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.enable();
                    } else if (!bluetoothAdapter.isDiscovering()) {
                        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);


                        bluetoothLauncher.launch(discoverableIntent);
                    }

                }else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ActivityCompat.requestPermissions(AttendanceActivity.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 2);
                    }
                    Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
                }

            }


        });

    }

    public boolean setBluetooth(boolean enable) throws InterruptedException {
        final long lTimeToGiveUp_ms = System.currentTimeMillis() + 10000;

        Toast.makeText(this, "Entered SetBluetooth", Toast.LENGTH_SHORT).show();

        if (ContextCompat.checkSelfPermission(AttendanceActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {

            if (bluetoothAdapter.isEnabled()) {

                String sOldName = bluetoothAdapter.getName();

                Boolean flag = bluetoothAdapter.setName(bt_name);





            }


        }

        return true;
    }

    ActivityResultLauncher<Intent> bluetoothLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if (result.getResultCode() == 120) {

                    try {
                        setBluetooth(true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(this, "Not discoverable", Toast.LENGTH_SHORT).show();
                }

            }
    );





}