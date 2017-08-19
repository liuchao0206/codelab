package com.coderpage.codelab.search;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.coderpage.codelab.codelab.R;
import com.coderpage.codelab.framework.Presenter;
import com.coderpage.codelab.framework.PresenterImpl;
import com.coderpage.codelab.framework.UpdatableView;
import com.coderpage.codelab.model.QueryHistory;
import com.coderpage.codelab.model.QuerySource;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.coderpage.codelab.utils.LogUtils.LOGE;
import static com.coderpage.codelab.utils.LogUtils.makeLogTag;


public class SearchActivity extends AppCompatActivity
        implements UpdatableView<SearchModel,
        SearchModel.SearchQueryEnum, SearchModel.SearchUserActionEnum> {

    private static final String TAG = makeLogTag(SearchActivity.class);

    @BindView(R.id.search_view)
    SearchView mSearchView;
    @BindView(R.id.lvSearchResult)
    ListView mSearchResult;

    ResultAdapter mResultAdapter;

    String mQuery;
    SearchModel mModel;
    Presenter mPresenter;
    UserActionListener mUserActionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        overridePendingTransition(0, 0);

        setupSearchView();

        mResultAdapter = new ResultAdapter();
        mSearchResult.setAdapter(mResultAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        Drawable up = DrawableCompat.wrap(ContextCompat.getDrawable(this, R.drawable.ic_up));
        DrawableCompat.setTint(up, getResources().getColor(R.color.colorAction));
        toolbar.setNavigationIcon(up);
        toolbar.setNavigationOnClickListener((v) -> {
            dismiss(null);
        });

        mModel = new SearchModel(this);
        mPresenter = new PresenterImpl(mModel, this,
                SearchModel.SearchUserActionEnum.values(),
                SearchModel.SearchQueryEnum.values());
        mPresenter.loadInitialQueries();

        String query = getIntent().getStringExtra(SearchManager.QUERY);
        mQuery = query == null ? "" : query;
        searchFor(query);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            doEnterAnim();
        }
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        showQueryHistory();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(SearchManager.QUERY)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (TextUtils.isEmpty(query)) {
                mUserActionListener.onUserAction(
                        SearchModel.SearchUserActionEnum.RELOAD_HISTORY, null);
            } else {
                searchFor(query);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mModel.cleanUp();
    }

    @Override
    public void displayErrorMessage(SearchModel.SearchQueryEnum query) {

    }

    @Override
    public void displayData(SearchModel model, SearchModel.SearchQueryEnum query) {
        switch (query) {
            case SHOW_HISTORY:
                StringBuilder sb = new StringBuilder();
                for (QueryHistory queryHistory : model.getQueryHistories()) {
                    sb.append(queryHistory.getQuery()).append(',');
                }
                LOGE(TAG, "history list:" + sb.toString());
                break;
        }
    }

    @Override
    public void displayUserActionResult(SearchModel model,
                                        Bundle args,
                                        SearchModel.SearchUserActionEnum userAction,
                                        boolean success) {
        switch (userAction) {
            case RELOAD_HISTORY:
                StringBuilder sb = new StringBuilder();
                for (QueryHistory queryHistory : model.getQueryHistories()) {
                    sb.append(queryHistory.getQuery()).append(',');
                }
                LOGE(TAG, "history list:" + sb.toString());
                break;
            case ADD_HISTORY:
                break;
            case DEL_HISTORY:
                break;
            case QUERY:
                sb = new StringBuilder();
                for (QuerySource querySource : model.getQueryResultList()) {
                    sb.append(querySource.getText()).append('\n').append("-------").append('\n');
                }
                LOGE(TAG, "result list:" + sb.toString());
                if (success) {
                    mResultAdapter.refresh(model.getQueryResultList());
                }
                break;
        }
    }

    @Override
    public void addListener(UserActionListener listener) {
        mUserActionListener = listener;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Uri getDataUri(SearchModel.SearchQueryEnum query) {
        return null;
    }

    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconified(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchFor(newText);
                return true;
            }
        });
        mSearchView.setOnCloseListener(() -> {
            dismiss(null);
            return false;
        });
    }

    private void showQueryHistory() {

    }

    private void searchFor(String query) {
        if (!TextUtils.isEmpty(query)) {
            Bundle bundle = new Bundle();
            bundle.putString(SearchModel.EXTRA_QUERY, query);
            mUserActionListener.onUserAction(
                    SearchModel.SearchUserActionEnum.QUERY, bundle);
            mUserActionListener.onUserAction(
                    SearchModel.SearchUserActionEnum.ADD_HISTORY, bundle);
        }
    }

    public void dismiss(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            doExitAnim();
        } else {
            ActivityCompat.finishAfterTransition(this);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void doEnterAnim() {
        // Fade in a background scrim as this is a floating window. We could have used a
        // translucent window background but this approach allows us to turn off window animation &
        // overlap the fade with the reveal animation – making it feel snappier.
        View scrim = findViewById(R.id.scrim);
        scrim.animate()
                .alpha(1f)
                .setDuration(500L)
                .setInterpolator(
                        AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in))
                .start();

        // Next perform the circular reveal on the search panel
        final View searchPanel = findViewById(R.id.searchPanel);
        if (searchPanel != null) {
            // We use a view tree observer to set this up once the view is measured & laid out
            searchPanel.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            searchPanel.getViewTreeObserver().removeOnPreDrawListener(this);
                            // As the height will change once the initial suggestions are delivered by the
                            // loader, we can't use the search panels height to calculate the final radius
                            // so we fall back to it's parent to be safe
                            int revealRadius = ((ViewGroup) searchPanel.getParent()).getHeight();
                            // Center the animation on the top right of the panel i.e. near to the
                            // search button which launched this screen.
                            Animator show = ViewAnimationUtils.createCircularReveal(searchPanel,
                                    searchPanel.getRight(), searchPanel.getTop(), 0f, revealRadius);
                            show.setDuration(250L);
                            show.setInterpolator(AnimationUtils.loadInterpolator(SearchActivity.this,
                                    android.R.interpolator.fast_out_slow_in));
                            show.start();
                            return false;
                        }
                    });
        }
    }

    /**
     * On Lollipop+ perform a circular animation (a contracting circular mask) when hiding the
     * search panel.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void doExitAnim() {
        final View searchPanel = findViewById(R.id.searchPanel);
        // Center the animation on the top right of the panel i.e. near to the search button which
        // launched this screen. The starting radius therefore is the diagonal distance from the top
        // right to the bottom left
        int revealRadius = (int) Math.sqrt(Math.pow(searchPanel.getWidth(), 2)
                + Math.pow(searchPanel.getHeight(), 2));
        // Animating the radius to 0 produces the contracting effect
        Animator shrink = ViewAnimationUtils.createCircularReveal(searchPanel,
                searchPanel.getRight(), searchPanel.getTop(), revealRadius, 0f);
        shrink.setDuration(200L);
        shrink.setInterpolator(AnimationUtils.loadInterpolator(SearchActivity.this,
                android.R.interpolator.fast_out_slow_in));
        shrink.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                searchPanel.setVisibility(View.INVISIBLE);
                ActivityCompat.finishAfterTransition(SearchActivity.this);
            }
        });
        shrink.start();

        // We also animate out the translucent background at the same time.
        findViewById(R.id.scrim).animate()
                .alpha(0f)
                .setDuration(200L)
                .setInterpolator(
                        AnimationUtils.loadInterpolator(SearchActivity.this,
                                android.R.interpolator.fast_out_slow_in))
                .start();
    }

    class ResultAdapter extends BaseAdapter {

        private List<QuerySource> mResults;
        private LayoutInflater mInflater;

        ResultAdapter() {
            mInflater = getLayoutInflater();
            mResults = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return mResults.size();
        }

        @Override
        public Object getItem(int position) {
            return mResults.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(
                        R.layout.recycler_item_query_result, parent, false);
            }
            QuerySource querySource = mResults.get(position);
            TextView textView = (TextView) convertView.findViewById(R.id.searchResult);
            textView.setText(querySource.getText());
            return convertView;
        }

        void refresh(List<QuerySource> list) {
            if (list.size() > 0) {
                mSearchResult.setVisibility(View.VISIBLE);
            } else {
                mSearchResult.setVisibility(View.GONE);
            }
            mResults.clear();
            mResults.addAll(list);
            notifyDataSetChanged();
        }
    }
}
