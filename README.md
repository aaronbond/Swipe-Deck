# Swipe-Deck
## A Tinder style Swipeable deck view for Android

![Screenshot](https://fat.gfycat.com/PartialBitterHermitcrab.gif)

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Swipe--Deck-blue.svg?style=flat-square)](http://android-arsenal.com/details/1/2970)  [![Join the chat at https://gitter.im/aaronbond/Swipe-Deck](https://badges.gitter.im/aaronbond/Swipe-Deck.svg)](https://gitter.im/aaronbond/Swipe-Deck?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## A Message To Developers

This project is still under considerable amount of development, as such i tend to tweak the API occasionally, if things change a little come and read the README or send me an issue. Please send me issues and pull requests if you need something fixed or have a feature you want and be sure to tell me if you find a bug!

## Installation

In your repositories and dependencies section add these parameters:

```groovy
dependencies {
    compile 'com.daprlabs.aaron:cardstack:0.3.0'
}
```
Sync Gradle and import Swipe-Deck into your project

```java
import com.daprlabs.cardstack.SwipeDeck;
```

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
            
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String item = (String)getItem(position);
                    Log.i("MainActivity", item);
                }
            });
            
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
        swipedeck:card_spacing="10dp"
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

        final ArrayList<String> testData = new ArrayList<>();
        testData.add("0");
        testData.add("1");
        testData.add("2");
        testData.add("3");
        testData.add("4");

        final SwipeDeckAdapter adapter = new SwipeDeckAdapter(testData, this);
        cardStack.setAdapter(adapter);

        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                Log.i("MainActivity", "card was swiped left, position in adapter: " + position);
            }

            @Override
            public void cardSwipedRight(int position) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + position);
            }

            @Override
            public void cardsDepleted() {
                Log.i("MainActivity", "no more cards");
            }
        });

```

# Deck XML Attributes

```xml
"max_visible" - (Integer) number of cards rendered in the deck

"rotation_degrees" - (Float) degree of tilt offset as the card moves left / right

"card_spacing" - (Dimension) amount to offset each card on the Y axis, 0dp will put cards directly atop each other (dp, px etc)

"render_above" - (Boolean) render the cards above other views in the layout

"render_below" - (Boolean) render the cards below other views in the layout

"opacity_end" - (Float) if using the left and right swipe image feature, range from 0 - 1,
 this is the point where your swipe images reach full opacity, for example 0.33 would mean
 full opacity when the card moves as far as 1/3 of the screen space left or right
		
```

# Features

###Easily design cards and deck container in XML and have cards render over the top (or underneath) elements in your layout

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
        android:padding="40dp"
        swipedeck:card_spacing="10dp"
        swipedeck:max_visible="3"
        swipedeck:render_above="true"
        swipedeck:rotation_degrees="15"
        swipedeck:opacity_end="0.33"/>

    <Button
        android:id="@+id/button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom"
        android:text="Button" />

</com.daprlabs.cardstack.SwipeFrameLayout>

```

![Screenshot](http://i.imgur.com/bijdPhg.png?1)

###Indicator images for swiping left and right, simply add a left and right swipe view to your card layout and register their resource
id with swipe deck:
```xml
            <ImageView
                android:id="@+id/left_image"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:src="@drawable/left_arrow"
                android:layout_alignTop="@+id/imageView"
                android:layout_toLeftOf="@+id/imageView"
                android:layout_toStartOf="@+id/imageView" />
            <ImageView
                android:id="@+id/right_image"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:src="@drawable/right_arrow"
                android:layout_below="@+id/offer_image"
                android:layout_toRightOf="@+id/imageView"
                android:layout_toEndOf="@+id/imageView" />
```

```java
        final SwipeDeckAdapter adapter = new SwipeDeckAdapter(testData, this);
        cardStack.setAdapter(adapter);
        
        cardStack.setLeftImage(R.id.left_image);
        cardStack.setRightImage(R.id.right_image);
```

![Screenshot](http://i.imgur.com/j1Npxpn.png?1)

###Programatically Swipe the top card left / right:

```java
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardStack.swipeTopCardLeft();

            }
        });
        Button btn2 = (Button) findViewById(R.id.button2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardStack.swipeTopCardRight();
            }
        });
```
![Screenshot](http://i.imgur.com/J6lwtGg.png?1)

## Hardware Acceleration
In a future release this will be enabled by default but for now:

```Java
 	cardStack = (SwipeDeck) findViewById(R.id.swipe_deck);
        cardStack.setHardwareAccelerationEnabled(true);
```

currently this just enables rendering the cards to an offscreen buffer. It works well on every device i've tested
but if you run into issues please let me know.

# TODO
Lots of optimisation work
Plenty of features left to add (let me know if you think of any)

# Addendum

Feel free to contact me or log an issue if there's something broken or missing. I'd be happy to fix it up. This is currently very early work and you should fully expect some issues I have yet to spot. 
currently requires minSdkVersion 14, i haven't tested it but should easily support all the way back to sdk 12. If for some reason people want that let me know and i'll release a version.
