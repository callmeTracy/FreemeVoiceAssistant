/*
 * File name: MusicPlayer.java
 * 
 * Description: The music player
 *
 * Author: Theobald_wu, contact with wuqizhi@tydtech.com
 * 
 * Date: 2014-8-23   
 * 
 * Copyright (C) 2014 TYD Technology Co.,Ltd.
 * 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.freeme.voiceassistant;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.provider.MediaStore.Audio.Media;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.freeme.util.Util;
import com.freeme.view.MySeekbar;

import com.freeme.data.SpeechData;
import com.freeme.music.IMediaPlaybackService;

public class MusicPlayer implements View.OnClickListener {
    private static final String TAG = "[Freeme]MusicPlayer";
    private static final String MUSIC_SERVICE_NAME = "com.freeme.music.MediaPlaybackService";
    private static final String PLAYSTATE_CHANGED = "com.android.music.playstatechanged";
    private static final String META_CHANGED = "com.android.music.metachanged";
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    private static final int MSG_OPEN_PLAYER_DONE = 1;
    private static final int MSG_GET_ALBUM_DONE = 2;
    private static final int MSG_UPDATE_PROGRESS_BAR = 3;
    private static final int MSG_OPEN_PLAYER_LIST_EMPTY = 4;

    public static final int TITLE_LIST = 1;
    public static final int ARTIST_LIST = 2;

    private static final int DELAY_UPDATE_PROGRESS_BAR = 1000;
    private static final int RAND_POS = 0;

    private volatile static MusicPlayer mInstance; // singleton
    private IMediaPlaybackService mService;
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
    private List<SongData> mSongList = new ArrayList<SongData>();
    private ASRRequestor.onRecongnitionListener mListener;

    private boolean mPlayPanelCreated;
    private ImageView mAlbum;
    private TextView mSongName;
    private TextView mArtist;
    private ImageView mPlayBtn;
    private MySeekbar mProgressBar;
    private long mAudioId;
    //add by mjzhang 20141105 for fast switch song
    private MusicServiceHandler MusicServiceHandler;

    private MusicPlayer(Context context, ASRRequestor.onRecongnitionListener listener) {
        mContext = context;
        mListener = listener;
        bindPlaybackService();
        initHandler();
        //add by mjzhang 20141105 for fast switch song
        MusicServiceHandler = new MusicServiceHandler();
        IntentFilter filter = new IntentFilter();
        filter.addAction(META_CHANGED);
        filter.addAction(PLAYSTATE_CHANGED);
        mContext.registerReceiver(mMetaChangedReceiver, filter);

    }

    public static MusicPlayer getInstance(Context context, ASRRequestor.onRecongnitionListener listener) {
        if (mInstance == null) {
            synchronized (MusicPlayer.class) {
                if (mInstance == null) {
                    mInstance = new MusicPlayer(context, listener);
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
        mSongList.clear();
        mPlayPanelCreated = false;
        mAudioId = -1;

        if (TITLE_LIST == type && mKey != null) {
            // search the same song name list
            searchSameTitleSongs(mKey);
            if (mSongList.size() > 1) {
                // should select song at first
                if (mListener != null) {
                    String result = mContext.getString(R.string.response_select_muisc);
                    mListener.onResponseSpeechResult(
                            new SpeechData(SpeechData.RESPONSE_TEXT_MODE, result), false);
                    mListener.onSpeak(result);
                    // show select panel
                    mListener.onResponseSpeechResult(new SpeechData(
                            SpeechData.MUSIC_WIDGET_MODE,
                            SpeechData.MUSIC_SELECT_PANEL_TAG), false);
                }
            } else if (!mSongList.isEmpty()) {
                mSelectedId = mSongList.get(0).id;
                play();
            } else {
                // not found
                mHandler.sendEmptyMessage(MSG_OPEN_PLAYER_LIST_EMPTY);
            }
        } else {
            // play the first song or play artist's song
            play();
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

        if (TITLE_LIST == type && mKey != null) {
            // search the same song name list
            searchSameTitleSongs(mKey, artist);
            if (mSongList.size() > 1) {
                // should select song at first
                if (mListener != null) {
                    String result = mContext.getString(R.string.response_select_muisc);
                    mListener.onResponseSpeechResult(
                            new SpeechData(SpeechData.RESPONSE_TEXT_MODE, result), false);
                    mListener.onSpeak(result);
                    // show select panel
                    mListener.onResponseSpeechResult(new SpeechData(
                            SpeechData.MUSIC_WIDGET_MODE,
                            SpeechData.MUSIC_SELECT_PANEL_TAG), false);
                }
            } else if (!mSongList.isEmpty()) {
                mSelectedId = mSongList.get(0).id;
                play();
            } else {
                // not found
                mHandler.sendEmptyMessage(MSG_OPEN_PLAYER_LIST_EMPTY);
            }
        } else {
            // play the first song or play artist's song
            play();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                //modified by mjzhang 20141103 for fast seitch
                case R.id.play:
                    if (mService.isPlaying()) {
                        MusicServiceHandler.removeMessages(MusicServiceHandler.MUSIC_PAUSE);
                        MusicServiceHandler.sendEmptyMessage(MusicServiceHandler.MUSIC_PAUSE);
//                    mService.pause();
                    } else {
                        MusicServiceHandler.removeMessages(MusicServiceHandler.MUSIC_PLAY);
                        MusicServiceHandler.sendEmptyMessage(MusicServiceHandler.MUSIC_PLAY);
//                    mService.play();
                    }
                    break;

                case R.id.prev:
                    MusicServiceHandler.removeMessages(MusicServiceHandler.MUSIC_PREV);
                    MusicServiceHandler.sendEmptyMessageDelayed(MusicServiceHandler.MUSIC_PREV, 300);
//                mService.prev();
                    break;

                case R.id.next:
                    MusicServiceHandler.removeMessages(MusicServiceHandler.MUSIC_NEXT);
                    MusicServiceHandler.sendEmptyMessageDelayed(MusicServiceHandler.MUSIC_NEXT, 300);
//                mService.next();
                    break;

                default:
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void play() {
        if (mOpenPlayerThread != null) {
            mOpenPlayerThread.cancel();
            mOpenPlayerThread = null;
        }

        mOpenPlayerThread = new OpenPlayerThread();
        mOpenPlayerThread.start();

        Log.i(TAG, "play()...");
    }

    private void searchSameTitleSongs(String key) {
        Cursor cursor = mContext.getContentResolver().query(
                Media.EXTERNAL_CONTENT_URI,
                new String[]{Media._ID, Media.TITLE, Media.ARTIST}, // projection
                Media.IS_MUSIC + "!='0'", // selection
                null, Media.DEFAULT_SORT_ORDER);

        try {
            while (cursor.moveToNext()) {
                // search by title
                String title = cursor.getString(cursor.getColumnIndex(Media.TITLE));

                if (key.equals(title)) {
                    SongData song = new SongData();
                    song.id = cursor.getLong(cursor.getColumnIndex(Media._ID));
                    song.title = title;
                    song.artist = cursor.getString(cursor.getColumnIndex(Media.ARTIST));
                    mSongList.add(song);
                    Log.i(TAG, "searchSameTitleSongs(): add song id = " + song.id);
                }
            }
        } finally {
            cursor.close();
        }
    }


    private void searchSameTitleSongs(String key, String artist) {
        Cursor cursor = mContext.getContentResolver().query(
                Media.EXTERNAL_CONTENT_URI,
                new String[]{Media._ID, Media.TITLE, Media.ARTIST}, // projection
                Media.IS_MUSIC + "!='0'", // selection
                null, Media.DEFAULT_SORT_ORDER);

        try {
            while (cursor.moveToNext()) {
                // search by title
                String title = cursor.getString(cursor.getColumnIndex(Media.TITLE));
                String artistname = cursor.getString(cursor.getColumnIndex(Media.ARTIST));
                if (key.equals(title) && artist.equals(artistname)) {
                    SongData song = new SongData();
                    song.id = cursor.getLong(cursor.getColumnIndex(Media._ID));
                    song.title = title;
                    song.artist = artist;
                    mSongList.add(song);
                    Log.i(TAG, "searchSameTitleSongs(): add song id = " + song.id);
                }
            }
        } finally {
            cursor.close();
        }
    }

    private void bindPlaybackService() {
        if (!mBound) {
            Intent intent = new Intent(MUSIC_SERVICE_NAME);
            intent.setPackage("com.freeme.music");
            mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
            mBound = true;
        }
    }

    private void onRelease() {
        if (mBound) {
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
                if (mService != null && mService.isPlaying()) {
                    mService.pause();
                    mService.stopStatusBarControl();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            mContext.unregisterReceiver(mMetaChangedReceiver);
            // Detach connection.
            mContext.unbindService(mServiceConnection);
            mBound = false;
            mInstance = null;
        }
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

    private View createSelectPanel() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View main = inflater.inflate(R.layout.music_select_panel, null);
        ViewGroup select_panel = (ViewGroup) main.findViewById(R.id.song_list);

        // add list
        for (int i = 0; i < mSongList.size(); i++) {
            SongData song = mSongList.get(i);
            View item = inflater.inflate(R.layout.music_list_item, select_panel, false);

            TextView title = (TextView) item.findViewById(R.id.title);
            title.setText(song.title);
            TextView artist = (TextView) item.findViewById(R.id.artist);
            artist.setText(song.artist);

            if (i < mSongList.size() - 1) {
                View line = item.findViewById(R.id.line);
                line.setVisibility(View.VISIBLE);
            }

            final long songId = song.id;
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedId = songId;
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

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = IMediaPlaybackService.Stub.asInterface(service);
            mCondition.open();
            Log.i(TAG, "onServiceConnected()...");
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            updateWidget(true);
        }
    };

    private void updateWidget(boolean force) {
        if (!mPlayPanelCreated || mService == null) {
            return;
        }

        try {
            // meta data changed
            long audioId = mService.getAudioId();
            if (mAudioId != audioId || force) {
                String song = mService.getTrackName();
                String artist = mService.getArtistName();

                mSongName.setText(song);
                mArtist.setText(artist);
                mProgressBar.setMax((int) mService.duration());
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
            if (mService.isPlaying()) {
                mPlayBtn.setImageResource(R.drawable.ic_pause);
                mHandler.removeMessages(MSG_UPDATE_PROGRESS_BAR);
                mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS_BAR);
            } else {
                mHandler.removeMessages(MSG_UPDATE_PROGRESS_BAR);
                mPlayBtn.setImageResource(R.drawable.ic_play);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getAlbumBitmap() {
        long id;
        boolean isOnline;
        String uri;
        Bitmap b = null;

        try {
            id = mService.getAlbumId();
            isOnline = mService.isOnlineMode();
            uri = mService.getSmallImageUri();
        } catch (RemoteException e) {
            e.printStackTrace();
            return b;
        }

        if (id < 0) {
            return b;
        }

        if (isOnline) {
            b = getAlbumFromOnline(uri);
        } else {
            b = getArtwork(id);
        }

        return b;
    }

    private Bitmap getAlbumFromOnline(String uri) {
        if (!Util.isNetworkAvailable(mContext)) {
            return null;
        }

        if (TextUtils.isEmpty(uri)) {
            return null;
        }

        Bitmap bm = null;//NetClient.getBitmapFromUrl(mContext, uri);
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
                            String result = mContext.getString(R.string.response_music_empty);
                            mListener.onResponseSpeechResult(
                                    new SpeechData(SpeechData.RESPONSE_TEXT_MODE, result), false);
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
        if (mService == null) {
            return;
        }

        try {
            mProgressBar.setProgress((int) mService.position());

            if (mService.isPlaying()) {
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS_BAR,
                        DELAY_UPDATE_PROGRESS_BAR);
            }
        } catch (RemoteException e) {
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
            // create player list
            ArrayList<Long> list = new ArrayList<Long>();
            int currentPos = RAND_POS;
            Cursor cursor = mContext.getContentResolver().query(
                    Media.EXTERNAL_CONTENT_URI,
                    new String[]{Media._ID, Media.ARTIST}, //projection
                    Media.IS_MUSIC + "!='0'", //selection
                    null, Media.DEFAULT_SORT_ORDER);

            try {
                int count = cursor.getCount();
                cursor.moveToNext();

                for (int i = 0; i < count; i++) {
                    long id = cursor.getLong(cursor.getColumnIndex(Media._ID));

                    if (ARTIST_LIST == mType) {
                        // add all artist's music by artist key
                        String artist = cursor.getString(cursor.getColumnIndex(Media.ARTIST));

                        if (mKey.equals(artist)) {
                            list.add(id);
                        }
                    } else {
                        // add all music
                        list.add(id);
                        // set position by title key
                        if (mKey != null && RAND_POS == currentPos) {
                            if (id == mSelectedId) {
                                currentPos = i;
                            }
                        }
                    }

                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }

            Log.i(TAG, "OpenPlayerThread.run(): currentPos = " + currentPos
                    + ", list length = " + list.size() + ", mSelectedId = " + mSelectedId);
            if (list.size() == 0) {
                mHandler.sendEmptyMessage(MSG_OPEN_PLAYER_LIST_EMPTY);
                return;
            }

            if (mService == null) {
                mCondition.block();
            }

            if (mIsCanceled) {
                return;
            }

            // start playing
            try {
                long[] playList = new long[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    playList[i] = list.get(i);
                }
                if (mService != null) {
                    mService.setOnlineMode(false);
                    mService.open(playList, currentPos);
                    mService.play();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            mHandler.sendEmptyMessage(MSG_OPEN_PLAYER_DONE);
        }

        public void cancel() {
            mIsCanceled = true;
        }
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

    //add by mjzhang 20141103 for fast switch
    public class MusicServiceHandler extends Handler {


        public static final int MUSIC_NEXT = 1;
        public static final int MUSIC_PREV = 2;
        public static final int MUSIC_PLAY = 3;
        public static final int MUSIC_PAUSE = 4;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MUSIC_NEXT:
                    try {
                        mService.next();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    break;
                case MUSIC_PREV:
                    try {
                        mService.prev();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    break;
                case MUSIC_PLAY:
                    try {
                        mService.play();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    break;
                case MUSIC_PAUSE:
                    try {
                        mService.pause();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    break;


                default:
                    break;
            }
        }
    }

    //end
    private class SongData {
        long id;
        String title;
        String artist;
    }
}
