package com.coderpage.codelab.search;

import android.content.Context;
import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.coderpage.codelab.codelab.R;
import com.coderpage.codelab.model.DaoSession;
import com.coderpage.codelab.model.QuerySource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lc. 2017-08-19
 * @since 0.1.0
 */

class SearchSourceFakeHelper {

    static void initIfNeed(Context context, DaoSession daoSession) {

        AsyncTask.THREAD_POOL_EXECUTOR.execute(() -> {
            boolean sourceInitOk = daoSession.getQuerySourceDao().count() > 0;
            if (sourceInitOk) return;

            InputStream inputStream = null;
            BufferedReader reader = null;
            try {
                inputStream = context.getResources().openRawResource(R.raw.search_source);
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                List<QuerySource> sourceList = new ArrayList<>();
                JSONArray array = JSON.parseArray(sb.toString());
                for (Object object : array) {
                    QuerySource querySource = new QuerySource();
                    querySource.setText(String.valueOf(object));
                    sourceList.add(querySource);
                }
                daoSession.getQuerySourceDao().insertInTx(sourceList, true);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                    if (reader != null) reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
