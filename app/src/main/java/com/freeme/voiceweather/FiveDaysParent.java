/*
 * File name: FiveDaysParent.java
 * 
 * Description: The qurey Weather.
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

public class FiveDaysParent {
    private int mResult;
    private String mDesc;
    private FiveDaysWeather mFiveDaysWeather;

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

    public FiveDaysWeather getFiveDaysWeather() {
        return mFiveDaysWeather;
    }

    public void setFiveDaysWeather(FiveDaysWeather fiveDaysWeather) {
        this.mFiveDaysWeather = fiveDaysWeather;
    }

    public static FiveDaysParent readFiveDaysParentFromDatabase(ContentResolver resolver, int code) {
        FiveDaysParent fiveDaysParent = new FiveDaysParent();
//		fiveDaysParent.mFiveDaysWeather = FiveDaysWeather.readFiveDaysWeatherFromDatabase(resolver, code);
        if ((fiveDaysParent.mFiveDaysWeather != null) && (fiveDaysParent.mFiveDaysWeather.getWeathers().get(0) != null)) {
            fiveDaysParent.mResult = 0;
            fiveDaysParent.mDesc = "成功 ";
            return fiveDaysParent;
        }
        return null;
    }

}
