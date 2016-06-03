package oporytskyi.busnotifier.activity;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import oporytskyi.busnotifier.R;
import oporytskyi.busnotifier.TheApplication;
import oporytskyi.busnotifier.dto.Direction;
import oporytskyi.busnotifier.manager.DirectionManager;
import oporytskyi.busnotifier.manager.ScheduleManager;
import oporytskyi.busnotifier.receiver.AlarmReceiver;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getName();

    private TextView beforehandView;
    private LinearLayout departuresArea;

    private DirectionManager directionManager;
    private ScheduleManager scheduleManager;

    private Direction direction;
    private PeriodFormatter periodFormatter;

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
                if (direction == null) {
                    Snackbar.make(view, "Choose direction!", Snackbar.LENGTH_LONG).show();
                    return;
                }
                LocalTime closest = scheduleManager.getClosest(direction);
                schedule(closest, direction);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        beforehandView = (TextView) findViewById(R.id.beforehand);
        departuresArea = (LinearLayout) findViewById(R.id.ll_departures);

        TheApplication theApplication = TheApplication.get();
        scheduleManager = theApplication.getScheduleManager();
        directionManager = theApplication.getDirectionManager();

        Menu menu = navigationView.getMenu();
        MenuItem item = menu.getItem(0);
        List<Direction> directions = directionManager.getDirections();
        for (int i = 0; i < directions.size(); i++) {
            final Direction direction = directions.get(i);
            MenuItem menuItem = item.getSubMenu().add(Menu.NONE, i, i, direction.getName());
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Log.d(TAG, "onNavigationItemSelected " + item);
                    MainActivity.this.direction = direction;
                    toolbar.setTitle(direction.getName());
                    beforehandView.setText("Beforehand: " + direction.getBeforehand().toString(periodFormatter));
                    generateTimes(direction);
                    return false;   //  close drawer in onNavigationItemSelected
                }
            });
        }

        periodFormatter = new PeriodFormatterBuilder().appendHours().appendSuffix("h").appendMinutes().appendSuffix("m").toFormatter();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void generateTimes(final Direction direction) {
        departuresArea.removeAllViews();
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.shortTime();
        for (final LocalTime localTime : direction.getDepartures()) {
            Button button = new Button(this);
            DateTime dateTime = localTime.toDateTimeToday(directionManager.getDateTimeZone());
            button.setText(dateTime.toString(dateTimeFormatter));
            if (scheduleManager.isEligable(localTime, direction.getBeforehand())) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        schedule(localTime, direction);
                    }
                });
            } else {
                button.setEnabled(false);
            }
            departuresArea.addView(button);
        }
    }

    private void schedule(LocalTime localTime, Direction direction) {
        DateTime dateTime = localTime.toDateTimeToday(directionManager.getDateTimeZone()).toDateTime(DateTimeZone.forTimeZone(TimeZone.getDefault()));
        setAlarm(dateTime.minus(direction.getBeforehand()));
        showNotification(dateTime, direction.getName());
        Snackbar.make(departuresArea, "Scheduled!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void setAlarm(DateTime dateTime) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent activity = PendingIntent.getActivity(this, 0, intent, 0);

        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(dateTime.getMillis(), activity);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, new Intent(AlarmReceiver.ALARM_ACTION), 0);
        alarmManager.setAlarmClock(alarmClockInfo, broadcast);
    }

    private void showNotification(DateTime dateTime, String directionName) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.shortTime();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_dialog_time)
                .setContentTitle(directionName)
                .setContentText("Departure at " + dateTime.toString(dateTimeFormatter));

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setOngoing(true);

        int mNotificationId = 1;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(mNotificationId, mBuilder.build());
    }

    public void onTimeClick(View view) {
        if (direction == null) {
            Snackbar.make(view, "Choose direction!", Snackbar.LENGTH_LONG).show();
            return;
        }
        Period beforehand = direction.getBeforehand();
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Period newBeforehand = new Period(hourOfDay, minute, 0, 0);
                direction.setBeforehand(newBeforehand);
                beforehandView.setText("Beforehand: " + newBeforehand.toString(periodFormatter));
                generateTimes(direction);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        directionManager.save();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    }
                }.execute((Void) null);
            }
        }, beforehand.getHours(), beforehand.getMinutes(), true).show();
    }
}
