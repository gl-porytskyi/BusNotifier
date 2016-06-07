package oporytskyi.busnotifier.fragment;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import oporytskyi.busnotifier.R;
import oporytskyi.busnotifier.TheApplication;
import oporytskyi.busnotifier.manager.ScheduleManager;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduledFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ScheduledFragment extends Fragment {
    private final static String TAG = ScheduledFragment.class.getName();

    private OnFragmentInteractionListener mListener;
    private Handler handler = new Handler();
    private TextView scheduled;
    private PeriodFormatter periodFormatter = new PeriodFormatterBuilder().appendHours().appendSuffix("h").appendMinutes().appendSuffix("m").appendSeconds().appendSuffix("s").toFormatter();
    private Runnable updateView = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "updateView");
            handler.postDelayed(this, 1000);
            ScheduleManager scheduleManager = TheApplication.get().getScheduleManager();
            DateTime departure = scheduleManager.getDeparture();
            if (departure != null) {
                DateTime alarm = scheduleManager.getAlarm();
                DateTime now = DateTime.now(DateTimeZone.forTimeZone(TimeZone.getDefault()));
                String string = getResources().getString(R.string.scheduled_time,
                        new Period(now, alarm).toString(periodFormatter),
                        new Period(now, departure).toString(periodFormatter));
                scheduled.setText(string);
            } else {
                scheduled.setText(R.string.scheduled_nothing);
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scheduled, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        scheduled = (TextView) getView().findViewById(R.id.tv_scheduled);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        handler.post(updateView);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        handler.removeCallbacks(updateView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        scheduled = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
