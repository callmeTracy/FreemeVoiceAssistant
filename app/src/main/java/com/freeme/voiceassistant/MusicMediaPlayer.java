package com.freeme.voiceassistant;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.freeme.statistic.VoiceStatisticUtil;
import com.freeme.util.Util;
import com.freeme.view.MySeekbar;

import com.freeme.data.SpeechData;

public class MusicMediaPlayer implements View.OnClickListener {
    private static final String TAG = "[Freeme]MusicMediaPlayer";
    private static final Uri sArtworkUri = Uri
            .parse("content://media/external/audio/albumart");

    private static final int MSG_OPEN_PLAYER_DONE = 1;
    private static final int MSG_GET_ALBUM_DONE = 2;
    private static final int MSG_UPDATE_PROGRESS_BAR = 3;
    private static final int MSG_OPEN_PLAYER_LIST_EMPTY = 4;
    private static final String PLAYSTATE_CHANGED = "com.android.music.playstatechanged";
    private static final String META_CHANGED = "com.android.music.metachanged";
    public static final int TITLE_LIST = 1;
    public static final int ARTIST_LIST = 2;

    private static final int DELAY_UPDATE_PROGRESS_BAR = 1000;
    private static final int RAND_POS = 0;

    private volatile static MusicMediaPlayer mInstance; // singleton
    MediaPlayer mMediaPlayer;
    private Context mContext;
    private boolean mBound;
    private ConditionVariable mCondition = new ConditionVariable();
    private OpenPlayerThread mOpenPlayerThread;
    private Handler mHandler;
    private AlbumImageWorker mAlbumWorker;
    // play list, all or artist
    private int mType = TITLE_LIST;
    private String mKey; // title or artist name
    private long mSelectedId;
    // have the same title
    private List<MyMusic> mSongList = new ArrayList<MyMusic>();
    private ASRRequestor.onRecongnitionListener mListener;
    boolean ispause = false;

    private boolean mPlayPanelCreated;
    private ImageView mAlbum;
    private TextView mSongName;
    private TextView mArtist;
    private ImageView mPlayBtn;
    private MySeekbar mProgressBar;
    private long mAudioId;
    public int musicindex = 0;
    AudioManager mAudioManager;
    boolean isplaying = false;
    boolean isdestroy = false;
    MediaOnFinishListener mfl = new MediaOnFinishListener();
    MyMusic currentplaymusic;

    private MusicMediaPlayer(Context context,
                             ASRRequestor.onRecongnitionListener listener) {
        mContext = context;
        mListener = listener;
        initHandler();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(mfl);
        mSongList = getMusicdates();
        IntentFilter filter = new IntentFilter();
        filter.addAction(META_CHANGED);
        filter.addAction(PLAYSTATE_CHANGED);
        mContext.registerReceiver(mMetaChangedReceiver, filter);
        mAudioManager = (AudioManager) mContext
                .getSystemService(mContext.AUDIO_SERVICE);
        int result = mAudioManager.requestAudioFocus(
                new OnAudioFocusChangeListener() {

                    @Override
                    public void onAudioFocusChange(int focusChange) {
                        switch (focusChange) {
                            case AudioManager.AUDIOFOCUS_GAIN:
                                if (!isdestroy) {
                                    if (!isplaying && !ispause) {
                                        mMediaPlayer.start();
                                        ispause = false;
                                        isplaying = true;
                                        if (mPlayPanelCreated) {

                                            mPlayBtn.setImageResource(R.drawable.ic_pause);
                                            mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS_BAR);
                                        }
                                    }
                                    mMediaPlayer.setVolume(1.0f, 1.0f);
                                }
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                if (!isdestroy) {
                                    if (isplaying) {
                                        mMediaPlayer.pause();
                                        isplaying = false;
                                    }
                                    if (mPlayPanelCreated) {
                                        mPlayBtn.setImageResource(R.drawable.ic_play);
                                    }
                                }
                                break;
                        }
                    }
                }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

    }

