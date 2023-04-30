package com.nitap.attende;

import static com.nitap.attende.LoginActivity.facetagForFaceInfo;
import static com.ttv.facerecog.R.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nitap.attende.models.MyConfiguration;
import com.nitap.attende.models.MyStudent;
import com.nitap.attende.models.Student;
import com.nitap.attende.models.StudentConfiguration;
import com.nitap.attende.pages.HomeActivity;
import com.ttv.face.FaceFeatureInfo;
import com.ttv.face.FaceResult;
import com.ttv.facerecog.DBHelper;
import com.ttv.facerecog.FaceEntity;
import com.ttv.facerecog.ImageRotator;
//import com.ttv.facerecog.R;
import com.ttv.facerecog.MainActivity;
import com.ttv.facerecog.R;
import com.ttv.facerecog.Utils;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import kotlin.jvm.internal.Intrinsics;


public class FaceRecognitionActivity extends AppCompatActivity {

    private DBHelper mydb ;
    public static ArrayList userLists;
    Button btnRegister, submitButton ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userLists = new ArrayList(0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);
        //Toast.makeText(getApplicationContext(), "FACE RECOGNITION ACTIVITY", Toast.LENGTH_SHORT).show();

        this.mydb = new DBHelper(this);
        this.mydb = new DBHelper((Context)this);


//        btnVerify = findViewById(id.btnVerify);
//        btnRegister = findViewById(id.btnRegister);
//        btnVerify.setEnabled(true);

        submitButton = findViewById(id.button_next);

