package ru.job4j.tourist.masters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import ru.job4j.tourist.activitys.MenuActivity;
import ru.job4j.tourist.R;
import ru.job4j.tourist.data_base.CoordinatesDbHelper;

public class ControlPanelMaster extends LinearLayout implements View.OnClickListener {
    private final MapMaster master = new MapMaster();
    private final CoordinatesDbHelper helper = new CoordinatesDbHelper(getContext());
    private boolean variant;
    public ControlPanelMaster(Context context) {
        super(context);
        init(context);
    }
    public ControlPanelMaster(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public ControlPanelMaster(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.current_location_button) : {
                MapMaster.onCurrentLocation();
                break;
            }
            case (R.id.save_location_button) : {
                this.variant = true;
                openSaveDialog();
                break;
            }
            case (R.id.stop_navigation__button) : {
                master.removeLine();
                master.removeMarker();
                master.setHasPurpose(false);
                break;
            }
            case (R.id.menu_button) : {
                Context context = getContext();
                context.startActivity(new Intent(context, MenuActivity.class));
                break;
            }
            case (R.id.save_track_button) : {
                this.variant = false;
                openSaveDialog();
                break;
            }
        }
    }
    private void openSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext())
                .inflate(R.layout.save_dialog_fragment, null);
        builder.setView(view);
        builder.setPositiveButton("Ok", (dialog, which) -> {
            EditText textField = ((AlertDialog) dialog).findViewById(R.id.save_dialog_editText);
            if (variant) {
                helper.loadPlace(master.getSavingPlace(textField.getText().toString()));
            } else {
                helper.saveTrack(master.getSavingTrack(textField.getText().toString()));
            }
        });
        builder.show();
    }
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.control_panel, this, true);
        ImageButton current = findViewById(R.id.current_location_button);
        Button saveLocation = findViewById(R.id.save_location_button);
        Button stopNavigation = findViewById(R.id.stop_navigation__button);
        Button menuButton = findViewById(R.id.menu_button);
        Button saveTrack = findViewById(R.id.save_track_button);
        current.setOnClickListener(this);
        saveLocation.setOnClickListener(this);
        stopNavigation.setOnClickListener(this);
        menuButton.setOnClickListener(this);
        saveTrack.setOnClickListener(this);
    }
}
