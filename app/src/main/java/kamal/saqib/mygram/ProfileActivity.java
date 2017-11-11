package kamal.saqib.mygram;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
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

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    TextView Email,Username,Address;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    String username,addr,profilepicaddr;
    ActionBar actionBar;
    FloatingActionButton fab,fab1,fab2;
    boolean isFABOpen;
    ImageView profilepic;
    int ppno;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Email=(TextView) findViewById(R.id.text);
        Username=(TextView) findViewById(R.id.name);
        profilepic=(ImageView) findViewById(R.id.profilepic);
        //Address=(TextView) findViewById(R.id.name);




        firebaseAuth= FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();

        final android.support.v7.app.ActionBar actionBar =getSupportActionBar();

        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009a9a")));



        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);

        final Animation fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        final Animation fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        isFABOpen=false;
        closeFABMenu();
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab.setSize(20);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    fab1.startAnimation(fab_open);
                    fab2.startAnimation(fab_open);
                    showFABMenu();
                }else{
                    fab2.startAnimation(fab_close);
                    fab1.startAnimation(fab_close);
                    closeFABMenu();
                }
            }
        });







        //Email.setText(firebaseAuth.getCurrentUser().getEmail().toString());

        databaseReference= FirebaseDatabase.getInstance().getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                Userinfo userinfo=dataSnapshot.child(user.getUid()).getValue(Userinfo.class);

                username=userinfo.getname();
                addr=userinfo.getAddress();
                ppno=userinfo.getDefaultprofilepic();
               username=toTitleCase(username);

                ArrayList<String> urllist=userinfo.get_urllist();

              //  Random rand=new Random();
               // if(ppno<0 && urllist.size()!=0){
                 //   while(ppno<0)
                  //      ppno=rand.nextInt(urllist.size());
                //}

                

                //actionBar.setTitle(username);
                Username.setText(username);
                //Address.setText(addr);
               // Log.i("IMAGEURL",profilepicaddr);

                if(ppno>=0) {
                    RequestOptions options = new RequestOptions();
                    options.fitCenter();
                    Glide.with(getApplicationContext()).load(urllist.get(ppno)).apply(options).into(profilepic);
                }
                else profilepic.setImageResource(R.drawable.user_user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







    }

    /*public void savedata()
    {
        String na=name.getText().toString();
        String add=address.getText().toString();

        Userinfo userinfo=new Userinfo(na,add);

        FirebaseUser user=firebaseAuth.getCurrentUser();

        databaseReference.child(user.getUid()).setValue(userinfo);

        Toast.makeText(this,"Information Saved",Toast.LENGTH_SHORT).show();

    }*/

    @Override
    public void onClick(View v) {

        if(v==fab1){
            startActivity(new Intent(getApplicationContext(), InsertImage.class));

        }
        if(v==fab2){
            startActivity(new Intent(getApplicationContext(),capture_image.class));
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();

        if(id==R.id.show_user){
            Intent i=new Intent(getApplicationContext(),UsersList.class);
            i.putExtra("username",username);

            startActivity(i);

            return true;
        }

        else if(id==R.id.view_images){
            startActivity(new Intent(getApplicationContext(),downloadimage.class));
            return true;
        }
        else if(id==R.id.log_out){
            Toast.makeText(getApplicationContext(),"Logged Out Succesfully" ,Toast.LENGTH_LONG).show();
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(),Login.class));
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public void showFABMenu(){
        isFABOpen=true;
        fab1.setVisibility(View.VISIBLE);
        fab2.setVisibility(View.VISIBLE);
    }

    public void closeFABMenu(){
        isFABOpen=false;
        fab1.setVisibility(View.GONE);
        fab2.setVisibility(View.GONE);
    }


    public static String toTitleCase(String s) {
        char a=s.charAt(0);
        a=Character.toUpperCase(a);
        String str =Character.toString(a);
        for(int i =1;i<s.length();i++) {
            a = s.charAt(i);
            if(a==' ')
                str = str+Character.toString(a)+Character.toUpperCase(s.charAt(++i));
            else
                str =str+(Character.toLowerCase(a));
        }
        return str;
    }
}