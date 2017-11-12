package kamal.saqib.mygram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class InsertImage extends AppCompatActivity implements View.OnClickListener {
    ImageView imageview;
    Button submit, retry;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    Uri selectedimagepath;
    Bitmap selectedimage;
    String imagename;
    ProgressDialog progressDialog;
    Uri url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_image);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009a9a")));

        imageview = (ImageView) findViewById(R.id.imageview);
        submit = (Button) findViewById(R.id.submit);
        retry = (Button) findViewById(R.id.retry);
        progressDialog = new ProgressDialog(this);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        submit.setOnClickListener(this);
        retry.setOnClickListener(this);

        insertpic();


    }

    public void insertpic() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1);
    }

    @Override
    public void onClick(View v) {

        if (v == retry)
            insertpic();
        else if (v == submit) {

            if (selectedimagepath == null) {
                Toast.makeText(getApplicationContext(), "First Select An image", Toast.LENGTH_SHORT).show();
            } else {

                progressDialog.setMessage("Uploading");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);

                StorageReference childRef = storageReference.child(imagename);
                UploadTask uploadTask = childRef.putFile(selectedimagepath);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") final Uri url1 = taskSnapshot.getDownloadUrl();
                        url = url1;
                        Toast.makeText(getApplicationContext(), "Upload Successful", Toast.LENGTH_SHORT).show();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();

                    }
                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                Userinfo userinfo = dataSnapshot.child(user.getUid()).getValue(Userinfo.class);
                                userinfo.add_url(url.toString());
                                databaseReference.child(user.getUid()).setValue(userinfo);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        progressDialog.dismiss();
                        finish();
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    }
                });


            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedimagepath = data.getData();

            try {
                selectedimage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedimagepath);
                imageview.setImageBitmap(selectedimage);

                String i = selectedimagepath.toString();

                imagename = i.substring(i.lastIndexOf("/") + 1);

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
