package kamal.saqib.mygram;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import java.net.InetAddress;


public class splash_activity extends AppCompatActivity {


    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_activity);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                if (isNetworkAvailable()==false) {
                    new android.support.v7.app.AlertDialog.Builder(splash_activity.this).setTitle("Error").
                            setMessage("Your Device has no Internet Connection").setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                            homeIntent.addCategory(Intent.CATEGORY_HOME);
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(homeIntent);
                        }
                    }).show();
                }
                else {
                /* Create an Intent that will start the Menu-Activity. */
                    Intent mainIntent = new Intent(splash_activity.this, Login.class);
                    splash_activity.this.startActivity(mainIntent);
                }

            }
        }, SPLASH_DISPLAY_LENGTH);
    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
    }


