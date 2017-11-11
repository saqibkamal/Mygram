package kamal.saqib.mygram;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class show_single_image extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_single_image);

        final android.support.v7.app.ActionBar actionBar =getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009a9a")));

        ImageView imageView=(ImageView) findViewById(R.id.imageView);
        Intent i=getIntent();
        String x = i.getExtras().getString("Url");


        Glide.with(this).load(x).into(imageView);
    }
}
