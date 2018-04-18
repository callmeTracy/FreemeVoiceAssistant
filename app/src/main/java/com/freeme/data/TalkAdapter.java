/*
 * File name: TalkAdapter.java
 * 
 * Description: The adapter of talk list about man-machine
 *
 * Author: Theobald_wu, contact with wuqizhi@tydtech.com
 * 
 * Date: 2014-7-16   
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
package com.freeme.data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.lang.Override;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.freeme.util.ASRHelper;
import com.freeme.util.Util;
import com.freeme.voiceassistant.ContactCore;
import com.freeme.voiceassistant.HotappCore;
import com.freeme.voiceassistant.MusicMediaPlayer;
import com.freeme.voiceassistant.R;
import com.freeme.voiceassistant.ManMachinePanel;
import com.freeme.voiceassistant.VoiceWeather;
import com.freeme.voiceassistant.WebViewActivity;

/**
 * @author heqianqian on 20160106
 */
public class TalkAdapter extends BaseAdapter {
    private static final String TAG = "[Freeme]TalkAdapter";

    private ManMachinePanel mContext;
    private List<SpeechData> mTalkDatas;
    private InputMethodManager mInputMethodManager;
    String key = "";
    Intent intentmap;
    private int[] bigIcon = {R.drawable.b_0, R.drawable.b_1, R.drawable.b_2,
            R.drawable.b_3, R.drawable.b_4, R.drawable.b_4, R.drawable.b_6,
            R.drawable.b_3, R.drawable.b_3, R.drawable.b_3};
    private int[] icon = {R.drawable.a_0, R.drawable.a_1, R.drawable.a_2,
            R.drawable.a_3, R.drawable.a_4, R.drawable.a_4, R.drawable.a_6,
            R.drawable.a_3, R.drawable.a_3, R.drawable.a_3};