        btnRegister = findViewById(id.upload_btn);
        submitButton.setEnabled(false);
        btnRegister.setEnabled(true);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction("android.intent.action.PICK");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(FaceRecognitionActivity.this, HomeActivity.class));
                //finish();
            }
        });


    }

    void display(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    void signOut() {
        FirebaseAuth userAuth;
        GoogleSignInClient mGoogleSigninClient;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSigninClient = GoogleSignIn.getClient(this, gso);
        //userAuth.getInstance().signOut();
        FirebaseAuth.getInstance().signOut();
        mGoogleSigninClient.signOut();
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {



        if (requestCode == 1 && resultCode == -1) {
            try {
                Context var10000 = (Context)this;
                Uri var10001 = data != null ? data.getData() : null;
                if (data == null) {
                    //display("DATA NULL");
                } else {
                   // display(data.getData().toString());
                }
                Intrinsics.checkNotNull(var10001);
                Bitmap var20 = ImageRotator.getCorrectlyOrientedImage(var10000, var10001);
                if (var20 == null) {
                   // display("BITMAP NULL");
                } else {
                    display(var20.toString());
                }
                Intrinsics.checkNotNullExpressionValue(var20, "ImageRotator.getCorrectl…Image(this, data?.data!!)");
                Bitmap bitmap = var20;
                List<FaceResult> var21 = com.nitap.attende.MainActivity.faceEngine.detectFace(bitmap);
                Intrinsics.checkNotNullExpressionValue(var21, "FaceEngine.getInstance(this).detectFace(bitmap)");
                if (var21 == null) {
                    //display("FACERESULT NULL");
                } else {
                    //display(Objects.toString(var21.size()));
                }
                final List faceResults = var21;
                Collection var6 = (Collection)faceResults;
                if (var6.size() == 1) {
                    com.nitap.attende.MainActivity.faceEngine.extractFeature(bitmap, true, faceResults);
                    display("FEATURES EXTRACTED");
                    // StringCompanionObject var7 = StringCompanionObject.INSTANCE;
                    String var8 = "User%03d";
                    Object[] var22 = new Object[1];
                    ArrayList var10003 = userLists;

                    if (var10003 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("userLists");
                    }

                    var22[0] = var10003.size() + 1;
                    Object[] var9 = var22;
                    String var23 = String.format(var8, Arrays.copyOf(var9, var9.length));
                    Intrinsics.checkNotNullExpressionValue(var23, "java.lang.String.format(format, *args)");
                    String userName = var23;
                    Rect cropRect = Utils.getBestRect(bitmap.getWidth(), bitmap.getHeight(), ((FaceResult)faceResults.get(0)).rect);
                    final Bitmap headImg = Utils.crop(bitmap, cropRect.left, cropRect.top, cropRect.width(), cropRect.height(), 120, 120);
                    View inputView = LayoutInflater.from(this).inflate(R.layout.dialog_input_view, (ViewGroup)null, false);
                    //final EditText editText = (EditText)inputView.findViewById(R.id.et_user_name);
                    ImageView ivHead = (ImageView)inputView.findViewById(R.id.iv_head);
                    ivHead.setImageBitmap(headImg);
                   // editText.setText((CharSequence)userName);
                    // Context var10002 = this.context;
                    //Intrinsics.checkNotNull(var10002);
                    //AlertDialog var24 = (new AlertDialog.Builder(this)).setView(inputView).setPositiveButton((CharSequence)"OK", (DialogInterface.OnClickListener)null).setNegativeButton((CharSequence)"Cancel", (DialogInterface.OnClickListener)null).create();
                   // Intrinsics.checkNotNullExpressionValue(var24, "AlertDialog.Builder(cont…                .create()");
                    //final AlertDialog confirmUpdateDialog = var24;
                    //confirmUpdateDialog.show();
                    display("ALERT SHOWED");
                    /*
                    confirmUpdateDialog.getButton(-1).setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
                        public final void onClick(@Nullable View v) {




                        }
                    }));
                     confirmUpdateDialog.cancel();
                     */
                    // START

                    // EditText var10000 = editText;
                    Intrinsics.checkNotNullExpressionValue(var10000, "editText");
                    String s = LoginActivity.rollno;  //var10000.getText().toString();

                    boolean exists = false;
                    Iterator var5 = com.ttv.facerecog.MainActivity.Companion.getUserLists().iterator();

                    while(var5.hasNext()) {
                        FaceEntity user = (FaceEntity)var5.next();
                        if (TextUtils.equals((CharSequence)user.userName, (CharSequence)s)) {
                            exists = true;
                            break;
                        }
                    }


                    DBHelper var91 = mydb;
                    Intrinsics.checkNotNull(var9);
                    int user_id = var91.insertUser(s, headImg, ((FaceResult)faceResults.get(0)).feature);
                    FaceEntity face = new FaceEntity(user_id, s, headImg, ((FaceResult)faceResults.get(0)).feature);
                    com.ttv.facerecog.MainActivity.Companion.getUserLists().add(face);

                    FaceFeatureInfo faceFeatureInfo = new FaceFeatureInfo(user_id, ((FaceResult)faceResults.get(0)).feature);

                    uploadPhoto(getApplicationContext(),data.getData(),LoginActivity.rollno,faceFeatureInfo);

                    /*
                    StudentConfiguration builderConfiguration = MyUtils.getStudentConfigurationBuilder(getApplicationContext());
                    String bitmapString = MyUtils.getStringFromObject(bitmap);
                    test(bitmapString);
                    assert builderConfiguration != null;
                    assert builderConfiguration.student!=null;
                    builderConfiguration.student.bitmapString = bitmapString;

                    String updatedString = MyUtils.getStringFromObject(builderConfiguration);
                    MyUtils.saveString(getApplicationContext(),"STUDENTCONFIGBUILDER",updatedString);
                    Toast.makeText(getApplicationContext(), "Uploading the photo", Toast.LENGTH_SHORT).show();
                    uploadStudent(builderConfiguration.student);

                    //com.nitap.attende.MainActivity.faceEngine.registerFaceFeature(faceFeatureInfo);
                   // confirmUpdateDialog.cancel();
                    View var10 = findViewById(R.id.upload_btn);
                    Intrinsics.checkNotNullExpressionValue(var10, "findViewById<Button>(R.id.btnVerify)");
                    //((Button)var10).setEnabled(com.ttv.facerecog.MainActivity.Companion.getUserLists().size() > 0);
                    //submitButton.setEnabled(true);

                    // END
                     */
                } else {
                    var6 = (Collection)faceResults;
                    if (var6.size() > 1) {
                        Toast.makeText((Context)this, (CharSequence)"Multiple face detected!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText((Context)this, (CharSequence)"No face detected!", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception var13) {
                var13.printStackTrace();
            }
        }


        /*
        if (requestCode == 1 && resultCode == -1) {
            try {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                assert data != null;
                Uri file =  data.getData(); //Uri.fromFile(new File("path/to/images/rivers.jpg"));

                LoginActivity.shouldTrain =true;
                LoginActivity.myUri = file;
                LoginActivity.myRollno = "421243";

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                Thread.sleep(5000);
                StudentConfiguration builderConfiguration = MyUtils.getStudentConfigurationBuilder(getApplicationContext());
                //String bitmapString = MyUtils.getStringFromObject(bitmap);
                //test(bitmapString);
                assert builderConfiguration != null;
                assert builderConfiguration.student!=null;
                //builderConfiguration.student.bitmapString = bitmapString;

                String updatedString = MyUtils.getStringFromObject(builderConfiguration);
                MyUtils.saveString(getApplicationContext(),"STUDENTCONFIGBUILDER",updatedString);
                Toast.makeText(getApplicationContext(), "Uploading the photo", Toast.LENGTH_SHORT).show();
                uploadStudent(builderConfiguration.student);

                /*
                StorageReference riversRef = storageRef.child("students").child(MyUtils.getStudentConfigurationBuilder(this).student.rollno);
                UploadTask uploadTask = riversRef.putFile(file);

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(getApplicationContext(),"Storage Upload failed",Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        Toast.makeText(getApplicationContext(),"Storage Upload success",Toast.LENGTH_SHORT).show();
                    }
                });



                Context var10000 = (Context)this;
                Uri var10001 = data != null ? data.getData() : null;
                if (data == null) {
                    //display("DATA NULL");
                } else {
                    // display(data.getData().toString());
                }
                Intrinsics.checkNotNull(var10001);
                Bitmap var20 = ImageRotator.getCorrectlyOrientedImage(var10000, var10001);
                if (var20 == null) {
                    // display("BITMAP NULL");
                } else {
                    display(var20.toString());
                }
                Intrinsics.checkNotNullExpressionValue(var20, "ImageRotator.getCorrectl…Image(this, data?.data!!)");
                Bitmap bitmap = var20;
                List<FaceResult> var21 = com.nitap.attende.MainActivity.faceEngine.detectFace(bitmap);
                Intrinsics.checkNotNullExpressionValue(var21, "FaceEngine.getInstance(this).detectFace(bitmap)");
                if (var21 == null) {
                    //display("FACERESULT NULL");
                } else {
                    //display(Objects.toString(var21.size()));
                }
                final List faceResults = var21;
                Collection var6 = (Collection)faceResults;
                if (var6.size() == 1) {
                    com.nitap.attende.MainActivity.faceEngine.extractFeature(bitmap, true, faceResults);
                    // display("FEATURES EXTRACTED");
                    // StringCompanionObject var7 = StringCompanionObject.INSTANCE;
                    String var8 = "User%03d";
                    Object[] var22 = new Object[1];
                    ArrayList var10003 = userLists;

                    if (var10003 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("userLists");
                    }

                    var22[0] = var10003.size() + 1;
                    Object[] var9 = var22;
                    String var23 = String.format(var8, Arrays.copyOf(var9, var9.length));
                    Intrinsics.checkNotNullExpressionValue(var23, "java.lang.String.format(format, *args)");
                    String userName = var23;
                    Rect cropRect = Utils.getBestRect(bitmap.getWidth(), bitmap.getHeight(), ((FaceResult)faceResults.get(0)).rect);
                    final Bitmap headImg = Utils.crop(bitmap, cropRect.left, cropRect.top, cropRect.width(), cropRect.height(), 120, 120);
                    View inputView = LayoutInflater.from(this).inflate(R.layout.dialog_input_view, (ViewGroup)null, false);
                    //final EditText editText = (EditText)inputView.findViewById(R.id.et_user_name);
                    ImageView ivHead = (ImageView)inputView.findViewById(R.id.iv_head);
                    ivHead.setImageBitmap(headImg);
                    // editText.setText((CharSequence)userName);
                    // Context var10002 = this.context;
                    //Intrinsics.checkNotNull(var10002);
                    AlertDialog var24 = (new AlertDialog.Builder(this)).setView(inputView).setPositiveButton((CharSequence)"OK", (DialogInterface.OnClickListener)null).setNegativeButton((CharSequence)"Cancel", (DialogInterface.OnClickListener)null).create();
                    Intrinsics.checkNotNullExpressionValue(var24, "AlertDialog.Builder(cont…                .create()");
                    final AlertDialog confirmUpdateDialog = var24;
                    confirmUpdateDialog.show();
                    //display("ALERT SHOWED");
                    confirmUpdateDialog.getButton(-1).setOnClickListener((View.OnClickListener)(new View.OnClickListener() {
                        public final void onClick(@Nullable View v) {




                        }
                    }));
                    confirmUpdateDialog.cancel();
                    // START

                    // EditText var10000 = editText;
                    Intrinsics.checkNotNullExpressionValue(var10000, "editText");
                    String s = facetagForFaceInfo;//var10000.getText().toString();

                    boolean exists = false;
                    Iterator var5 = com.ttv.facerecog.MainActivity.Companion.getUserLists().iterator();

                    while(var5.hasNext()) {
                        FaceEntity user = (FaceEntity)var5.next();
                        if (TextUtils.equals((CharSequence)user.userName, (CharSequence)s)) {
                            exists = true;
                            break;
                        }
                    }


                    DBHelper var91 = mydb;
                    Intrinsics.checkNotNull(var9);
                    int user_id = var91.insertUser(s, headImg, ((FaceResult)faceResults.get(0)).feature);
                    FaceEntity face = new FaceEntity(user_id, s, headImg, ((FaceResult)faceResults.get(0)).feature);
                    com.ttv.facerecog.MainActivity.Companion.getUserLists().add(face);

                    FaceFeatureInfo faceFeatureInfo = new FaceFeatureInfo(user_id, ((FaceResult)faceResults.get(0)).feature);
                    StudentConfiguration builderConfiguration = MyUtils.getStudentConfigurationBuilder(getApplicationContext());
                    String bitmapString = MyUtils.getStringFromObject(bitmap);
                    test(bitmapString);
                    assert builderConfiguration != null;
                    assert builderConfiguration.student!=null;
                    builderConfiguration.student.bitmapString = bitmapString;

                    String updatedString = MyUtils.getStringFromObject(builderConfiguration);
                    MyUtils.saveString(getApplicationContext(),"STUDENTCONFIGBUILDER",updatedString);
                    Toast.makeText(getApplicationContext(), "Uploading the photo", Toast.LENGTH_SHORT).show();
                    uploadStudent(builderConfiguration.student);

                    //com.nitap.attende.MainActivity.faceEngine.registerFaceFeature(faceFeatureInfo);
                    confirmUpdateDialog.cancel();
                    View var10 = findViewById(R.id.upload_btn);
                    Intrinsics.checkNotNullExpressionValue(var10, "findViewById<Button>(R.id.btnVerify)");
                    //((Button)var10).setEnabled(com.ttv.facerecog.MainActivity.Companion.getUserLists().size() > 0);
                    //submitButton.setEnabled(true);

                    // END

                } else {
                    var6 = (Collection)faceResults;
                    if (var6.size() > 1) {
                        Toast.makeText((Context)this, (CharSequence)"Multiple face detected!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText((Context)this, (CharSequence)"No face detected!", Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (Exception var13) {
                var13.printStackTrace();
            }
        }
*/


        super.onActivityResult(requestCode, resultCode, data);

        ////////

    }

    private void uploadPhoto(Context applicationContext, Uri data, String rollno,FaceFeatureInfo faceFeatureInfo) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("students").child(rollno);
        Uri file = data;
        //StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
        UploadTask uploadTask = imagesRef.putFile(file);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                display("successfully uploaded photo to firebase storage");
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        display("got download url");
                        String photoUrl = uri.toString();
                        String faceFeatureInfoString = MyUtils.getStringFromObject(faceFeatureInfo.getFeatureData());

                        MyConfiguration myConfiguration = MyUtils.getConfigurationBuilder(getApplicationContext());
                        myConfiguration.student.photoUrl = photoUrl;
                        myConfiguration.student.faceFeatureInfoString = faceFeatureInfoString;

                        MyUtils.saveConfigurationBuilder(getApplicationContext(),myConfiguration);

                        MyStudent student = new MyStudent();
                        student.email = myConfiguration.student.email;
                        student.rollno = myConfiguration.student.rollno;
                        student.regno = myConfiguration.student.regno;
                        student.name = myConfiguration.student.name;
                        student.deviceHash = myConfiguration.student.deviceHash;
                        student.sectionId = myConfiguration.student.sectionId;
                        student.faceFeatureInfoString = myConfiguration.student.faceFeatureInfoString;
                        student.photoUrl = myConfiguration.student.photoUrl;

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("students").child(student.rollno);
                        ref.setValue(student).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                display("ALL SUCCESS");
                                display("REGISTERED WITH FACE ENGINE");

                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                display("couldnt upload Mystudent");
                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        display("Failed to get download url");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    display("couldnt upload photo to firebase storage");

            }
        });




    }

    private void test(String faceInfoString) {
        FaceFeatureInfo faceinfo = MyUtils.getFaceFeatureInfo(this,faceInfoString);
        String newFaceString = MyUtils.getStringFromObject(faceinfo);
        if(faceInfoString.equals(newFaceString)) {
            Toast.makeText(this, "BOTH SAME", Toast.LENGTH_SHORT).show();
            FaceFeatureInfo newInfo = MyUtils.getFaceFeatureInfo(this,newFaceString);
            assert faceinfo != null;
            assert newInfo != null;
            if (Arrays.equals(faceinfo.getFeatureData(), newInfo.getFeatureData())) {
                Toast.makeText(this, "FEATURE DATA ALSO SAME", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "FEATURE DATA IS DIFFERENT", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "BOTH DIFFERENT", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, faceInfoString, Toast.LENGTH_LONG).show();
            Toast.makeText(this, newFaceString, Toast.LENGTH_LONG).show();
        }
    }

    private void uploadStudent(Student student) {
        Toast.makeText(getApplicationContext(), "UploadStudent called", Toast.LENGTH_SHORT).show();
        assert student!=null;
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("students").child(student.rollno);
        courseRef.setValue(student).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Uploaded photo successfully", Toast.LENGTH_SHORT).show();
                StudentConfiguration studentConfiguration = MyUtils.getStudentConfigurationBuilder(getApplicationContext());
                assert studentConfiguration != null;
                String finalConfigString = MyUtils.getString(getApplicationContext(),"STUDENTCONFIGBUILDER");


               // studentConfiguration.student = student;
               // String updatedConfigString = MyUtils.getStringFromObject(studentConfiguration);
                //MyUtils.removeAll(getApplicationContext());
                //String faceinfoString = studentConfiguration.student.faceFeatureInfoString;
                //FaceFeatureInfo faceFeatureInfo = MyUtils.getFaceFeatureInfo(getApplicationContext(),faceinfoString);
                //com.nitap.attende.MainActivity.faceEngine.registerFaceFeature(faceFeatureInfo);
                //MyUtils.saveString(getApplicationContext(),"STUDENTCONFIG",updatedConfigString);
                getStudentDetails();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error, Retry uploading photo again", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void getStudentDetails() {
        /*
        String configString = MyUtils.getString(getApplicationContext(),"STUDENTBUILDERCONFIG");
        MyUtils.saveString(getApplicationContext(),"STUDENTCONFIG",configString);
        MyUtils.removeString(getApplicationContext(),"STUDENTBUILDERCONFIG");
        assert MyUtils.getStudentConfiguration(this)!=null;
        startActivity(new Intent(this,HomeActivity.class));
        finish();
        */
        MyUtils.removeAll(getApplicationContext());
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}