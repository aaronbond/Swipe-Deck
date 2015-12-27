# Swipe-Deck
## A Tinder style Swipeable deck view for Android

![alt tag](https://gfycat.com/AgreeableEvenHeterodontosaurus)

## Installation

First add this maven repository to your app's gradle file:

```groovy
repositories{
maven {
    url = "https://dl.bintray.com/aaronbond/maven"
}
}
```
Then in your dependencies section add this parameter:

```groovy
dependencies {
    compile 'com.daprlabs.aaron:cardstack:0.0.3'
}

```

I will put the package up on Jcenter eventually


## Example 

Start by defining a card view, this can be made in the normal way in XML:

```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.v7.widget.CardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:card_view="http://schemas.android.com/apk/res-auto"
    android:layout_height="wrap_content"
    android:layout_width="match_parent"
    card_view:cardCornerRadius="6dp"
    card_view:cardElevation="10dp"
    >
    <LinearLayout
        android:orientation="vertical"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:minHeight="200dp">
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Test Text"
            android:id="@+id/textView"
            android:layout_gravity="center_horizontal" />
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textAppearance="?android:attr/textAppearanceLarge"
            android:text="Large Text"
            android:id="@+id/textView2"
            android:layout_gravity="center_horizontal" />
    </LinearLayout>
</android.support.v7.widget.CardView>
```
You can use any type of view you like (not just a Card View) but i would recommend adding a drop shadow or border of some kind.

Next Swipe Deck takes an adapter just like you're used to with other adapter views. Here's a quick sample adapter:

```java
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
                v = inflater.inflate(R.layout.test_card, parent, false);
            }
            ((TextView) v.findViewById(R.id.textView2)).setText(data.get(position));
            return v;
        }
    }

```

Now we add a swipe deck to our layout:

```xml
<?xml version="1.0" encoding="utf-8"?>
<com.daprlabs.cardstack.SwipeFrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:swipedeck="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <com.daprlabs.cardstack.SwipeDeck
        android:id="@+id/swipe_deck"
        android:layout_width="match_parent"
        android:layout_height="480dp"
        android:padding="20dp"
        swipedeck:card_spacing="20"
        swipedeck:max_visible="3"
        swipedeck:render_above="true"
        swipedeck:rotation_degrees="15" />

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom"
        android:text="Button" />

</com.daprlabs.cardstack.SwipeFrameLayout>

```
I've included some modified layouts (SwipeFrameLayout, SwipeRelativeLayout etc) for ease of use, but you can use any layout you desire. However you may not get the desired outcome unless you set android:clipChildren="false" on your containing layout. If you choose not to do this cards will be clipped as they move outside their view boundary.

Now we simply give our card deck an adapter and perhaps a callback from our Activity:

```java
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
```

# Deck Attributes

```
"max_visible" - Integer, number of cards rendered in the deck
"rotation_degrees" - Float, degree of tilt offset as the card moves left / right
"card_spacing" - Float, amount to offset each card on the Y axis
"render_above" - Boolean, render the cards above other views in the layout
"render_below" - Boolean, render the cards below other views in the layout
		
```

# TODO
Lots of optimisation work
Plenty of features left to add

# Addendum

Feel free to contact me or log an issue if there's something broken or missing. I'd be happy to fix it up. This is currently very early work and you should fully expect some issues I have yet to spot. 
