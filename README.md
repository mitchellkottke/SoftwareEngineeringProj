# teamNULL

## Implementing UMD Fonts
Under the Attributes look for fontFamily, then hit the drop down button and all the imported UMD fonts will appear. __textbook.otf__ will be the default normal text. 

##Changing A Blank Activity to Navigation Drawer Activity
1. Go into the activity layout xml file under the Res -> Layout folders.
2. Then in the second line of the xml file change the constrait layout to a drawer layout: 
	`android.support.v4.widget.DrawerLayout`
3. To add in the navigation drawer, then copy and paste this code right above the end tag for the DrawerLayout
	```<android.support.design.widget.NavigationView
        android:id="@+id/navigation_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_gravity="start"
        android:background="@color/colorMaroon"
        app:menu="@menu/nav_menu"
        app:itemTextColor="@color/colorWhite"
        app:theme="@style/NavigationDrawerStyle">
    </android.support.design.widget.NavigationView>```
4. Now move to the new activity's java file and add two private variables:
	```private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;```
5. In the onCreate function add in this code:
	```mDrawerLayout= (DrawerLayout) findViewById(R.id.nav_drawer);
        mToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView=(NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);```
6. Have the public class also implement NavigationView.OnNavigationItemSelectedLister. Add this code to the defintion of the public class:
	`implements NavigationView.OnNavigationItemSelectedListener`
7. Then after the OnClick function add these additional functions:
	```
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_home){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_recordVideo){
            Intent intent = new Intent(this, RecordVideoActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_flashcards){
            Intent intent = new Intent(this, FlashcardsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_resources){
            Intent intent = new Intent(this, ResourcesActivity.class);
            startActivity(intent);
        }
        return false;
    }```
8. Please check out the MainActivity's code to see where things are placed.
