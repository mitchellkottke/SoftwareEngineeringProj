# Tech Prep

## Tech Prep App Demo
[![Alt text](https://img.youtube.com/vi/Oq0JdTPiH1w/0.jpg)](https://youtu.be/Oq0JdTPiH1w)

## Implementing UMD Fonts & Colors
Under the Attributes look for fontFamily, then hit the drop down button and all the imported UMD fonts will appear. __textbook.otf__ will be the default normal text. 

The UMD Maroon and Gold colors are implmented into the app. The id for the marroon and gold colors are:
```
@color/colorMaroon 
@color/colorGold
```
These can easily applied by adding it into the background section under the attributes of the button and or other item added.

## Changing A Blank Activity to Navigation Drawer Activity
1. Go into the activity layout xml file under the Res -> Layout folders.
2. Then in the second line of the xml file change the constrait layout to a drawer layout: 
	`android.support.v4.widget.DrawerLayout`
3. To add in the navigation drawer, then copy and paste this code right above the end tag for the DrawerLayout
```xml
    <android.support.design.widget.NavigationView
        android:id="@+id/navigation_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_gravity="start"
        android:background="@color/colorMaroon"
        app:menu="@menu/nav_menu"
        app:itemTextColor="@color/colorWhite"
        app:theme="@style/NavigationDrawerStyle">
    </android.support.design.widget.NavigationView>
```
4. Now move to the new activity's java file and add two private variables:
```java
private DrawerLayout mDrawerLayout;
private ActionBarDrawerToggle mToggle;
```
5. In the onCreate function add in this code:
```java
mDrawerLayout= (DrawerLayout) findViewById(R.id.nav_drawer);
mToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView=(NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
```
6. Have the public class also implement NavigationView.OnNavigationItemSelectedLister. Add this code to the defintion of the public class:
	`implements NavigationView.OnNavigationItemSelectedListener`
7. Then after the OnClick function add these additional functions:
```java
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
    }
```
8. Please check out the MainActivity's code to see where things are placed.

## Add Option/Link to the Menu
1. Go to nav_menu.xml in the Res -> Menu folder.
2. Add a new item with this code: 
```xml
<item android:id="@+id/id_name
	android:title="Page Name">
</item>
```
3. Add this code in the __onNaviagationItemSelected__ funtion in __ALL__ the activity pages. Make sure to use the new id that was declared in the nav_menu file. 
```java
if (id == R.id.id_name){
    Intent intent = new Intent(this, NEWACTIVITY.class);
    startActivity(intent);
}
```

__NOTE:__ That id's may not appear automatically. If this is occuring build and clean the project.

## Generating google-services.JSON Files
Everyone will have to generate their own google-services.JSON file in order to get the Google login working on their devicies (including emulators).

1. Go to https://console.developers.google.com
2. Go to Select a project > hit the + button to create a new project > name project and create
3. Go to https://developers.google.com/identity/sign-in/android/start-integrating
4. Scroll down to button that says "Configure A Project" and click it
5. Select the project you just created
6. Name product
7. On 'Congifure your OAuth client' page select 'Android' from the drop down
8. Package name: com.example.liz.interviewapp
9. For SHA-1 signing certificate: Open Android Studio > Select 'Gradle' from top right side bar > Select 'app(root)' > Select 'Android' > Select 'signingReport' > Copy and paste SHA1 key > Create > Done
![ ](https://i.stack.imgur.com/3QcBI.png)
Image from: https://stackoverflow.com/questions/15727912/sha-1-fingerprint-of-keystore-certificate/15727931
10. Go to https://console.firebase.google.com
11. Add project
12. Select Tech Prep from drop down > Add firebase
13. In Firebase go to Settings > Project Settings > Add Firebase to you Adnroid App > Fill in fields > Register App > Download google-services.json 
14. Open Android > Project view > and delete existing google-services.json file from the app if it exists
15. Right click app[app-app] > New > File > name it google-services.json > copy and paste contents from downloaded file
