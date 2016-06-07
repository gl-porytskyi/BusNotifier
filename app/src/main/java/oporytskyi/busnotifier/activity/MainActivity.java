package oporytskyi.busnotifier.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;
import oporytskyi.busnotifier.R;
import oporytskyi.busnotifier.TheApplication;
import oporytskyi.busnotifier.dto.Direction;
import oporytskyi.busnotifier.fragment.DirectionFragment;
import oporytskyi.busnotifier.fragment.ScheduledFragment;
import oporytskyi.busnotifier.manager.DirectionManager;
import oporytskyi.busnotifier.manager.ScheduleManager;
import org.joda.time.LocalTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getName();

    private DirectionManager directionManager;
    private ScheduleManager scheduleManager;

    private ScheduledFragment scheduledFragment = new ScheduledFragment();
    private Map<String, DirectionFragment> directionFragmentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Direction direction = directionManager.getCurrent();
                if (direction == null) {
                    Snackbar.make(view, "Choose direction!", Snackbar.LENGTH_LONG).show();
                    return;
                }
                LocalTime closest = scheduleManager.getClosest(direction);
                scheduleManager.schedule(closest, direction);
                Snackbar.make(view, "Scheduled!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TheApplication theApplication = TheApplication.get();
        scheduleManager = theApplication.getScheduleManager();
        directionManager = theApplication.getDirectionManager();

        Menu menu = navigationView.getMenu();
        SubMenu subMenu = menu.getItem(menu.size() - 1).getSubMenu();
        List<Direction> directions = directionManager.getDirections();
        for (int i = 0; i < directions.size(); i++) {
            final Direction direction = directions.get(i);
            MenuItem menuItem = subMenu.add(Menu.NONE, i, i, direction.getName());
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    directionManager.setCurrent(direction);
                    toolbar.setTitle(direction.getName());
                    DirectionFragment directionFragment = directionFragmentMap.get(direction.getName());
                    if (directionFragment == null) {
                        directionFragment = new DirectionFragment();
                        directionFragmentMap.put(direction.getName(), directionFragment);
                    }
                    showFragment(directionFragment);
                    return false;   //  close drawer in onNavigationItemSelected
                }
            });
        }
        showFragment(scheduledFragment);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            Toast.makeText(this, "Nothing here", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected " + item);
        switch (item.getItemId()) {
            case R.id.menu_scheduled:
                showFragment(scheduledFragment);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
//        transaction.addToBackStack(null);
        transaction.commit();
    }
}
