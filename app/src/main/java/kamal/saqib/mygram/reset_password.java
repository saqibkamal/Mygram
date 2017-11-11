package kamal.saqib.mygram;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class reset_password extends AppCompatActivity implements View.OnClickListener {

    TextView submit;
    EditText email;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        email=(EditText) findViewById(R.id.email);
        submit=(TextView) findViewById(R.id.resetpassword);

        submit.setOnClickListener(this);

        final android.support.v7.app.ActionBar actionBar =getSupportActionBar();

        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009a9a")));


    }

    @Override
    public void onClick(View v) {
        if(v==submit){


            FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {


                                new android.support.v7.app.AlertDialog.Builder(reset_password.this).setTitle("Email Sent").
                                        setMessage("Please Check Your Email to reset your password.").
                                        setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                                startActivity(new Intent(getApplicationContext(),Login.class));
                                            }
                                        }).show().setIcon(R.drawable.emailsent);
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Please Enter A valid Email",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }
    }
}