    public TalkAdapter(ManMachinePanel context) {
        super();

        mContext = context;
        mTalkDatas = context.getCurrentScenarioData();
        mInputMethodManager = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public int getCount() {
        return mTalkDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final SpeechData speech = mTalkDatas.get(position);

        // should inflate every time because maybe different layout
        switch (speech.getMode()) {
            case SpeechData.REQUEST_TEXT_MODE:
            case SpeechData.RESPONSE_TEXT_MODE: {
                convertView = inflater
                        .inflate(
                                speech.getMode() == SpeechData.REQUEST_TEXT_MODE ? R.layout.request_text
                                        : R.layout.response_text, parent, false);
                TextView tv = (TextView) convertView.findViewById(R.id.text);
                tv.setText(speech.getData());
                notifyDataSetChanged();
                break;
            }
            case SpeechData.SEARCH_WIDGET_MODE: {
                convertView = inflater.inflate(R.layout.content_search_layout, parent,
                        false);
                TextView tv = (TextView) convertView.findViewById(R.id.search);
                tv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        try {
                            URL url = new URL("http://www.baidu.com.cn/s?wd=" + speech.getData() + "&cl=3");
                            Intent intent = Intent.parseUri(url.toString(), 0);
                            intent.setClass(mContext, WebViewActivity.class);
                            mContext.startActivity(intent);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                });

                break;
            }

            case SpeechData.UNLINE_WIDGET_MODE:
                convertView = inflater.inflate(R.layout.unline_panel, parent,
                        false);
                TextView setting = (TextView) convertView.findViewById(R.id.setting);
                setting.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                        mContext.startActivity(wifiSettingsIntent);
                    }
                });
                break;

            case SpeechData.MAP_WIDGET_MODE: {
                convertView = inflater.inflate(R.layout.content_map_layout, parent,
                        false);
                TextView tv = (TextView) convertView.findViewById(R.id.map);

                tv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        try {
                            String[] strs = speech.getData().split(",");

                            if (Util.isInstallByread("com.baidu.BaiduMap")) {
                                if (ASRHelper.MAP_INTENT_POI.equals(strs[0]) && strs[1] != null) {

                                    intentmap = Intent.getIntent("intent://map/place/search?query=" + strs[1] + "region=" + strs[2] + "&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                                    mContext.startActivity(intentmap);


                                } else if (ASRHelper.MAP_INTENT_ROUTE.equals(strs[0])) {
                                    if (strs[5] != null && strs[4] != null) {
                                        intentmap = Intent.getIntent("intent://map/direction?origin=latlng:" + strs[1] + "," + strs[2] + "|name:" + strs[3] + "&destination=" + strs[4] + "&mode=driving&" +
                                                "region=" + strs[6] + "&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                                        mContext.startActivity(intentmap);
                                    } else if (strs[5] == null && strs[4] != null) {
                                        intentmap = Intent.getIntent("intent://map/direction?origin=latlng:" + strs[1] + "," + strs[2] + "|name:" + strs[3] + "&destination=" + strs[4] + "&mode=driving&" +
                                                "region=" + strs[6] + "&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                                        mContext.startActivity(intentmap);
                                    } else if (strs[5] != null && strs[4] == null) {
                                        intentmap = Intent.getIntent("intent://map/direction?origin=latlng:" + strs[1] + "," + strs[2] + "|name:" + strs[3] + "&destination=" + strs[5] + "&mode=driving&" +
                                                "region=" + strs[6] + "&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                                        mContext.startActivity(intentmap);
                                    }
                                }

                            } else {
                                if (ASRHelper.MAP_INTENT_POI.equals(strs[0]) && strs[1] != null) {
                                    String url = "http://api.map.baidu.com/place/search?query=" + strs[1] + "&region=" + strs[2] + "&output=html&src=yourCompanyName|yourAppName";
                                    intentmap = new Intent(Intent.ACTION_VIEW);
                                    intentmap.setData(Uri.parse(url));
                                    mContext.startActivity(intentmap);
                                } else if (ASRHelper.MAP_INTENT_ROUTE.equals(strs[0])) {
                                    if (strs[5] != null && strs[4] != null) {
                                        String url = "http://api.map.baidu.com/direction?origin=latlng:" + strs[1] + "," + strs[2] + "|name:" + strs[3] + "&destination=" + strs[4] + "&" +
                                                "mode=driving&region=" + strs[6] + "&output=html&src=yourCompanyName|yourAppName";
                                        intentmap = new Intent(Intent.ACTION_VIEW);
                                        intentmap.setData(Uri.parse(url));
                                        mContext.startActivity(intentmap);
                                    } else if (strs[5] == null && strs[4] != null) {
                                        String url = "http://api.map.baidu.com/direction?origin=latlng:" + strs[1] + "," + strs[2] + "|name:" + strs[3] + "&destination=" + strs[4] + "&" +
                                                "mode=driving&region=" + strs[6] + "&output=html&src=yourCompanyName|yourAppName";
                                        intentmap = new Intent(Intent.ACTION_VIEW);
                                        intentmap.setData(Uri.parse(url));
                                        mContext.startActivity(intentmap);
                                    } else if (strs[5] != null && strs[4] == null) {
                                        String url = "http://api.map.baidu.com/direction?origin=latlng:" + strs[1] + "," + strs[2] + "|name:" + strs[3] + "&destination=" + strs[5] + "&" +
                                                "mode=driving&region=" + strs[6] + "&output=html&src=yourCompanyName|yourAppName";
                                        intentmap = new Intent(Intent.ACTION_VIEW);
                                        intentmap.setData(Uri.parse(url));
                                        mContext.startActivity(intentmap);
                                    }


                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                });

                break;
            }
            case SpeechData.SPECIAL_SR_ITEM_MODE: {
                String[] temp = speech.getData().split(DataManager.SEPERATOR);
                convertView = inflater.inflate(R.layout.special_item_panel, parent,
                        false);
                // icon
                // if (speech.getSpecialItemIcon() == null) {
                Drawable d = mContext.getResources().getDrawable(
                        android.R.drawable.sym_def_app_icon);
                if ("translate".equals(temp[0])) {
                    d = mContext.getResources().getDrawable(R.drawable.translate);
                }
                if ("phone".equals(temp[0])) {
                    d = mContext.getResources().getDrawable(R.drawable.phone);
                }
                if ("date".equals(temp[0])) {
                    d = mContext.getResources().getDrawable(R.drawable.date);
                }
                if ("alarm".equals(temp[0])) {
                    d = mContext.getResources().getDrawable(R.drawable.alarm);
                }
                if ("setting".equals(temp[0])) {
                    d = mContext.getResources().getDrawable(R.drawable.setting);
                }
                if ("weather".equals(temp[0])) {
                    d = mContext.getResources().getDrawable(R.drawable.weather);
                }
                if ("music".equals(temp[0])) {
                    d = mContext.getResources().getDrawable(R.drawable.music);
                }
                if ("app".equals(temp[0])) {
                    d = mContext.getResources().getDrawable(R.drawable.app);
                }
                if ("search".equals(temp[0])) {
                    d = mContext.getResources().getDrawable(R.drawable.search);
                }
//                if ("joke".equals(temp[0])) {
//                    d = mContext.getResources().getDrawable(R.drawable.joke);
//                }
                if ("chat".equals(temp[0])) {
                    d = mContext.getResources().getDrawable(R.drawable.chat);
                }
                if ("map".equals(temp[0])) {
                    d = mContext.getResources().getDrawable(R.drawable.map);
                }
            /*
             * if (!DataManager.SYSTEM_DEFAULT.equals(temp[0])) { Intent intent
			 * = new Intent(); ComponentName component =
			 * ComponentName.unflattenFromString(temp[0]);
			 * intent.setComponent(component); PackageManager pm =
			 * mContext.getPackageManager(); ResolveInfo info =
			 * pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY); if
			 * (info != null) { d = info.loadIcon(pm); } }
			 */
                speech.setSpecialItemIcon(d);
                // }
                ImageView iv = (ImageView) convertView.findViewById(R.id.icon);
                iv.setImageDrawable(speech.getSpecialItemIcon());

                // title
                TextView title = (TextView) convertView.findViewById(R.id.title);
                title.setText(mContext.getString(Integer.parseInt(temp[1])));
                // hint
                TextView hint = (TextView) convertView.findViewById(R.id.hint);
                hint.setText(mContext.getString(Integer.parseInt(temp[2])));
                // next arrow
                if (speech.isClickable()) {
                    View next = convertView.findViewById(R.id.next_ind);
                    next.setVisibility(View.VISIBLE);
                    // register click event of list item
                    final int special_item = speech.getSpecialItemType();
                    convertView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mContext.enterRecognizerScenario(special_item);
                        }
                    });
                }
                break;
            }

            case SpeechData.MUSIC_WIDGET_MODE:
                convertView = MusicMediaPlayer.getWidget(speech.getData());
                break;

		/*
         * case SpeechData.ALARM_WIDGET_MODE: {
		 * 
		 * break; }
		 */
            case SpeechData.WEATHER_WIDGET_MODE:
                String noData = speech.getData();
                if (noData != null && !noData.equals("")) {
                    convertView = inflater.inflate(R.layout.voice_weather, parent,
                            false);
                    addWeather(noData, convertView);
                } else {
                    final VoiceWeather weather = speech.getWeather();
                    String mAllData = weather.getAllData();
                    ArrayList<String> cityList = weather.getCity();

                    if (weather.getAllData() == null) {
                        convertView = inflater.inflate(
                                R.layout.weather_widget_panel, parent, false);
                        final ViewGroup promptList1 = (ViewGroup) convertView
                                .findViewById(R.id.prompt_list);
                        for (int i = 0; i < cityList.size(); i++) {
                            final View item = inflater.inflate(
                                    R.layout.weather_list_item, promptList1, false);
                            TextView hint = (TextView) item
                                    .findViewById(R.id.city_item);
                            hint.setText(cityList.get(i));

                            // if (i < cityList.size() - 1) {
                            // View line = item.findViewById(R.id.line);
                            // line.setVisibility(View.VISIBLE);
                            // }

                            item.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int index = promptList1.indexOfChild(v);
                                    weather.setCity(index);
                                    int weatherId = item.getId();
                                    mTalkDatas.remove(speech);
                                    notifyDataSetChanged();
                                }
                            });
                            promptList1.addView(item);
                        }
                    } else {
                        convertView = inflater.inflate(R.layout.voice_weather,
                                parent, false);
                        addWeather(mAllData, convertView);
                    }
                }

                break;
            // add ZMJ [end]

            case SpeechData.PROMPT_WIDGET_MODE: {
                String temp[] = speech.getData().split(DataManager.SEPERATOR);
                String titles = speech.getPromptItemTitle();
                String[] temp1 = null;
                if (titles != null) {
                    temp1 = titles.split(DataManager.SEPERATOR);
                }
                convertView = inflater.inflate(R.layout.prompt_widget_panel,
                        parent, false);
                ViewGroup promptList = (ViewGroup) convertView
                        .findViewById(R.id.prompt_list);

                for (int i = 0; i < temp.length; i++) {
                    View item = inflater.inflate(R.layout.prompt_list_item,
                            promptList, false);
                    TextView hint = (TextView) item.findViewById(R.id.grammar_hint);
                    hint.setText(mContext.getString(Integer.parseInt(temp[i])));

                    if (temp1 != null && i < temp1.length) {
                        TextView title = (TextView) item.findViewById(R.id.title);
                        title.setText(mContext.getString(Integer.parseInt(temp1[i])));
                        title.setVisibility(View.VISIBLE);
                    }

                    promptList.addView(item);
                }
                break;
            }

            case SpeechData.CONTACT_WIDGET_MODE:
                convertView = buildContactWidget(speech);
                break;

            case SpeechData.HOTAPP_WIDGET_MODE:
                convertView = HotappCore.getWidget(speech.getData());
                break;

            default:
                break;
        }

        return convertView;
    }

    public void addSpeech(SpeechData speech) {
        if (mTalkDatas != null) {
            if (speech.getMode() == SpeechData.MUSIC_WIDGET_MODE) {
                clearSpeechItem(SpeechData.MUSIC_WIDGET_MODE);
            }

            if (speech.getMode() == SpeechData.CONTACT_WIDGET_MODE) {
                ContactCore contact = speech.getContact();
                if (contact == null || contact.getState() == ContactCore.NONE) {
                    return;
                }
                // clear all contact widgets at first
                clearSpeechItem(SpeechData.CONTACT_WIDGET_MODE);
            }
            // clear hot-app widgets with tag
            if (speech.getMode() == SpeechData.HOTAPP_WIDGET_MODE) {
                clearHotappItem(speech.getData());
            }
            if (speech.getMode() == SpeechData.RESPONSE_TEXT_MODE) {
                clearnetworkno(speech.getData());
            }

            if (speech.getMode() == SpeechData.UNLINE_WIDGET_MODE) {
                clearUnline(speech.getData());
                mContext.mTTSClient.speak(mContext.getString(R.string.network_no));
            }
            mTalkDatas.add(speech);
            notifyDataSetChanged();
        }

    }

    public void removeAlarmWidget(long alarmId) {

    }

    public void setTalkData() {
        mTalkDatas = mContext.getCurrentScenarioData();
        notifyDataSetChanged();
    }

    private void tempEntry() {

    }

    private void clearSpeechItem(int mode) {
        for (SpeechData item : mTalkDatas) {
            if (item.getMode() == mode) {
                mTalkDatas.remove(item);
                break;
            }
        }
    }

    private boolean hideSoftKeyboard(View v) {
        boolean result = false;
        if (mInputMethodManager != null) {
            result = mInputMethodManager.hideSoftInputFromWindow(
                    v.getWindowToken(), 0);
        }
        return result;
    }

    private View buildContactWidget(final SpeechData speech) {
        final ContactCore contactCore = speech.getContact();
        if (contactCore == null) {
            throw new IllegalAccessError();
        }

        View main = null;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ArrayList<ContactCore.Contact> contacts = contactCore.getList();

        switch (contactCore.getState()) {
            case ContactCore.CALLING: {
                ContactCore.Contact contact = contacts.get(contactCore
                        .getFinalContact());
                main = inflater.inflate(R.layout.contact_calling_panel, null);
                Bitmap bm = contact.photo;
                if (bm != null) {
                    ImageView photo = (ImageView) main.findViewById(R.id.photo);
                    photo.setImageBitmap(bm);
                }
                TextView name = (TextView) main.findViewById(R.id.name);
                name.setText(contact.name);
                TextView number = (TextView) main.findViewById(R.id.number);
                number.setText(contact.number);
                ImageView bar = (ImageView) main.findViewById(R.id.bar);
                contactCore.startCalling(bar);
                break;
            }

            case ContactCore.SENDING_MSG: {
                ContactCore.Contact contact = contacts.get(contactCore
                        .getFinalContact());
                main = inflater.inflate(R.layout.contact_send_message_panel, null);
                TextView name = (TextView) main.findViewById(R.id.name);
                name.setText(contact.name);
                final View sendBtn = main.findViewById(R.id.send);
                sendBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactCore.sendMessage();
                        hideSoftKeyboard(v);
                    }
                });

                final EditText content = (EditText) main.findViewById(R.id.content);

                content.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        contactCore.saveMessageContent(s);

                        if (s != null && s.length() > 0) {
                            Util.setEnabled(sendBtn, true);
                        } else {
                            Util.setEnabled(sendBtn, false);
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                        // do nothing
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                View voiceInput = main.findViewById(R.id.voice_input_msg);
                if (!Util.isNetworkAvailable(mContext)) {
                    voiceInput.setVisibility(View.GONE);
                    content.setHint(mContext
                            .getString(R.string.message_content_hint_no));
                } else {
                    voiceInput.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            contactCore.setMsgContentInsertPos(content
                                    .getSelectionStart());
                            mContext.startSmsVoiceInput(contactCore);
                        }
                    });
                }

                CharSequence message = contactCore.getMessageContent();
                if (message != null && message.length() > 0) {
                    content.setText(message);
                    content.setSelection(message.length());
                } else {
                    // disable send button
                    Util.setEnabled(sendBtn, false);
                }
                break;
            }

            case ContactCore.SEARCH_LIST: {
                main = inflater.inflate(R.layout.contact_search_panel, null);
                final ViewGroup contacts_panel = (ViewGroup) main
                        .findViewById(R.id.contact_list);
                // add list
                for (int i = 0; i < contacts.size(); i++) {
                    ContactCore.Contact contact = contacts.get(i);
                    View item = inflater.inflate(R.layout.contact_list_item,
                            contacts_panel, false);
                    Bitmap bm = contact.photo;
                    if (bm != null) {
                        ImageView photo = (ImageView) item.findViewById(R.id.photo);
                        photo.setImageBitmap(bm);
                    }
                    TextView name = (TextView) item.findViewById(R.id.name);
                    name.setText(contact.name);
                    TextView number = (TextView) item.findViewById(R.id.number);
                    number.setText(contact.number);
                    if (i < contacts.size() - 1) {
                        View line = item.findViewById(R.id.line);
                        line.setVisibility(View.VISIBLE);
                    }
                    item.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int index = contacts_panel.indexOfChild(v);

                            if (contactCore.getFinalContact() != index) {
                                contactCore.selectContact(index);
                                notifyDataSetChanged();
                            }
                        }
                    });
                    if (i == 0) {
                        // head
                        item.setBackgroundResource(R.drawable.panel_list_item);
                    } else if (i == contacts.size() - 1) {
                        // tail
                        item.setBackgroundResource(R.drawable.panel_list_item);
                    } else {
                        // middle
                        item.setBackgroundResource(R.drawable.panel_list_item);
                    }
                    if (contacts.size() > 1) {
                        // selected state
                        if (i == contactCore.getFinalContact()) {
                            item.setSelected(true);
                        }
                    }
                    contacts_panel.addView(item);
                }

                View sendBtn = main.findViewById(R.id.send_msg);
                sendBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactCore.sendTo();
                    }
                });
                View callBtn = main.findViewById(R.id.call);
                callBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactCore.callUp();
                    }
                });
                if (contacts.size() > 1 && contactCore.getFinalContact() == -1) {
                    Util.setEnabled(sendBtn, false);
                    Util.setEnabled(callBtn, false);
                }
                break;
            }

            case ContactCore.CALL_SEL_LIST:
            case ContactCore.SEND_MSG_SEL_LIST: {
                main = inflater.inflate(R.layout.contact_select_panel, null);
                final ViewGroup contacts_panel = (ViewGroup) main
                        .findViewById(R.id.contact_list);
                // add list
                for (int i = 0; i < contacts.size(); i++) {
                    ContactCore.Contact contact = contacts.get(i);
                    View item = inflater.inflate(R.layout.contact_list_item,
                            contacts_panel, false);
                    Bitmap bm = contact.photo;
                    if (bm != null) {
                        ImageView photo = (ImageView) item.findViewById(R.id.photo);
                        photo.setImageBitmap(bm);
                    }
                    TextView name = (TextView) item.findViewById(R.id.name);
                    name.setText(contact.name);
                    TextView number = (TextView) item.findViewById(R.id.number);
                    number.setText(contact.number);
                    if (i < contacts.size() - 1) {
                        View line = item.findViewById(R.id.line);
                        line.setVisibility(View.VISIBLE);
                    }
                    item.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int index = contacts_panel.indexOfChild(v);
                            contactCore.selectContact(index);
                            notifyDataSetChanged();
                        }
                    });
                    if (i == 0) {
                        // head
                        item.setBackgroundResource(R.drawable.panel_list_item);
                    } else if (i == contacts.size() - 1) {
                        // tail
                        item.setBackgroundResource(R.drawable.panel_list_item);
                    } else {
                        // middle
                        item.setBackgroundResource(R.drawable.panel_list_item);
                    }
                    contacts_panel.addView(item);
                }
                break;
            }
            default:
                break;
        }

        if (main != null) {
            View cancel = main.findViewById(R.id.cancel);
            if (cancel != null) {
                cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (contactCore.getState() == ContactCore.CALLING) {
                            contactCore.cancelCalling();
                            removeContactWidget(contactCore);
                        } else if (contactCore.getState() == ContactCore.SENDING_MSG) {
                            if (hideSoftKeyboard(v)) {
                                return;
                            }
                            contactCore.cancelSendSms();
                            removeContactWidget(contactCore);
                        }
                    }
                });
            }
        }
        return main;
    }

    public void clearHotappItem(String tag) {
        for (SpeechData item : mTalkDatas) {
            if (item.getMode() == SpeechData.HOTAPP_WIDGET_MODE && tag != null
                    && tag.equals(item.getData())) {
                mTalkDatas.remove(item);
                break;
            }
        }
    }


    private void clearUnline(String tag) {
        for (SpeechData item : mTalkDatas) {
            if (item.getMode() == SpeechData.UNLINE_WIDGET_MODE && tag != null
                    && tag.equals(item.getData())) {
                mTalkDatas.remove(item);
                break;
            }
        }
    }

    private void clearnetworkno(String tag) {
        for (SpeechData item : mTalkDatas) {
            if (item.getMode() == SpeechData.RESPONSE_TEXT_MODE && tag != null
                    && tag.equals(mContext.getString(R.string.network_no))) {
                mTalkDatas.remove(item);
                break;
            }
        }
    }

    public void removeContactWidget(ContactCore contact) {
        for (SpeechData item : mTalkDatas) {
            ContactCore c = item.getContact();
            if (c != null && c == contact) {
                mTalkDatas.remove(item);
                notifyDataSetChanged();
                break;
            }
        }
    }


    private void addWeather(String mWeatherData, View convertView) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> maps;
        TextView mFirWindy = (TextView) convertView.findViewById(R.id.mWindy);
        // TextView mFirDate = (TextView)
        // convertView.findViewById(R.id.date_weather);
        // TextView mFirDescription = (TextView)
        // convertView.findViewById(R.id.description);
        TextView mFirWeatherTemp = (TextView) convertView
                .findViewById(R.id.mTemp);
        TextView mCityAdress = (TextView) convertView
                .findViewById(R.id.city_adress);
        RelativeLayout noData = (RelativeLayout) convertView
                .findViewById(R.id.relativeLayout1);
        ListView mList = (ListView) convertView.findViewById(R.id.listView1);
        ImageView icon1 = (ImageView) convertView.findViewById(R.id.imageView1);
        if (mWeatherData.equals("noNetwork")) {
            mList.setVisibility(View.GONE);
            noData.setVisibility(View.GONE);
        } else if (mWeatherData.equals("noCity")) {
            mList.setVisibility(View.GONE);
            noData.setVisibility(View.GONE);
        } else {
            mList.setVisibility(View.VISIBLE);
            noData.setVisibility(View.VISIBLE);
            String[] spitData = mWeatherData.split(",");
            mFirWindy.setText(spitData[3]);
            // mFirDate.setText(spitData[5]);
            // mFirDescription.setText(spitData[2]);
            mFirWeatherTemp.setText(spitData[4]);
            mCityAdress.setText(spitData[0]);
            int iconId;
            if (Integer.parseInt(spitData[6]) == -1) {
                iconId = Util.getIcon(Integer.parseInt(spitData[7]), "big");
            } else {
                iconId = Util.getIcon(Integer.parseInt(spitData[6]), "big");
            }
            if (iconId != -1) {
                icon1.setImageResource(iconId);
            }
            maps = new HashMap<String, Object>();
            maps.put("week", spitData[8]);
            maps.put("date", spitData[9]);
            int sIconId;
            if (Integer.parseInt(spitData[10]) == -1) {
                sIconId = Util.getIcon(Integer.parseInt(spitData[11]), "small");
            } else {
                sIconId = Util.getIcon(Integer.parseInt(spitData[10]), "small");
            }
            if (sIconId != -1) {
                maps.put("icon2", sIconId);
            }
            maps.put("temp", spitData[12]);
            list.add(maps);

            // two day
            maps = new HashMap<String, Object>();
            maps.put("week", spitData[13]);
            maps.put("date", spitData[14]);
            int sIconId1;
            if (Integer.parseInt(spitData[15]) == -1) {
                sIconId1 = Util.getIcon(Integer.parseInt(spitData[16]), "small");
            } else {
                sIconId1 = Util.getIcon(Integer.parseInt(spitData[15]), "small");
            }
            if (sIconId1 != -1) {
                maps.put("icon2", sIconId1);
            }
            maps.put("temp", spitData[17]);
            list.add(maps);

            // three day
            maps = new HashMap<String, Object>();
            maps.put("week", spitData[18]);
            maps.put("date", spitData[19]);
            int sIconId2;
            if (Integer.parseInt(spitData[20]) == -1) {
                sIconId2 = Util.getIcon(Integer.parseInt(spitData[21]), "small");
            } else {
                sIconId2 = Util.getIcon(Integer.parseInt(spitData[20]), "small");
            }
            if (sIconId2 != -1) {
                maps.put("icon2", sIconId2);
            }
            maps.put("temp", spitData[22]);
            list.add(maps);

            // fourday
            /*
             * maps = new HashMap<String, Object>(); maps.put("week",
			 * spitData[23]); maps.put("date", spitData[24]); int sIconId3;
			 * if(Integer.parseInt(spitData[25])==-1){ sIconId3 =
			 * Util.etIcon(Integer.parseInt(spitData[26]), "small"); }else{ sIconId3
			 * = Util.getIcon(Integer.parseInt(spitData[25]), "small"); }
			 * if(sIconId3!=-1){ maps.put("icon2", sIconId3); }
			 * 
			 * maps.put("temp", spitData[27]); list.add(maps);
			 */

            // fiveday
            /*
             * maps = new HashMap<String, Object>(); maps.put("week",
			 * spitData[18]); maps.put("date", spitData[19]); int sIconId4;
			 * if(Integer.parseInt(spitData[30])==-1){ sIconId4 =
			 * Util.getIcon(Integer.parseInt(spitData[31]), "small"); }else{
			 * sIconId4= Util.getIcon(Integer.parseInt(spitData[30]), "small"); }
			 * if(sIconId4!=-1){ maps.put("icon2", sIconId4); }
			 * 
			 * maps.put("temp", spitData[32]); list.add(maps);
			 */
            SimpleAdapter adapter = new SimpleAdapter(mContext, list,
                    R.layout.voice_weather_item, new String[]{"week", "date",
                    "icon2", "temp"}, new int[]{R.id.week,
                    R.id.date, R.id.icon, R.id.temp});
            mList.setAdapter(adapter);
        }
    }

}
