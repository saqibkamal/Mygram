package kamal.saqib.mygram;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class give_user_permission extends AppCompatActivity {

    ListView listView;
    ArrayList<String> array_name, array_address,array_email,array_allowed_email;
    ArrayList<Integer> array_profile_pic;
    ArrayList<ArrayList<String>> url_array;
    ArrayAdapter<String> adapter;
    FirebaseStorage firebaseStorage;
    DatabaseReference databaseReference;
    String current_user,current_address,current_email;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_user_permission);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009a9a")));

        listView = (ListView) findViewById(R.id.list);
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        array_name = new ArrayList<String>();
        array_address = new ArrayList<String>();
        array_profile_pic = new ArrayList<Integer>();
        url_array = new ArrayList<ArrayList<String>>();
        array_email=new ArrayList<String>();
        array_allowed_email=new ArrayList<>();
        Intent i = getIntent();
        current_user = i.getStringExtra("username");
        current_address =i.getStringExtra("address");
        current_email=i.getStringExtra("email");
        Log.i("EMAIL",current_email);
        firebaseAuth = FirebaseAuth.getInstance();





        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                array_name.clear();
                array_profile_pic.clear();
                array_address.clear();
                url_array.clear();
                FirebaseUser userr = firebaseAuth.getCurrentUser();
                Userinfo xx = dataSnapshot.child(userr.getUid()).getValue(Userinfo.class);
                array_allowed_email=xx.get_allowed_userlist();

                for (DataSnapshot user : dataSnapshot.getChildren()) {

                    Userinfo userinfo = user.getValue(Userinfo.class);
                    String nam = userinfo.getname();
                    String add=userinfo.getAddress();
                    String em=userinfo.getEmail();
                    ArrayList<String> x = userinfo.get_urllist();
                    if (em.equals(current_email))
                        continue;
                    if (array_allowed_email.contains(em))
                        continue;
                    nam = toTitleCase(nam);
                    array_name.add(nam);
                    array_address.add(toTitleCase(userinfo.getAddress()));
                    url_array.add(x);
                    array_profile_pic.add(userinfo.getDefaultprofilepic());
                    array_email.add(em);

                }

                give_user_permission.display_adapter adapter = new give_user_permission.display_adapter();
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Userinfo userinfo = dataSnapshot.child(user.getUid()).getValue(Userinfo.class);
                        userinfo.add_allowed_user(array_email.get(position));
                        array_email.remove(position);
                        array_address.remove(position);
                        array_name.remove(position);
                        url_array.remove(position);
                        array_profile_pic.remove(position);

                        databaseReference.child(user.getUid()).setValue(userinfo);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


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

    public class display_adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return url_array.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.layout_for_list, null);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView2);
            TextView textView1 = (TextView) convertView.findViewById(R.id.text1);
            TextView textView2 = (TextView) convertView.findViewById(R.id.text2);

            textView1.setText(array_name.get(position));
            textView2.setText(array_address.get(position));
            int x = array_profile_pic.get(position);
            ArrayList<String> xx = url_array.get(position);

            if (x != -1 && xx.size() != 0)
                Glide.with(getApplicationContext()).load(xx.get(x)).into(imageView);

            return convertView;
        }

    }
}
