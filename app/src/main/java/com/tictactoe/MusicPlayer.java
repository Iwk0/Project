package com.tictactoe;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.tictactoe.model.Audio;
import com.tictactoe.model.Image;
import com.tictactoe.utils.ImageResize;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by imishev on 25.9.2014 г..
 */
public class MusicPlayer {

    private final static String ID = "id";
    private final static String PATH = "path";
    private final static String AUDIO = "audio";
    private final static String AUDIO_IMAGE_ID = "imageId";
    private final static String IMAGE = "image";
    private final static String STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    private MediaPlayer mp;
    private Handler handler;
    private Runnable runnable;
    private SeekBar seekBar;
    private Audio audio;
    private Image image;
    private ImageView imageView;
    private List<Bitmap> images;
    private String xmlPath;

    private int audioId, currentPosition, oldIndex = -1, newIndex;

    public MusicPlayer(Activity activity, String xmlPath, int audioId, int seekBarId, final int imageViewId) {
        this.xmlPath = xmlPath;
        this.audioId = audioId;
        this.mp = new MediaPlayer();

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
            }
        });

        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactoryObject.newPullParser();

            audio = xmlParserAudio(parser);
            image = xmlParserImage(parser, audio.getImageId());

            mp.setDataSource(STORAGE_PATH + audio.getAudioPath());
            mp.prepare();
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        } catch (XmlPullParserException e) {
            Log.e("XmlPullParserException", e.getMessage());
        }

        images = new ArrayList<Bitmap>();
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... paths) {
                for (String path : image.getPaths()) {
                    images.add(ImageResize.decodeSampledBitmapFromUri(STORAGE_PATH + path, 250, 250));
                }

                return null;
            }
        }.execute();

        seekBar = (SeekBar) activity.findViewById(seekBarId);
        imageView = (ImageView) activity.findViewById(imageViewId);

        handler = new Handler();
        runnable = new Runnable() {

            private final int IMAGE_SIZE = image.getPaths().size();
            private final double INTERVAL = mp.getDuration() * 1.0 / IMAGE_SIZE;

            @Override
            public void run() {
                currentPosition = mp.getCurrentPosition();
                seekBar.setProgress(currentPosition);
                newIndex = (int) (currentPosition / INTERVAL);

                if (newIndex != oldIndex && newIndex < IMAGE_SIZE) {
                    imageView.setImageBitmap(images.get(newIndex));
                    oldIndex = newIndex;
                }

                handler.postDelayed(this, 1000);
            }
        };
    }

    public void start() {
        mp.start();
        mp.seekTo(currentPosition);
        handler.post(runnable);
    }

    public void pause() {
        mp.pause();
        handler.removeCallbacks(runnable);
    }

    public void stop() {
        handler.removeCallbacks(runnable);

        mp.stop();
        mp.reset();

        try {
            mp.setDataSource(STORAGE_PATH + audio.getAudioPath());
            mp.prepare();
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        }

        oldIndex = - 1;
        currentPosition = 0;
        seekBar.setProgress(0);
    }

    public void seekTo(int i) {
        if (mp.isPlaying()) {
            mp.seekTo(i);
        } else {
            currentPosition = i;
        }
    }
    //Comment
    public int getDuration() {
        return mp.getDuration();
    }

    public boolean isPlaying() {
        return mp.isPlaying();
    }

    private Audio xmlParserAudio(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.setInput(new FileInputStream(STORAGE_PATH + xmlPath + "audios.xml"), null);

        Audio audio = null;
        String curText = null;
        int event = parser.getEventType();

        while (event != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();

            switch (event) {
                case XmlPullParser.START_TAG:
                    if (tagName.equalsIgnoreCase(AUDIO)) {
                        if (audio != null && audio.getId() == audioId) {
                            return audio;
                        }
                        audio = new Audio();
                    }
                    break;
                case XmlPullParser.TEXT:
                    curText = parser.getText();
                    break;
                case XmlPullParser.END_TAG:
                    if (tagName.equalsIgnoreCase(ID)) {
                        audio.setId(Integer.parseInt(curText));
                    } else if (tagName.equalsIgnoreCase(PATH)) {
                        audio.setAudioPath(curText);
                    } else if (tagName.equalsIgnoreCase(AUDIO_IMAGE_ID)) {
                        audio.setImageId(Integer.valueOf(curText));
                    }
                    break;
            }

            event = parser.next();
        }

        return null;
    }

    private Image xmlParserImage(XmlPullParser parser, int imagesId) throws XmlPullParserException, IOException {
        parser.setInput(new FileInputStream(STORAGE_PATH + xmlPath + "images.xml"), null);

        Image image = null;
        String curText = null;
        List<String> paths = new ArrayList<String>();
        int event = parser.getEventType();

        while (event != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();

            switch (event) {
                case XmlPullParser.START_TAG:
                    if (tagName.equalsIgnoreCase(IMAGE)) {
                        if (image != null && image.getId() == imagesId) {
                            image.setPaths(paths);
                            return image;
                        }
                        paths.clear();
                        image = new Image();
                    }
                    break;
                case XmlPullParser.TEXT:
                    curText = parser.getText();
                    break;
                case XmlPullParser.END_TAG:
                    if (tagName.equalsIgnoreCase(ID)) {
                        image.setId(Integer.parseInt(curText));
                    } else if (tagName.equalsIgnoreCase(PATH)) {
                        paths.add(curText);
                    }
                    break;
            }

            event = parser.next();
        }

        return null;
    }
}