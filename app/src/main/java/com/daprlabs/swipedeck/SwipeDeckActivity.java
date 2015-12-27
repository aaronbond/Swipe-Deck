package com.daprlabs.swipedeck;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.daprlabs.cardstack.SwipeDeck;

import java.util.ArrayList;
import java.util.List;

public class SwipeDeckActivity extends AppCompatActivity {

    private SwipeDeck cardStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_deck);
        cardStack = (SwipeDeck) findViewById(R.id.swipe_deck);

        ArrayList<String> testData = new ArrayList<>();
        testData.add("1");
        testData.add("2");
        testData.add("3");
        testData.add("4");
        testData.add("5");
        testData.add("1");
        testData.add("2");
        testData.add("3");
        testData.add("4");
        testData.add("5");
        testData.add("1");
        testData.add("2");
        testData.add("3");
        testData.add("4");
        testData.add("5");
        testData.add("1");
        testData.add("2");
        testData.add("3");
        testData.add("4");
        testData.add("5");
        testData.add("1");
        testData.add("2");
        testData.add("3");
        testData.add("4");
        testData.add("5");

        SwipeDeckAdapter adapter = new SwipeDeckAdapter(testData, this);
        cardStack.setAdapter(adapter);

        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft() {
                Log.i("MainActivity", "card was swiped left");
            }

            @Override
            public void cardSwipedRight() {
                Log.i("MainActivity", "card was swiped right");
            }

            @Override
            public void cardClicked() {
                Log.i("MainActivity", "card was clicked");
            }

            @Override
            public void cardsDepleted() {
                Log.i("MainActivity", "no more cards");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_swipe_deck, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class SwipeDeckAdapter extends BaseAdapter {

        private List<String> data;
        private Context context;

        public SwipeDeckAdapter(List<String> data, Context context) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if(v == null){
                LayoutInflater inflater = getLayoutInflater();
                // normally use a viewholder
                v = inflater.inflate(R.layout.test_card2, parent, false);
            }
            //((TextView) v.findViewById(R.id.textView2)).setText(data.get(position));

            return v;
        }
    }
}
