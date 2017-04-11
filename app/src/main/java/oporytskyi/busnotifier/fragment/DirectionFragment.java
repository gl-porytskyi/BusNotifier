package oporytskyi.busnotifier.fragment;


import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import oporytskyi.busnotifier.R;
import oporytskyi.busnotifier.TheApplication;
import oporytskyi.busnotifier.activity.MainActivity;
import oporytskyi.busnotifier.dto.Direction;
import oporytskyi.busnotifier.manager.DirectionManager;
import oporytskyi.busnotifier.manager.ScheduleManager;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class DirectionFragment extends Fragment {
    private static final String TAG = DirectionFragment.class.getName();
    private final PeriodFormatter periodFormatter = new PeriodFormatterBuilder().appendHours().appendSuffix("h").appendMinutes().appendSuffix("m").toFormatter();

    @Inject
    DirectionManager directionManager;
    @Inject
    ScheduleManager scheduleManager;

    private TextView beforehandView;
    private LinearLayout departuresArea;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TheApplication) getActivity().getApplication()).getTheComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_direction, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        beforehandView = (TextView) getView().findViewById(R.id.beforehand);
        departuresArea = (LinearLayout) getView().findViewById(R.id.ll_departures);

        Direction direction = directionManager.getCurrent();
        beforehandView.setText(getString(R.string.beforehand, direction.getBeforehand().toString(periodFormatter)));
        beforehandView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTime(v);
            }
        });
        generateTimes();
    }

    private void generateTimes() {
        departuresArea.removeAllViews();
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.shortTime();
        final Direction direction = directionManager.getCurrent();
        for (final LocalTime localTime : direction.getDepartures()) {
            Button button = new Button(getContext());
            DateTime dateTime = localTime.toDateTimeToday(directionManager.getDateTimeZone());
            button.setText(dateTime.toString(dateTimeFormatter));
            if (scheduleManager.isEligable(localTime, direction.getBeforehand())) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity) getActivity()).schedule(v, direction, localTime);
                    }
                });
            } else {
                button.setEnabled(false);
            }
            departuresArea.addView(button);
        }
    }

    public void onClickTime(View view) {
        final Direction direction = directionManager.getCurrent();
        if (direction == null) {
            Snackbar.make(view, "Choose direction!", Snackbar.LENGTH_LONG).show();
            return;
        }
        Period beforehand = direction.getBeforehand();
        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Period newBeforehand = new Period(hourOfDay, minute, 0, 0);
                direction.setBeforehand(newBeforehand);
                beforehandView.setText(getString(R.string.beforehand, newBeforehand.toString(periodFormatter)));
                generateTimes();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        directionManager.save();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        Toast.makeText(DirectionFragment.this.getContext(), "Saved", Toast.LENGTH_SHORT).show();
                    }
                }.execute((Void) null);
            }
        }, beforehand.getHours(), beforehand.getMinutes(), true).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        beforehandView = null;
        departuresArea = null;
    }
}
