package kamal.saqib.mygram;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.lang.Object;
import java.util.ArrayList;
import java.util.Random;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    TextView Email, Username, address, following, following_text, follower, follower_text;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    String username, addr, email, locationprovider;
    Double lat, lon;
    FloatingActionButton fab, fab1, fab2;
    boolean isFABOpen;
    ImageView profilepic, viewimage, viewuser, give_user_per;
    int ppno;
    ArrayList<String> urllist;
    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationprovider = locationManager.getBestProvider(new Criteria(), false);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationprovider);




        Email = (TextView) findViewById(R.id.email);
        Username = (TextView) findViewById(R.id.name);
        profilepic = (ImageView) findViewById(R.id.profilepic);
        viewimage = (ImageView) findViewById(R.id.view_images);
        viewuser = (ImageView) findViewById(R.id.view_user);
        address = (TextView) findViewById(R.id.address);
        give_user_per = (ImageView) findViewById(R.id.give_user_permission);
        following = (TextView) findViewById(R.id.following);
        following_text = (TextView) findViewById(R.id.followingtext);
        follower = (TextView) findViewById(R.id.follower);
        follower_text = (TextView) findViewById(R.id.followertext);

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), show_single_image.class);
                if (ppno != -1)
                    i.putExtra("Url", urllist.get(ppno));
                else
                    i.putExtra("Url", "-1");
                startActivity(i);
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        email = firebaseAuth.getCurrentUser().getEmail().toString();
        Email.setText(email);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009a9a")));


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);

        viewuser.setOnClickListener(this);
        viewimage.setOnClickListener(this);
        give_user_per.setOnClickListener(this);

        final Animation fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        final Animation fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        isFABOpen = false;
        closeFABMenu();
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab.setSize(20);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    fab1.startAnimation(fab_open);
                    fab2.startAnimation(fab_open);
                    showFABMenu();
                } else {
                    fab2.startAnimation(fab_close);
                    fab1.startAnimation(fab_close);
                    closeFABMenu();
                }
            }
        });




        databaseReference = FirebaseDatabase.getInstance().getReference();

        lat=location.getLatitude();
        lon=location.getLongitude();


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Userinfo userinfo = dataSnapshot.child(user.getUid()).getValue(Userinfo.class);
                userinfo.setlatlon(lat, lon);
                databaseReference.child(user.getUid()).setValue(userinfo);


 //               Toast.makeText(getApplicationContext(), lat.toString() + lon.toString(), Toast.LENGTH_SHORT).show();

                int no_of_followers = 0;
                for (DataSnapshot userp : dataSnapshot.getChildren()) {
                    Userinfo temp = userp.getValue(Userinfo.class);
                    if (temp.get_allowed_userlist().contains(email))
                        no_of_followers++;
                }

                username = userinfo.getname();
                addr = userinfo.getAddress();
                ppno = userinfo.getDefaultprofilepic();
                username = toTitleCase(username);
                urllist = userinfo.get_urllist();
                following.setText(Integer.toString(userinfo.get_allowed_userlist().size()));

                Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/abc.otf");
                Typeface typeface1 = Typeface.createFromAsset(getAssets(), "fonts/abc1.otf");

                Username.setTypeface(typeface);
                Username.setText(username);
                address.setText(addr);
                address.setTypeface(typeface1);
                Email.setTypeface(typeface1);
                following.setTypeface(typeface);
                following_text.setTypeface(typeface);
                follower.setText(Integer.toString(no_of_followers));
                follower.setTypeface(typeface);
                follower_text.setTypeface(typeface);


                if (ppno >= 0) {
                    RequestOptions options = new RequestOptions();
                    options.fitCenter();
                    Glide.with(getApplicationContext()).load(urllist.get(ppno)).apply(options).into(profilepic);
                } else
                    profilepic.setImageResource(R.drawable.user_user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View v) {

        if (v == fab1) {
            startActivity(new Intent(getApplicationContext(), InsertImage.class));

        } else if (v == fab2) {
            startActivity(new Intent(getApplicationContext(), capture_image.class));
        } else if (v == viewuser) {
            Intent i = new Intent(getApplicationContext(), UsersList.class);
            i.putExtra("username", username);
            i.putExtra("address", addr);
            i.putExtra("email", email);
            startActivity(i);
        } else if (v == viewimage) {
            startActivity(new Intent(getApplicationContext(), downloadimage.class));

        } else if (v == give_user_per) {
            Intent i = new Intent(getApplicationContext(), give_user_permission.class);
            i.putExtra("username", username);
            i.putExtra("address", addr);
            i.putExtra("email", email);


            startActivity(i);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.log_out) {
            new android.support.v7.app.AlertDialog.Builder(ProfileActivity.this).setTitle("Log Out").
                    setMessage("Are you sure you want to Log out ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Logged Out Succesfully", Toast.LENGTH_LONG).show();
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();


            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public void showFABMenu() {
        isFABOpen = true;
        fab1.setVisibility(View.VISIBLE);
        fab2.setVisibility(View.VISIBLE);
    }

    public void closeFABMenu() {
        isFABOpen = false;
        fab1.setVisibility(View.GONE);
        fab2.setVisibility(View.GONE);
    }


    public static String toTitleCase(String s) {
        char a = s.charAt(0);
        a = Character.toUpperCase(a);
        String str = Character.toString(a);
        for (int i = 1; i < s.length(); i++) {
            a = s.charAt(i);
            if (a == ' ')
                str = str + Character.toString(a) + Character.toUpperCase(s.charAt(++i));
            else
                str = str + (Character.toLowerCase(a));
        }
        return str;
    }

    @Override
    public void onBackPressed() {
        new android.support.v7.app.AlertDialog.Builder(ProfileActivity.this).setTitle("Exit").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setMessage("Do you want to exit").show();


    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Userinfo userinfo = dataSnapshot.child(user.getUid()).getValue(Userinfo.class);
                userinfo.setlatlon(lat, lon);
                databaseReference.child(user.getUid()).setValue(userinfo);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(locationprovider, 400, 1, this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
