package com.coderpage.codelab.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.coderpage.codelab.LabApplication;
import com.coderpage.codelab.framework.Model;
import com.coderpage.codelab.framework.QueryEnum;
import com.coderpage.codelab.framework.UserActionEnum;
import com.coderpage.codelab.model.DaoSession;
import com.coderpage.codelab.model.QueryHistory;
import com.coderpage.codelab.model.QueryHistoryDao;
import com.coderpage.codelab.model.QuerySource;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lc. 2017-08-19
 * @since 0.1.0
 */

class SearchModel implements Model<SearchModel.SearchQueryEnum,
        SearchModel.SearchUserActionEnum> {

    static final String EXTRA_QUERY = "extra_query";
    static final String EXTRA_ID = "extra_id";

    private Activity activity;
    private DaoSession daoSession;
    private List<QueryHistory> queryHistories;
    private List<QuerySource> queryResultList;

    SearchModel(Activity activity) {
        queryHistories = new ArrayList<>();
        queryResultList = new ArrayList<>();
        this.activity = activity;
        this.daoSession = ((LabApplication) activity.getApplication()).getDaoSession();
        SearchSourceFakeHelper.initIfNeed(activity.getApplicationContext(), daoSession);
    }

    public List<QueryHistory> getQueryHistories() {
        return queryHistories;
    }

    public List<QuerySource> getQueryResultList() {
        return queryResultList;
    }

    @Override
    public SearchQueryEnum[] getQueries() {
        return SearchQueryEnum.values();
    }

    @Override
    public SearchUserActionEnum[] getUserActions() {
        return SearchUserActionEnum.values();
    }

    @Override
    public void requestData(SearchQueryEnum query, DataQueryCallback callback) {
        switch (query) {
            case SHOW_HISTORY:
                QueryHistoryDao queryHistoryDao = daoSession.getQueryHistoryDao();
                Query<QueryHistory> historyQuery = queryHistoryDao.queryBuilder()
                        .orderDesc(QueryHistoryDao.Properties.Date).build();
                queryHistories.clear();
                queryHistories.addAll(historyQuery.list());
                break;
        }
    }

    @Override
    public void deliverUserAction(SearchUserActionEnum action,
                                  @Nullable Bundle args,
                                  UserActionCallback callback) {
        switch (action) {
            case QUERY:
                if (args == null || !args.containsKey(EXTRA_QUERY)) {
                    throw new IllegalArgumentException("miss extra value query");
                }
                String query = args.getString(EXTRA_QUERY);
                List<QuerySource> sourceList = daoSession.getQuerySourceDao()
                        .queryRaw(" where text like '%" + query + "%'");
                queryResultList.clear();
                queryResultList.addAll(sourceList);
                callback.onModelUpdated(this, action);
                break;
            case RELOAD_HISTORY:
                QueryHistoryDao queryHistoryDao = daoSession.getQueryHistoryDao();
                Query<QueryHistory> historyQuery = queryHistoryDao.queryBuilder()
                        .orderDesc(QueryHistoryDao.Properties.Date).build();
                queryHistories.clear();
                queryHistories.addAll(historyQuery.list());
                break;
            case ADD_HISTORY:
                if (args == null || !args.containsKey(EXTRA_QUERY)) {
                    throw new IllegalArgumentException("miss extra value query");
                }
                query = args.getString(EXTRA_QUERY);
                QueryHistory history = new QueryHistory(null, query,
                        new Date(System.currentTimeMillis()));
                daoSession.getQueryHistoryDao().insertOrReplace(history);
                callback.onModelUpdated(this, action);
                break;
            case DEL_HISTORY:
                if (args == null || !args.containsKey(EXTRA_ID)) {
                    throw new IllegalArgumentException("miss extra value id");
                }
                long id = args.getLong(EXTRA_ID);
                daoSession.getQueryHistoryDao().deleteByKey(id);
                callback.onModelUpdated(this, action);
                break;
        }
    }

    @Override
    public void cleanUp() {
        queryHistories.clear();
        queryResultList.clear();
    }

    enum SearchQueryEnum implements QueryEnum {
        SHOW_HISTORY(1, null);

        private int id;
        private String[] projection;

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String[] getProjection() {
            return projection;
        }

        SearchQueryEnum(int id, String[] projection) {
            this.id = id;
            this.projection = projection;
        }
    }

    enum SearchUserActionEnum implements UserActionEnum {
        QUERY(1),
        RELOAD_HISTORY(2),
        ADD_HISTORY(3),
        DEL_HISTORY(4);

        private int id;

        @Override
        public int getId() {
            return id;
        }

        SearchUserActionEnum(int id) {
            this.id = id;
        }
    }

}