    public static MusicMediaPlayer getInstance(Context context,
                                               ASRRequestor.onRecongnitionListener listener) {
        VoiceStatisticUtil.generateStatisticInfo(context, VoiceStatisticUtil.OPTION_OPENMUSIC);
        if (mInstance == null) {
            synchronized (MusicPlayer.class) {
                if (mInstance == null) {
                    mInstance = new MusicMediaPlayer(context, listener);
                }
            }
        }

        return mInstance;
    }

    public static void release() {
        if (mInstance != null) {
            mInstance.onRelease();

        }
    }

    private void play(int position, MyMusic music, int m) {
        // TODO Auto-generated method stub
        try {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mPlayBtn.setImageResource(R.drawable.ic_play);
                mMediaPlayer.pause();
                isplaying = false;
                ispause = true;
            } else {
                if (m == 0) {
                    mPlayBtn.setImageResource(R.drawable.ic_pause);
                    mMediaPlayer.start();
                    isplaying = true;
                    ispause = false;
                } else {
                    mPlayBtn.setImageResource(R.drawable.ic_pause);
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(music.getUrl());
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                    isplaying = true;
                    ispause = false;
                    mProgressBar.setProgress(0);
                    mSongName.setText(music.getTitle());
                    mArtist.setText(music.getArtist());
                }
            }
            currentplaymusic = music;
            updateWidget(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static View getWidget(String tag) {
        View w = null;
        if (mInstance != null) {
            w = mInstance.createWidget(tag);
        } else {
            throw new IllegalAccessError("mInstance is null!");
        }

        return w;
    }

    public void play(int type, String key) {
        if (ARTIST_LIST == type && (key == null || key.isEmpty())) {
            throw new IllegalArgumentException("artist name key can't empty!");
        }

        mType = type;
        mKey = key;
        mSelectedId = -1;
        mPlayPanelCreated = false;
        mAudioId = -1;
        if (TITLE_LIST == type && mKey != null) {
            mSongList.clear();
            searchSameTitleSongs(mKey);
            Log.i("heqianqian","music-title-="+key);
            if (mSongList.size() > 1) {
                // should select song at first
                if (mListener != null) {
                    String result = mContext
                            .getString(R.string.response_select_muisc);
                    mListener.onResponseSpeechResult(new SpeechData(
                            SpeechData.RESPONSE_TEXT_MODE, result), false);
                    mListener.onSpeak(result);
                    // show select panel
                    mListener.onResponseSpeechResult(new SpeechData(
                            SpeechData.MUSIC_WIDGET_MODE,
                            SpeechData.MUSIC_SELECT_PANEL_TAG), false);
                }
            } else if (!mSongList.isEmpty()) {
                mSelectedId = mSongList.get(0).getId();
                play();
            } else {
                // not found
                if (currentplaymusic != null) {
                    mSongList.add(currentplaymusic);
                }
                mHandler.sendEmptyMessage(MSG_OPEN_PLAYER_LIST_EMPTY);

            }
        } else if (ARTIST_LIST == type && mKey != null) {
            mSongList.clear();
            searchSameTitleSongs(mKey, 0);
            Log.i("heqianqian","aritist-title-="+mKey);
            if (mSongList.size() > 1) {
                // should select song at first
                if (mListener != null) {
                    String result = mContext
                            .getString(R.string.response_select_muisc);
                    mListener.onResponseSpeechResult(new SpeechData(
                            SpeechData.RESPONSE_TEXT_MODE, result), false);
                    mListener.onSpeak(result);
                    // show select panel
                    mListener.onResponseSpeechResult(new SpeechData(
                            SpeechData.MUSIC_WIDGET_MODE,
                            SpeechData.MUSIC_SELECT_PANEL_TAG), false);
                }
            } else if (!mSongList.isEmpty()) {
                mSelectedId = mSongList.get(0).getId();
                play();
            } else {
                // not found
                mHandler.sendEmptyMessage(MSG_OPEN_PLAYER_LIST_EMPTY);
                if (currentplaymusic != null) {
                    mSongList.add(currentplaymusic);
                }
            }
        } else {
            mSongList = getMusicdates();
            if (mSongList.isEmpty()) {
                mHandler.sendEmptyMessage(MSG_OPEN_PLAYER_LIST_EMPTY);
            } else {
                mSelectedId = mSongList.get(0).getId();
                play();
            }

        }
    }

    public void play(int type, String key, String artist) {
        if (ARTIST_LIST == type && (key == null || key.isEmpty())) {
            throw new IllegalArgumentException("artist name key can't empty!");
        }

        mType = type;
        mKey = key;
        mSelectedId = -1;
        mSongList.clear();
        mPlayPanelCreated = false;
        mAudioId = -1;
        Log.i("heqianqian","music-title-="+key);
        if (TITLE_LIST == type && mKey != null) {
            // search the same song name list
            searchSameTitleSongs(mKey, artist);
            Log.i("heqianqian","mSongList.size()=mSongList.size()"+mSongList.size());
            if (mSongList.size() > 1) {
                // should select song at first
                if (mListener != null) {
                    String result = mContext
                            .getString(R.string.response_select_muisc);
                    mListener.onResponseSpeechResult(new SpeechData(
                            SpeechData.RESPONSE_TEXT_MODE, result), false);
                    mListener.onSpeak(result);
                    // show select panel
                    mListener.onResponseSpeechResult(new SpeechData(
                            SpeechData.MUSIC_WIDGET_MODE,
                            SpeechData.MUSIC_SELECT_PANEL_TAG), false);
                }
            } else if (!mSongList.isEmpty()) {
                mSelectedId = mSongList.get(0).getId();
                play();
            } else {
                // not found
                mHandler.sendEmptyMessage(MSG_OPEN_PLAYER_LIST_EMPTY);
                if (currentplaymusic != null) {
                    mSongList.add(currentplaymusic);
                }
            }
        } else {
            // play the first song or play artist's song
            mSelectedId = mSongList.get(0).getId();
            play();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.play:
                    play(mProgressBar.getProgress(), mSongList.get(musicindex), 0);
                    break;

                case R.id.prev:
                    prev();
                    break;

                case R.id.next:
                    next();
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prev() {
        // TODO Auto-generated method stub
        mSongList = getMusicdates();
        if (mSongList.size() > 0) {
            if (musicindex == 0) {
                if (mSongList.size() >= 1) {
                    musicindex = mSongList.size() - 1;
                }
            } else {
                musicindex = musicindex - 1;
            }
            mMediaPlayer.pause();
            play(0, mSongList.get(musicindex), 1);
        }
    }

    private void next() {
        // TODO Auto-generated method stub
        mSongList = getMusicdates();
        if (mSongList.size() > 0) {
            if (musicindex == mSongList.size() - 1) {
                musicindex = 0;
            } else {
                musicindex = musicindex + 1;
            }
            mMediaPlayer.pause();
            play(0, mSongList.get(musicindex), 1);
            currentplaymusic = mSongList.get(musicindex);
        }
    }

    class MediaOnFinishListener implements OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            // TODO Auto-generated method stub
            next();
        }

    }

    private void play() {
        if (mOpenPlayerThread != null) {
            mOpenPlayerThread.cancel();
            mOpenPlayerThread = null;
        }

        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(searchsongbyId(mSelectedId).getUrl());
                currentplaymusic = searchsongbyId(mSelectedId);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                ispause = false;
                updateWidget(true);
                isplaying = true;
            } else {
                mMediaPlayer.pause();
                isplaying = false;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mHandler.sendEmptyMessage(MSG_OPEN_PLAYER_DONE);
    }

    private void searchSameTitleSongs(String key) {
        Cursor cursor = mContext.getContentResolver()
                .query(Media.EXTERNAL_CONTENT_URI,
                        new String[]{Media._ID, Media.TITLE, Media.ARTIST,
                                Media.DATA}, // projection
                        Media.IS_MUSIC + "!='0'", // selection
                        null, Media.DEFAULT_SORT_ORDER);

        try {
            while (cursor.moveToNext()) {
                // search by title
                String title = cursor.getString(cursor
                        .getColumnIndex(Media.TITLE));

                if (key.equals(title)) {
                    MyMusic song = new MyMusic();
                    song.setId(cursor.getLong(cursor.getColumnIndex(Media._ID)));
                    song.setTitle(title);
                    song.setArtist(cursor.getString(cursor
                            .getColumnIndex(Media.ARTIST)));
                    song.setUrl(cursor.getString(cursor
                            .getColumnIndex(Media.DATA)));
                    mSongList.add(song);
                    Log.i(TAG,
                            "searchSameTitleSongs(): add song id = "
                                    + song.getId());
                }
                Log.i("heqianqian", "mSongList.size()====" + mSongList.size());
            }
        } finally {
            cursor.close();
        }
    }

    private void searchSameTitleSongs(String key, int m) {
        Cursor cursor = mContext.getContentResolver()
                .query(Media.EXTERNAL_CONTENT_URI,
                        new String[]{Media._ID, Media.TITLE, Media.ARTIST,
                                Media.DATA}, // projection
                        Media.IS_MUSIC + "!='0'", // selection
                        null, Media.DEFAULT_SORT_ORDER);

        try {
            while (cursor.moveToNext()) {
                // search by title
                String artist = cursor.getString(cursor
                        .getColumnIndex(Media.ARTIST));

                if (key.equals(artist)) {
                    MyMusic song = new MyMusic();
                    song.setId(cursor.getLong(cursor.getColumnIndex(Media._ID)));
                    song.setArtist(artist);
                    song.setTitle(cursor.getString(cursor
                            .getColumnIndex(Media.TITLE)));
                    song.setUrl(cursor.getString(cursor
                            .getColumnIndex(Media.DATA)));
                    mSongList.add(song);
                    Log.i(TAG,
                            "searchSameTitleSongs(): add song id = "
                                    + song.getId());
                }
            }
        } finally {
            cursor.close();
        }
    }


    public MyMusic searchsongbyId(long songid) {
        Cursor cursor = mContext.getContentResolver()
                .query(Media.EXTERNAL_CONTENT_URI,
                        new String[]{Media._ID, Media.TITLE, Media.ARTIST,
                                Media.DATA}, // projection
                        Media.IS_MUSIC + "!='0'", // selection
                        null, Media.DEFAULT_SORT_ORDER);
        MyMusic song = null;
        try {
            while (cursor.moveToNext()) {
                // search by title
                long id = cursor.getLong(cursor.getColumnIndex(Media._ID));

                if (songid == id) {
                    song = new MyMusic();
                    song.setId(cursor.getLong(cursor.getColumnIndex(Media._ID)));
                    song.setArtist(cursor.getString(cursor
                            .getColumnIndex(Media.ARTIST)));
                    song.setTitle(cursor.getString(cursor
                            .getColumnIndex(Media.TITLE)));
                    song.setUrl(cursor.getString(cursor
                            .getColumnIndex(Media.DATA)));
                    Log.i(TAG,
                            "searchSameTitleSongs(): add song id = "
                                    + song.getId());
                }
            }
        } finally {
            cursor.close();
        }
        return song;
    }

    private void searchSameTitleSongs(String key, String artist) {
        Cursor cursor = mContext.getContentResolver()
                .query(Media.EXTERNAL_CONTENT_URI,
                        new String[]{Media._ID, Media.TITLE, Media.ARTIST,
                                Media.DATA}, // projection
                        Media.IS_MUSIC + "!='0'", // selection
                        null, Media.DEFAULT_SORT_ORDER);

        try {
            while (cursor.moveToNext()) {
                // search by title
                String title = cursor.getString(cursor
                        .getColumnIndex(Media.TITLE));
                String artistname = cursor.getString(cursor
                        .getColumnIndex(Media.ARTIST));
                if (key.equals(title) && artist.equals(artistname)) {
                    MyMusic song = new MyMusic();
                    song.setId(cursor.getLong(cursor.getColumnIndex(Media._ID)));
                    song.setTitle(title);
                    song.setArtist(cursor.getString(cursor
                            .getColumnIndex(Media.ARTIST)));
                    song.setUrl(cursor.getString(cursor
                            .getColumnIndex(Media.DATA)));
                    mSongList.add(song);
                }
            }
        } finally {
            cursor.close();
        }
    }

    private void onRelease() {
        //if (mBound) {
        Log.i(TAG, "onRelease()...");

        if (mOpenPlayerThread != null) {
            mOpenPlayerThread.cancel();
            mOpenPlayerThread = null;
        }

        if (mAlbumWorker != null) {
            mAlbumWorker.cancel(true);
            mAlbumWorker = null;
        }

        mHandler.removeMessages(MSG_UPDATE_PROGRESS_BAR);

        // stop player
        try {
            if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    isplaying = false;
                    ispause = true;
                }
                mMediaPlayer.release();
                isdestroy = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mContext.unregisterReceiver(mMetaChangedReceiver);
        mBound = false;
        mInstance = null;
        //}
    }

    private View createWidget(String tag) {
        View widget = null;

        if (SpeechData.MUSIC_SELECT_PANEL_TAG.equals(tag)) {
            // select panel
            widget = createSelectPanel();
        } else {
            // play panel
            widget = LayoutInflater.from(mContext).inflate(
                    R.layout.music_widget_panel, null);
            mAlbum = (ImageView) widget.findViewById(R.id.album);
            mSongName = (TextView) widget.findViewById(R.id.song);
            mArtist = (TextView) widget.findViewById(R.id.artist);
            mPlayBtn = (ImageView) widget.findViewById(R.id.play);
            mPlayBtn.requestFocus();
            View prev = widget.findViewById(R.id.prev);
            View next = widget.findViewById(R.id.next);
            mProgressBar = (MySeekbar) widget.findViewById(R.id.seek_bar);
            // set onClick events
            mPlayBtn.setOnClickListener(this);
            prev.setOnClickListener(this);
            next.setOnClickListener(this);

            mPlayPanelCreated = true;
            updateWidget(true);
        }
        return widget;
    }

    int i;

    private View createSelectPanel() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View main = inflater.inflate(R.layout.music_select_panel, null);
        ViewGroup select_panel = (ViewGroup) main.findViewById(R.id.song_list);
        // add list
        for (i = 0; i < mSongList.size(); i++) {
            final MyMusic song = mSongList.get(i);
            View item = inflater.inflate(R.layout.music_list_item,
                    select_panel, false);

            TextView title = (TextView) item.findViewById(R.id.title);
            title.setText(song.getTitle());
            TextView artist = (TextView) item.findViewById(R.id.artist);
            artist.setText(song.getArtist());

            if (i < mSongList.size() - 1) {
                View line = item.findViewById(R.id.line);
                line.setVisibility(View.VISIBLE);
            }
            final long songId = song.getId();
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedId = songId;
                    Log.i("heqianqian", "myselectid===" + songId);
                    play();

                }
            });

