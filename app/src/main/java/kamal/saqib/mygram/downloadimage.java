package kamal.saqib.mygram;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class downloadimage extends AppCompatActivity {


    FirebaseStorage firebaseStorage;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    ArrayList<String> imageurlist;
    ProgressDialog progressDialog;
    int defaultprofilepic;
    Userinfo userinfo;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadimage);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009a9a")));


        final GridView gridView = (GridView) findViewById(R.id.gridview);

        progressDialog = new ProgressDialog(this);


        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = firebaseAuth.getCurrentUser();
                userinfo = dataSnapshot.child(user.getUid()).getValue(Userinfo.class);
                imageurlist = userinfo.get_urllist();
                defaultprofilepic = userinfo.getDefaultprofilepic();


                gridView.setAdapter(
                        new ImageListAdapter(downloadimage.this, imageurlist)
                );


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent i = new Intent(getApplicationContext(), show_single_image.class);
                i.putExtra("Url", imageurlist.get(position));
                startActivity(i);
            }
        });

        final CharSequence options[] = new CharSequence[]{"Set As Profie Picture", "Delete"};


        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(downloadimage.this);
                builder.setTitle("Choose an option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {

                            new android.support.v7.app.AlertDialog.Builder(downloadimage.this).setTitle("Profile Picture").
                                    setMessage("Do you want to set this Image as Profile Picture").
                                    setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            defaultprofilepic = position;
                                            updateprofilepic();
                                        }
                                    }).
                                    setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (defaultprofilepic == position) {
                                                defaultprofilepic = -1;
                                                updateprofilepic();
                                            }
                                        }

                                    }).show().setIcon(R.drawable.alertboximage);
                        } else if (which == 1) {

                            progressDialog.setMessage("Deleting");
                            progressDialog.show();
                            StorageReference photoRef = firebaseStorage.getReferenceFromUrl(imageurlist.get(position));

                            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                            Userinfo userinfo = dataSnapshot.child(user.getUid()).getValue(Userinfo.class);
                                            userinfo.remove_url(position);
                                            databaseReference.child(user.getUid()).setValue(userinfo);
                                            progressDialog.hide();
                                            finish();
                                            startActivity(new Intent(getApplicationContext(), downloadimage.class));

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Unable To delete the photo", Toast.LENGTH_SHORT).show();
                                    progressDialog.hide();
                                }
                            });
                        }

                    }
                });
                builder.show();
                return true;
            }
        });
    }


    public void updateprofilepic() {

        userinfo.setDefaultprofilepic(defaultprofilepic);
        databaseReference.child(user.getUid()).setValue(userinfo);


    }


    public class ImageListAdapter extends ArrayAdapter {

        private Context context;
        private LayoutInflater inflater;

        ArrayList<String> imageUrls;

        public ImageListAdapter(Context context, ArrayList<String> imageUrls) {
            super(context, R.layout.image_view, imageUrls);

            this.context = context;
            this.imageUrls = imageUrls;

            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.image_view, parent, false);
            }

            RequestOptions options = new RequestOptions();
            options.centerCrop();

            Glide.with(context)
                    .load(imageUrls.get(position)).apply(options)
                    .into((ImageView) convertView);

            return convertView;
        }
    }
}
