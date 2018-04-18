package com.freeme.voiceassistant;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ImageView;
import com.freeme.data.SpeechData;
import com.freeme.util.ASRHelper;
import com.freeme.util.Util;
import com.freeme.voiceassistant.ASRRequestor.onRecongnitionListener;
import java.util.ArrayList;
import java.util.List;

public class ContactCore {
    public static final int NONE = 0;
    public static final int CONTACT_NOT_EXSIT = 1;
    public static final int CALL_SEL_LIST = 2;
    public static final int SEND_MSG_SEL_LIST = 3;
    public static final int SEARCH_LIST = 4;
    public static final int CALLING = 5;
    public static final int SENDING_MSG = 6;
    private static final String TAG = "[Freeme]ContactCore";
    private static final int CALLING_WAIT_DURATION = 5000;
    private Context mContext;
    private ArrayList<Contact> mContactList = new ArrayList<Contact>();
    private int mSelectedIndex = -1;
    private int mVoiceCmd;
    private int mState;
    private onRecongnitionListener mListener;
    private ObjectAnimator mCallingWaitAnimation;
    private boolean mCancelCalling;
    private CharSequence mMessageContent;
    private int mContentInsertPos;

    public ContactCore(Context context, int cmd, String[] name, onRecongnitionListener l) {
        mContext = context;
        mVoiceCmd = cmd;
        mListener = l;
        mBuildContactTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, name);

    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
    }

    public ArrayList<Contact> getList() {
        return mContactList;
    }

    public int getFinalContact() {
        return mSelectedIndex;
    }

    public void selectContact(int index) {
        if (index >= 0 && index < mContactList.size()) {
            mSelectedIndex = index;
            updateState();
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Calling up that can be canceled in animation duration
     *
     * @param bar progress
     */
    public void startCalling(ImageView bar) {
        bar.setPivotX(0);
        mCancelCalling = false;
        mCallingWaitAnimation = ObjectAnimator.ofFloat(bar, "scaleX", 0, 1);
        mCallingWaitAnimation.setDuration(CALLING_WAIT_DURATION);
        mCallingWaitAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mCancelCalling) {
                    callUp();
                }
                if (mListener != null) {
                    mListener.onRemoveContactSpeech(ContactCore.this);
                }
            }
        });
        mCallingWaitAnimation.start();
    }


    public void cancelCalling() {
        mCancelCalling = true;
        mCallingWaitAnimation.cancel();
        mListener.onResponseSpeechResult(
                new SpeechData(SpeechData.RESPONSE_TEXT_MODE, mContext.getString(R.string.response_calling_cancel)), false);
        mListener.onSpeak(mContext.getString(R.string.response_calling_cancel));
    }


    public void cancelSendSms() {
        mListener.onResponseSpeechResult(
                new SpeechData(SpeechData.RESPONSE_TEXT_MODE, mContext.getString(R.string.response_calling_cancel)), false);
        mListener.onSpeak(mContext.getString(R.string.response_calling_cancel));

    }


    public void saveMessageContent(CharSequence s) {
        mMessageContent = s;
    }

    public void setMsgContentInsertPos(int pos) {
        mContentInsertPos = pos;
    }

    public void insertMessageContent(CharSequence s) {
        if (mMessageContent != null) {
            StringBuilder buffer = new StringBuilder(mMessageContent);
            buffer.insert(mContentInsertPos, s);
            mMessageContent = buffer.toString();
        } else {
            mMessageContent = s;
        }
    }

    public CharSequence getMessageContent() {
        return mMessageContent;
    }

    public void sendMessage() {
        if (mMessageContent == null || mMessageContent.length() == 0) {
            return;
        }

        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> texts = smsManager.divideMessage(mMessageContent.toString());
        if (Util.isSimCardExist(mContext)) {

            if (mMessageContent.toString().length() > 70) {
                List<String> contents = smsManager.divideMessage(mMessageContent.toString());
                for (String sms : contents) {
                    smsManager.sendTextMessage(mContactList.get(mSelectedIndex).number, null, sms, null, null);
                }
            } else {
                smsManager.sendTextMessage(mContactList.get(mSelectedIndex).number, null, mMessageContent.toString(), null, null);
            }
            sendEnd();
        }
    }

    public void sendEnd() {
        if (mListener != null) {
            mListener.onRemoveContactSpeech(this);
            String action = mContext.getString(R.string.response_send_message_success);
            mListener.onResponseSpeechResult(
                    new SpeechData(SpeechData.RESPONSE_TEXT_MODE, action), false);
            mListener.onSpeak(action);
        }
    }

    /**
     * launcher intent that its action is Intent.ACTION_CALL
     */
    public void callUp() {
        Intent intent = new Intent(Intent.ACTION_CALL,
                Uri.parse("tel:" + mContactList.get(mSelectedIndex).number));
        Util.launcherIntent(mContext, intent);
    }

    /**
     * launcher intent that its action is Intent.ACTION_SENDTO
     */
    public void sendTo() {
        Intent intent = new Intent(Intent.ACTION_SENDTO,
                Uri.parse("smsto:" + mContactList.get(mSelectedIndex).number));
        Util.launcherIntent(mContext, intent);
    }

    private void updateState() {
        if (mContactList.isEmpty()) {
            mState = CONTACT_NOT_EXSIT;
        } else if (ASRHelper.ASR_GRAM_SEARCH_CONTACT == mVoiceCmd) {
            mState = SEARCH_LIST;
        } else if (ASRHelper.ASR_GRAM_CALL_UP == mVoiceCmd) {
            // If the contact is unique, enter directly calling state
            mState = mSelectedIndex == -1 ? CALL_SEL_LIST : CALLING;
        } else if (ASRHelper.ASR_GRAM_SEND_MSG == mVoiceCmd) {
            // If the contact is unique, enter directly sending message state
            mState = mSelectedIndex == -1 ? SEND_MSG_SEL_LIST : SENDING_MSG;
        } else {
            mState = NONE;
        }
    }

    private void showWidget() {
        String action = null;
        if (CONTACT_NOT_EXSIT == mState) {
            action = mContext.getString(R.string.response_contact_not_exsit);
        } else if (CALLING == mState) {
            action = String.format(
                    mContext.getString(R.string.response_calling),
                    mContactList.get(mSelectedIndex).name);
        } else if (SENDING_MSG == mState) {
            action = mContext.getString(R.string.response_send_message);
        } else if (CALL_SEL_LIST == mState || SEND_MSG_SEL_LIST == mState) {
            action = mContext.getString(R.string.response_select_contact);
        }

        if (action != null && mListener != null) {
            mListener.onResponseSpeechResult(
                    new SpeechData(SpeechData.RESPONSE_TEXT_MODE, action), false);
            mListener.onSpeak(action);
        }

        if (mListener != null &&
                NONE != mState && CONTACT_NOT_EXSIT != mState) {
            mListener.onResponseSpeechResult(new SpeechData(this), false);
        }
    }

    public static class Contact {
        public String name;
        public String number;
        public Bitmap photo;
    }

    private AsyncTask<String, Void, Void> mBuildContactTask = new AsyncTask<String, Void, Void>() {
        @Override
        protected Void doInBackground(String... names) {
            for (String name : names) {
                /*
                 * Query phone number and photo thumb according contact name, if
                 * phone number is null, it is not a contact.
                 */
                addContactsFromDatabase(name);
            }
            if (mContactList.size() == 1) {
                mSelectedIndex = 0;
            }
            updateState();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            showWidget();
        }

        private void addContactsFromDatabase(String contact) {
            String contact_name = null;
            if (contact != null) {
                contact_name = exChange2(contact);
            }
            ContentResolver contentResolver = mContext.getContentResolver();
            // search all contacts with display_name
            Cursor cursor = contentResolver.query(Contacts.CONTENT_URI,
                    new String[]{Contacts._ID, Contacts.HAS_PHONE_NUMBER, Contacts.PHOTO_ID},
                    Contacts.DISPLAY_NAME + "='" + contact_name + "'",
                    null, null);
            while (cursor.moveToNext()) {
                String number = null;
                Bitmap bitmap = null;
                // should have available number
                String hasNumber = cursor.getString(cursor.getColumnIndex(Contacts.HAS_PHONE_NUMBER));
                if (Integer.parseInt(hasNumber) > 0) {
                    long id = cursor.getLong(cursor.getColumnIndex(Contacts._ID));
                    // find phone number by contact ID
                    Cursor phoneCur = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id,
                            null, null);
                    if (phoneCur.moveToFirst()) {
                        number = phoneCur.getString(0);
                    }
                    phoneCur.close();
                    // find photo thumb by contact ID
                    int photoId = cursor.getInt(cursor.getColumnIndex(Contacts.PHOTO_ID));
                    if (photoId > 0) {
                        Uri contactUri = ContentUris.withAppendedId(
                                Contacts.CONTENT_URI, id);
                        Uri photoUri = Uri.withAppendedPath(contactUri,
                                Contacts.Photo.CONTENT_DIRECTORY);
                        Cursor photoCursor = contentResolver.query(photoUri,
                                new String[]{Contacts.Photo.PHOTO}, null, null, null);
                        if (photoCursor.moveToFirst()) {
                            byte[] data = photoCursor.getBlob(0);
                            if (data != null) {
                                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            }
                        }
                        photoCursor.close();
                    }
                    // add contact to list
                    if (number != null && !number.isEmpty()) {
                        Contact c = new Contact();
                        c.name = contact_name;
                        c.number = number;
                        c.photo = bitmap;
                        mContactList.add(c);
                        Log.i(TAG, "addContactsFromDatabase(): name = " + contact_name
                                + ", number = " + number);
                    }
                }
            }
            cursor.close();
        }
    };

    public static String exChange2(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.substring(i, i + 1).equals(str.substring(i, i + 1).toUpperCase())) {
                str.substring(i, i + 1).toLowerCase();
            }
        }
        return str;
    }

}
