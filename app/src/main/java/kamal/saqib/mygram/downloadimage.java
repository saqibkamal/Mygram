package kamal.saqib.mygram;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class downloadimage extends AppCompatActivity {

    ImageView imageView;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference, childref;
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

        final android.support.v7.app.ActionBar actionBar =getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009a9a")));




        final GridView gridView = (GridView) findViewById(R.id.gridview);


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


        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
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
                                Log.i("IMAGEURL",imageurlist.get(position));
                                StorageReference photoRef = firebaseStorage.getReferenceFromUrl(imageurlist.get(position));

                                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.i("Deleted ","Successfully");
                                    }
                                });
                            }
                        }).show().setIcon(R.drawable.alertboximage);
                return true;
            }
        });
    }


    public void updateprofilepic() {

        userinfo.setDefaultprofilepic(defaultprofilepic);
        databaseReference.child(user.getUid()).setValue(userinfo);


    }






    public class ImageListAdapter extends ArrayAdapter{

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
