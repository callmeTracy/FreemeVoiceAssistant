/*
 * File name: IconCacheArray.java
 * 
 * Description: cache pool, it can cache image bitmaps and so on
 *
 * Author: Theobald_wu, contact with wuqizhi@tydtech.com
 * 
 * Date: 2014-9-29   
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

public class CachePool<Type> {
    private static final int POOL_SIZE = 10;
    private int[] mKeys = new int[POOL_SIZE];
    private Object[] mValues = new Object[POOL_SIZE];
    private int[] mEntries = new int[POOL_SIZE]; // used to remove the first cache except top-app if full
    private boolean[] mIsMaintains = new boolean[POOL_SIZE]; // do not deleted
    private int mEntriesId = 0;
    private int mLength = 0;

    public void put(int key, Type value) {
        put(key, value, false);
    }

    public void put(int key, Type value, boolean isMaintain) {
        mEntriesId++;
        // if exist, replace value
        for (int i = 0; i < POOL_SIZE; i++) {
            if (mKeys[i] == key) {
                mValues[i] = value;
                mEntries[i] = mEntriesId;
                mIsMaintains[i] = isMaintain;
                return;
            }
        }

        // append new item, check whether full at first
        if (mLength == POOL_SIZE) {
            // replace the first entry
            flush(key, value, isMaintain);
            return;
        }
        add(key, value, isMaintain);
    }

    public Type get(int key) {
        Type result = null;
        for (int i = 0; i < POOL_SIZE; i++) {
            if (mKeys[i] == key) {
                result = (Type) mValues[i];
                break;
            }
        }
        return result;
    }

    private void add(int key, Type value, boolean isMaintain) {
        if (mLength >= POOL_SIZE) {
            throw new IllegalAccessError("add(): over pool size!");
        }
        mKeys[mLength] = key;
        mValues[mLength] = value;
        mEntries[mLength] = mEntriesId;
        mIsMaintains[mLength] = isMaintain;
        mLength++;
    }

    private void flush(int key, Type value, boolean isMaintain) {
        int flushIndex = -1;
        int minEntries = Integer.MAX_VALUE;
        for (int i = 0; i < POOL_SIZE; i++) {
            if (mIsMaintains[i]) {
                continue;
            }
            if (mEntries[i] < minEntries) {
                minEntries = mEntries[i];
                flushIndex = i;
            }
        }
        // replace the new value
        mKeys[flushIndex] = key;
        mValues[flushIndex] = value;
        mEntries[flushIndex] = mEntriesId;
        mIsMaintains[flushIndex] = isMaintain;
    }
}
