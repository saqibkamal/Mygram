package kamal.saqib.mygram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    EditText email;
    EditText password, repassword;
    TextView signin, register;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    EditText name, address;
    DatabaseReference databaseReference;
    String na, add;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    Userinfo userinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        register = (TextView) findViewById(R.id.register);
        signin = (TextView) findViewById(R.id.signin);
        name = (EditText) findViewById(R.id.name);
        address = (EditText) findViewById(R.id.address);
        repassword = (EditText) findViewById(R.id.repassword);


        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009a9a")));


        register.setOnClickListener(this);
        signin.setOnClickListener(this);
        address.setOnKeyListener(this);
    }

    public void registernew() {
        progressDialog.setMessage("Registering ....");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        final String username = email.getText().toString();
        final String pass = password.getText().toString();
        final String repass = repassword.getText().toString();

        na = name.getText().toString();
        add = address.getText().toString();

        if (!pass.equals(repass)) {
            Toast.makeText(getApplicationContext(), "Password Didn't Match ", Toast.LENGTH_LONG).show();
            progressDialog.hide();
            return;
        }


        if (username.length() == 0 || password.length() == 0 || na.length() == 0 || add.length() == 0) {
            Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();
            progressDialog.hide();
            return;
        }


        firebaseAuth.createUserWithEmailAndPassword(username, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    userinfo = new Userinfo(na, add);
                    databaseReference.child(user.getUid()).setValue(userinfo);

                    progressDialog.hide();
                    Toast.makeText(getApplicationContext(), "Registration Complete", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

                } else {
                    progressDialog.hide();
                    Toast.makeText(getApplicationContext(), "Something Went Wrong while logging in", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        if (v == register) {
            registernew();

        } else if (v == signin) {
            startActivity(new Intent(getApplicationContext(), Login.class));
        }

    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)
            registernew();

        return false;
    }


}
