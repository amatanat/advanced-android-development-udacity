package com.example.android.android_me.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.android.android_me.R;

public class MainActivity extends AppCompatActivity implements MasterListFragment.OnImageClicked{

    private int headIndex;
    private int bodyIndex;
    private int legIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void OnImageSelected(int position) {

        int bodyPartNumber = position / 12;
         int listIndex = position - 12 * bodyPartNumber;

         switch (listIndex){
             case 0:
                 headIndex = listIndex;
                 break;
             case 1:
                 bodyIndex = listIndex;
                 break;
             case 3:
                 legIndex = listIndex;
                 break;
             default:
                 break;
         }

         Bundle bundle = new Bundle();
         bundle.putInt("headIndex", headIndex);
         bundle.putInt("bodyIndex", bodyIndex);
         bundle.putInt("legIndex", legIndex);

        final Intent intent = new Intent(this, AndroidMeActivity.class);
         intent.putExtras(bundle);

        Button button = (Button) findViewById(R.id.button_next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(intent);
            }
        });
    }
}
