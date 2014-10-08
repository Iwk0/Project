package com.tictactoe;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private MusicPlayer musicPlayer;
    private SeekBar seekBar;
    private Button playButton;
    private ArrayList<String> absolutePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        musicPlayer = new MusicPlayer(this, "/program/", 1, R.id.seekBar, R.id.image);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(musicPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    musicPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        findViewById(R.id.restart).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                absolutePath = new ArrayList<String>();
                getAbsolutePathOfAllSongs(Environment.getExternalStorageDirectory());

                for (String path : absolutePath) {
                    Log.i("SONG PATH", path);
                }
            }
        });

        playButton = (Button) findViewById(R.id.play);
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (musicPlayer.isPlaying()) {
                    musicPlayer.pause();
                    ((Button) view).setText("Play");
                } else {
                    musicPlayer.start();
                    ((Button) view).setText("Pause");
                }
            }
        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                musicPlayer.stop();
                playButton.setText("Play");
            }
        });

        if (isNetworkAvailable()) {
            new JSONReader("Bulgaria", "Sofia", "BG").execute();
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void getAbsolutePathOfAllSongs(File dir) {
        String extension = ".mp3";

        File[] listFile = dir.listFiles();

        if (listFile != null) {
            for (File file : listFile) {
                if (file.isDirectory()) {
                    getAbsolutePathOfAllSongs(file);
                } else {
                    if (file.getName().endsWith(extension)) {
                        absolutePath.add(file.getAbsolutePath());
                    }
                }
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}