            if (i == 0) {
                // head
                item.setBackgroundResource(R.drawable.panel_list_item);
            } else if (i == mSongList.size() - 1) {
                // tail
                item.setBackgroundResource(R.drawable.panel_list_item);
            } else {
                // middle
                item.setBackgroundResource(R.drawable.panel_list_item);
            }

            select_panel.addView(item);
        }

        return main;
    }

    private void updateWidget(boolean force) {
        if (!mPlayPanelCreated || mMediaPlayer == null) {
            return;
        }

        if (currentplaymusic == null) {
            currentplaymusic = mSongList.get(0);
        }
        MyMusic music = currentplaymusic;
        try {
            // meta data changed
            long audioId = currentplaymusic.getId();
            if (mAudioId != audioId || force) {
                String song = currentplaymusic.getTitle();
                String artist = currentplaymusic.getArtist();

                mSongName.setText(song);
                mArtist.setText(artist);
                mProgressBar.setMax((int) mMediaPlayer.getDuration());
                mAudioId = audioId;

                // get album art image
                if (mAlbumWorker != null) {
                    mAlbumWorker.cancel(true);
                }

                mAlbumWorker = new AlbumImageWorker();
                mAlbumWorker.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                Log.i(TAG, "updateWidget(): audo id = " + audioId
                        + ", song name = " + song + ", artist = " + artist);
            }

            // state changed
            if (mMediaPlayer.isPlaying()) {
                mPlayBtn.setImageResource(R.drawable.ic_pause);
                mHandler.removeMessages(MSG_UPDATE_PROGRESS_BAR);
                mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS_BAR);
            } else {
                mHandler.removeMessages(MSG_UPDATE_PROGRESS_BAR);
                mPlayBtn.setImageResource(R.drawable.ic_play);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getAlbumBitmap() {
        long id;
        boolean isOnline;
        String uri;
        Bitmap b = null;

        try {
            // id = mmedia.getAlbumId();
            // isOnline = mService.isOnlineMode();
            // uri = mmedi.getSmallImageUri();
        } catch (Exception e) {
            e.printStackTrace();
            return b;
        }

        // if (id < 0) {
        // return b;
        // }

        // if (isOnline) {
        // b = getAlbumFromOnline(uri);
        // } else {
        // b = getArtwork(id);
        // }

        return b;
    }

    private Bitmap getAlbumFromOnline(String uri) {
        if (!Util.isNetworkAvailable(mContext)) {
            return null;
        }

        if (TextUtils.isEmpty(uri)) {
            return null;
        }

        Bitmap bm = null;// NetClient.getBitmapFromUrl(mContext, uri);
        if (bm != null) {
            return bm;
        } else {
            return null;
        }
    }

    private Bitmap getArtwork(long id) {
        Bitmap bm = null;
        ContentResolver res = mContext.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, id);

        if (uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                bm = BitmapFactory.decodeStream(in);
            } catch (FileNotFoundException ex) {
                // The album art thumbnail does not actually exist. Maybe the
                // user deleted it, or
                // maybe it never existed to begin with.
                Log.i(TAG, "getArtWork: open " + uri.toString()
                        + " failed, try getArtworkFromFile");
                bm = getArtworkFromFile(id);

                if (bm != null) {
                    if (bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
                    }
                }
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                }
            }
        }

        return bm;
    }

    private Bitmap getArtworkFromFile(long albumid) {
        ParcelFileDescriptor pfd = null;
        FileDescriptor fd = null;
        Bitmap bm = null;

        try {
            Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
            pfd = mContext.getContentResolver().openFileDescriptor(uri, "r");
            Log.i(TAG, "getArtworkFromFile: pFD = " + pfd);
            if (pfd != null) {
                fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (IllegalStateException ex) {
        } catch (FileNotFoundException ex) {
            Log.e(TAG, "getArtworkFromFile: FileNotFoundException!");
        } finally {
            if (pfd != null) {
                try {
                    pfd.close();
                    pfd = null;
                } catch (IOException e) {
                    Log.i(TAG, "finally e : " + e.toString());
                }
            }
        }

        Log.i(TAG, "<< getArtworkFromFile: " + bm);
        return bm;
    }

    private void initHandler() {
        mHandler = new Handler(mContext.getMainLooper()) {
            @Override
            public void dispatchMessage(Message msg) {
                switch (msg.what) {
                    case MSG_OPEN_PLAYER_DONE:
                        mOpenPlayerThread = null;
                        if (mListener != null) {
                            mListener.onResponseSpeechResult(new SpeechData(
                                    SpeechData.MUSIC_WIDGET_MODE,
                                    SpeechData.MUSIC_PLAY_PANEL_TAG), false);
                        }
                        break;
                    case MSG_GET_ALBUM_DONE:
                        mAlbumWorker = null;
                        break;
                    case MSG_UPDATE_PROGRESS_BAR:
                        updateProgress();
                        break;
                    case MSG_OPEN_PLAYER_LIST_EMPTY:
                        mOpenPlayerThread = null;
                        if (mListener != null) {
                            String result = mContext
                                    .getString(R.string.response_music_empty);
                            mListener.onResponseSpeechResult(new SpeechData(
                                    SpeechData.RESPONSE_TEXT_MODE, result), false);
                            mListener.onSpeak(result);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void updateProgress() {
        if (mMediaPlayer == null) {
            return;
        }

        try {
            mProgressBar.setProgress((int) mMediaPlayer.getCurrentPosition());

            if (mMediaPlayer.isPlaying()) {
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS_BAR,
                        DELAY_UPDATE_PROGRESS_BAR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver mMetaChangedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive(): action = " + intent.getAction()
                    + ", song name = " + intent.getStringExtra("track")
                    + ", artist = " + intent.getStringExtra("artist")
                    + ", playing = " + intent.getBooleanExtra("playing", false));

            updateWidget(false);
        }

        ;
    };

    private class OpenPlayerThread extends Thread {
        private boolean mIsCanceled;

        @Override
        public void run() {

        }

        public void cancel() {
            mIsCanceled = true;
        }
    }

    public List<MyMusic> getMusicdates() {
        List<MyMusic> musics = new ArrayList<MyMusic>();
        Cursor cursor = mContext.getContentResolver().query(
                Media.EXTERNAL_CONTENT_URI, null, null, null,
                Media.DEFAULT_SORT_ORDER);
        try {
            while (cursor.moveToNext()) {
                MyMusic music = new MyMusic();
                long id = cursor.getLong(cursor
                        .getColumnIndex(Media._ID)); // id
                String title = cursor.getString((cursor
                        .getColumnIndex(Media.TITLE)));// title
                String artist = cursor.getString(cursor
                        .getColumnIndex(Media.ARTIST));// artist
                long duration = cursor.getLong(cursor
                        .getColumnIndex(Media.DURATION));// time
                long size = cursor.getLong(cursor
                        .getColumnIndex(Media.SIZE)); // size
                String url = cursor.getString(cursor
                        .getColumnIndex(Media.DATA));// filepath
                int isMusic = cursor.getInt(cursor
                        .getColumnIndex(Media.IS_MUSIC));// is
                // music
                // or
                // not
                if (isMusic != 0) {
                    music.setId(id);
                    music.setTitle(title);
                    music.setArtist(artist);
                    music.setDuration(duration);
                    music.setSize(size);
                    music.setUrl(url);
                    music.setIsmusic(isMusic);
                    musics.add(music);
                }

            }
        } finally {
            cursor.close();
        }
        return musics;
    }

    private class AlbumImageWorker extends AsyncTask<Void, Void, Bitmap> {
        /**
         * get the album art image
         *
         * @param albumId The album id
         * @return Return the album art bitmap
         */
        protected Bitmap doInBackground(Void... params) {
            Bitmap bm = null;
            try {
                bm = getAlbumBitmap();
            } catch (IllegalArgumentException ex) {
                Log.e(TAG, "AlbumImageWorker called with wrong parameters");
                return null;
            }

            return bm;
        }

        /**
         * update the album icon if got the bitmap
         *
         * @param bm album art bitmap
         */
        protected void onPostExecute(Bitmap bm) {
            if (bm == null) {
                mAlbum.setImageResource(R.drawable.ic_default_album);
            } else {
                mAlbum.setImageBitmap(bm);
            }

            mHandler.sendEmptyMessage(MSG_GET_ALBUM_DONE);
        }
    }
}
