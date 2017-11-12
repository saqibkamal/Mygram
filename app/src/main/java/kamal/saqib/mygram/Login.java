package kamal.saqib.mygram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    EditText email;
    EditText password;
    TextView register, resetpassword, login;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    RelativeLayout relativeLayout;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009a9a")));

        email = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (TextView) findViewById(R.id.login);
        register = (TextView) findViewById(R.id.register);
        resetpassword = (TextView) findViewById(R.id.resetpassword);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        if (firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }


        register.setOnClickListener(this);
        login.setOnClickListener(this);
        password.setOnKeyListener(this);
        resetpassword.setOnClickListener(this);
    }

    public void login() {
        progressDialog.setMessage("Signing In ....");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        final String username = email.getText().toString();
        final String pass = password.getText().toString();

        if (username.length() == 0) {
            Toast.makeText(getApplicationContext(), "Enter Your Email", Toast.LENGTH_LONG).show();
            progressDialog.hide();
            return;
        }
        if (password.length() == 0) {
            Toast.makeText(getApplicationContext(), "Enter your password", Toast.LENGTH_SHORT).show();
            progressDialog.hide();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(username, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.hide();
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class).putExtra("Email",username));
                } else
                    Toast.makeText(getApplicationContext(), "Incorrect Credentials", Toast.LENGTH_LONG).show();
            }
        });


    }


    @Override
    public void onClick(View v) {
        if (v == login)
            login();
        else if (v == relativeLayout || v == linearLayout) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } else if (v == register) {
            //finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));

        }
        if (v == resetpassword) {
            startActivity(new Intent(getApplicationContext(), reset_password.class));
        }

    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)
            login();
        return false;
    }


}
