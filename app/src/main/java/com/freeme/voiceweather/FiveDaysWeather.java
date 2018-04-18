/*
 * File name: FiveDaysWeather.java
 * 
 * Description: For qurey Weather.
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentResolver;
import android.database.Cursor;
import android.text.TextUtils;

public class FiveDaysWeather {
    List<CommonDaysWeather> weathers = null;

    public FiveDaysWeather() {
        weathers = new ArrayList<CommonDaysWeather>();
    }

    public List<CommonDaysWeather> getWeathers() {
        return weathers;
    }

    public void setWeathers(List<CommonDaysWeather> weathers) {
        this.weathers = weathers;
    }
}
