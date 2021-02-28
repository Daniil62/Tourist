package ru.job4j.tourist.activitys;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import ru.job4j.tourist.R;
import ru.job4j.tourist.data_base.CoordinatesDbHelper;

public class ObjectManagerActivity extends AppCompatActivity {
    private TextView header;
    CoordinatesDbHelper helper;
    private double latitude;
    private double longitude;
    private boolean isItTrack;
    private int id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_manager);
        Intent intent = getIntent();
        this.helper = new CoordinatesDbHelper(this);
        this.latitude = intent.getDoubleExtra("latitude", 0.0);
        this.longitude = intent.getDoubleExtra("longitude", 0.0);
        this.isItTrack = intent.getBooleanExtra("track", false);
        this.id = isItTrack ? intent.getIntExtra("track_id", -1) :
                intent.getIntExtra("place_id", -1);

        String title = intent.getStringExtra("title");
        this.header = findViewById(R.id.object_manager_title_textView);
        Button toMap = findViewById(R.id.object_manager_to_map_button);
        Button rename = findViewById(R.id.object_manager_rename_button);
        Button delete = findViewById(R.id.object_manager_delete_button);
        header.setText(title);
        toMap.setOnClickListener(this::onClick);
        rename.setOnClickListener(this::onClick);
        delete.setOnClickListener(this::onClick);
    }
    private void onClick(View view) {
        switch (view.getId()) {
            case (R.id.object_manager_to_map_button) : {
                Intent intent = new Intent(this, PreviewActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("track", isItTrack);
                intent.putExtra("id", id);
                startActivity(intent);
                break;
            }
            case (R.id.object_manager_rename_button) : {
                openRenameDialog();
                break;
            }
            case (R.id.object_manager_delete_button) : {
                openDeleteDialog();
                break;
            }
        }
    }
    private void openRenameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this)
                .inflate(R.layout.save_dialog_fragment, null);
        builder.setView(view);
        builder.setPositiveButton("Ok", (dialog, which) -> {
            EditText textField = ((AlertDialog) dialog).findViewById(R.id.save_dialog_editText);
            if (isItTrack) {
                helper.renameTrack(id + 1, textField.getText().toString());
            } else {
                helper.renamePlace(id + 1, textField.getText().toString());
            }
            header.setText(textField.getText().toString());
        });
        builder.show();
    }
    private void openDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = isItTrack ? getResources().getString(R.string.delete_track) :
                getResources().getString(R.string.delete_place);
        builder.setTitle(title).setPositiveButton("Ok", (dialog, which) -> {
            if (isItTrack) {
                helper.deleteTrack(id + 1);
            } else {
                helper.deletePlace(id + 1);
            }
            onBackPressed();
        });
        builder.show();
    }
}
