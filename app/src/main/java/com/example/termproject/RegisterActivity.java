package com.example.termproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.termproject.Models.DBHelper;
import com.example.termproject.Models.UserModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    Button register;
    EditText email;
    EditText username;
    EditText password;
    EditText confPass;
    String add;
    Uri imageUrl;
    FloatingActionButton selectPhoto;
    CircleImageView profPic;
    long lat;
    long longi;
    DBHelper dbHelper;
    FusedLocationProviderClient provider;
    //DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReferenceFromUrl("https://termproject-chatapp-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //bind all data to respective fields
        bindFields();
        //location workings
        if(ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            provider.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location loc = task.getResult();
                    if(loc!=null)
                    {
                        lat = Double.valueOf(loc.getLatitude()).longValue();
                        longi = Double.valueOf(loc.getLongitude()).longValue();
                    }
                    else {
                         lat = Double.valueOf(31.526542688148975).longValue();
                         longi = Double.valueOf(74.28628631738394).longValue();
                    }
                }
            });
        }
        else {
            ActivityCompat.requestPermissions(RegisterActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
            ActivityCompat.recreate(RegisterActivity.this);
        }


        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picIntent = new Intent();
                picIntent.setType("image/*");
                picIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(picIntent,1);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               registerAction();
            }
        });
        TextView txtview = findViewById(R.id.tologin);
        txtview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(RegisterActivity.this,loginActivity.class);
                startActivity(in);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data!=null && data.getData()!=null)
        {
            imageUrl = data.getData();
            profPic.setImageURI(imageUrl);
        }
    }

    public void registerAction()
    {
        add = "";
        String emailtext = email.getText().toString().trim();
        String usernametext = username.getText().toString();
        String passwordtext = password.getText().toString().trim();
        String confPassword = confPass.getText().toString().trim();
        if (emailtext.isEmpty() || usernametext.isEmpty() || passwordtext.isEmpty() || confPassword.isEmpty()
                || !passwordtext.equals(confPassword))
        {
            email.setError("Email Required");
            Toast.makeText(this, "No field Should be empty!", Toast.LENGTH_SHORT).show();
        }
        else {

            dbHelper.registerUser(emailtext,passwordtext).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Geocoder geocoder = new Geocoder(RegisterActivity.this, Locale.getDefault());
                        add="";
                        try {
                            while (add.equals("")) {
                                List<Address> li = geocoder.getFromLocation(lat, longi, 1);
                                add = li.get(0).getLocality() + " , " + li.get(0).getAdminArea() + " , " + li.get(0).getCountryName();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ProgressDialog progressBar = new ProgressDialog(RegisterActivity.this);
                        progressBar.setTitle("Uploading Image.......");
                        progressBar.show();
                        if(imageUrl == null)
                        {
                            imageUrl = Uri.parse("https://firebasestorage.googleapis.com/v0/b/termproject-chatapp.appspot.com/o/defaultpfp.jpg?alt=media&token=64c64c43-c0fb-4ea7-b1aa-9cb3fe7ea70f");
                            UserModel userModel1 = new UserModel(lat,longi,usernametext,add,dbHelper.getUID(),imageUrl.toString());
                            if(dbHelper.registerUserData(userModel1)) {
                                progressBar.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, NearbyUserList.class);
                                intent.putExtra("uid",dbHelper.getUID());
                                startActivity(intent);
                                finish();
                            }
                        }
                        else{
                            UserModel user = new UserModel(lat,longi,usernametext,add,dbHelper.getUID(),imageUrl.toString());
                            Uri uri = Uri.parse(user.getImageURL());

                            String extension;

                            //Check uri format to avoid null
                            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                                //If scheme is a content
                                final MimeTypeMap mime = MimeTypeMap.getSingleton();
                                extension = mime.getExtensionFromMimeType(RegisterActivity.this.getContentResolver().getType(uri));
                            } else {
                                //If scheme is a File
                                //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
                                extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

                            }

                            StorageReference ref = dbHelper.storageReference.child(user.getUid() + "." + extension);
                            ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(@NonNull Uri uri) {
                                            UserModel userModel = new UserModel(user.getLat()
                                                    ,user.getLongi(),usernametext,add,dbHelper.getUID(),uri.toString());
                                            if(dbHelper.registerUserData(userModel)) {
                                                progressBar.dismiss();
                                                Intent intent = new Intent(RegisterActivity.this, loginActivity.class);
                                                intent.putExtra("user", emailtext);
                                                intent.putExtra("pass", passwordtext);
                                                intent.putExtra("uid", dbHelper.getUID());
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                    double progress = (100.00 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                                    progressBar.setMessage("Uploading: " + (int)progress + "%");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.dismiss();
                                    Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                    else
                        {
                            Toast.makeText(RegisterActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                }
            });
        }

    }

    public void bindFields()
    {
        register = findViewById(R.id.register);
        email = findViewById(R.id.email);
        username = findViewById(R.id.regUserName);
        password = findViewById(R.id.regPassword);
        confPass = findViewById(R.id.confPass);
        profPic = findViewById(R.id.profPic);
        selectPhoto = findViewById(R.id.addPicture);
        provider = LocationServices.getFusedLocationProviderClient(this);
        lat = Double.valueOf(31.526542688148975).longValue();
        longi = Double.valueOf(74.28628631738394).longValue();
        dbHelper = new DBHelper();
    }
}