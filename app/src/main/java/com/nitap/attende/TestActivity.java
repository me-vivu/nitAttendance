package com.nitap.attende;

import static org.apache.poi.ss.util.CellUtil.createCell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nitap.attende.models.Teacher;
import com.ttv.facerecog.R;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        XSSFWorkbook ourWorkBook = new XSSFWorkbook();
        Sheet sheet = ourWorkBook.createSheet("statSheet");
        Row row1 = sheet.createRow(0);
        Row row2 = sheet.createRow(1);

        row1.createCell(0).setCellValue("hdj");
        row1.createCell(1).setCellValue("bfkjsk");
        row2.createCell(0).setCellValue("jffs");
        row2.createCell(1).setCellValue("nfew");

        /*createCell(row1, 0, "Mike");
        createCell(row1, 1, "470");

        createCell(row2, 0, "Montessori");
        createCell(row2, 1, "460");*/


        File mydir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        //File myfile = new File(mydir.getAbsolutePath()+"test.xlsx");
        File myfile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "test.xlsx");
        try {
            myfile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            FileOutputStream fileOut = new FileOutputStream(myfile);
            ourWorkBook.write(fileOut);
            fileOut.close();
            Toast.makeText(this, "written", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}