package kamal.saqib.mygram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class show_others_images extends AppCompatActivity {
    ArrayList<String> url_list;
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_others_images);

        final android.support.v7.app.ActionBar actionBar =getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#009a9a")));

        Bundle b=this.getIntent().getExtras();
        url_list=b.getStringArrayList("Urls");

        gridView=(GridView) findViewById(R.id.gridview);

        gridView.setAdapter(
                new ImageListAdapter(show_others_images.this, url_list)
        );

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                Intent i=new Intent(getApplicationContext(),show_single_image.class);
                i.putExtra("Url",url_list.get(position));
                startActivity(i);
            }
        });


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

            Glide.with(context)
                    .load(imageUrls.get(position))
                    .into((ImageView) convertView);

            return convertView;
        }
    }
}
