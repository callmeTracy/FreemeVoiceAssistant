/*
 * File name: TodayParent.java
 * 
 * Description:For qurey Weather.
 *
 * Author: zhangmingjun, contact with zhangmingjun@tydtech.com
 * 
 * Date: 2014-09-13   
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
package com.freeme.voiceweather;

import android.content.ContentResolver;
import android.text.TextUtils;

public class TodayParent {
    private int mResult;
    private String mDesc;
    private CommonDaysWeather mTodayWeather;

    public int getResult() {
        return mResult;
    }

    public void setResult(int result) {
        this.mResult = result;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        this.mDesc = desc;
    }

    public CommonDaysWeather getTodayWeather() {
        return mTodayWeather;
    }

    public void setTodayWeather(CommonDaysWeather todayWeather) {
        this.mTodayWeather = todayWeather;
    }

    public static TodayParent readTodayParentFromDatabase(ContentResolver resolver, int code) {
        TodayParent todayParent = new TodayParent();
        if (todayParent.mTodayWeather != null) {
            todayParent.mResult = 0;
            todayParent.mDesc = "成功";
            return todayParent;
        }
        return null;
    }
}
