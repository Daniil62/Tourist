package ru.job4j.tourist.activitys;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import java.util.concurrent.TimeUnit;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ru.job4j.tourist.R;
import ru.job4j.tourist.masters.MapMaster;

public class InstrumentationActivity extends AppCompatActivity {
    private Disposable disposable;
    private MapMaster master;
    private TextView distanceTo;
    private TextView coveredDistance;
    private TextView speed;
    private TextView latitude;
    private TextView longitude;
    private TextView altitude;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrumentation);
        master = new MapMaster();
        distanceTo = findViewById(R.id.instrumentation_distance_textView);
        coveredDistance = findViewById(R.id.instrumentation_traveled_distance_textView);
        speed = findViewById(R.id.instrumentation_speed_textView);
        latitude = findViewById(R.id.instrumentation_latitude_textView);
        longitude = findViewById(R.id.instrumentation_longitude_textView);
        altitude = findViewById(R.id.instrumentation_altitude_textView);
        init();
    }
    @Override
    protected void onStop() {
        disposable.dispose();
        super.onStop();
    }
    @SuppressLint("SetTextI18n")
    private void init() {
        if (disposable == null || disposable.isDisposed()) {
            disposable = Observable.interval(1, TimeUnit.SECONDS).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(v -> {
                        String text = master.getDistanceToPurpose();
                        distanceTo.setText(text.equals("--") ?
                                text : text + " " + getResources().getString(R.string.km));
                        coveredDistance.setText(master.getCoveredDistance()
                                + " " + getResources().getString(R.string.km));
                        speed.setText(master.getSpeed() + " "
                                + getResources().getString(R.string.speed_km_h));
                        latitude.setText(master.getCurrentLatitudeString());
                        longitude.setText(master.getCurrentLongitudeString());
                        altitude.setText(master.getCurrentAltitudeString());
            });
        }
    }
}
