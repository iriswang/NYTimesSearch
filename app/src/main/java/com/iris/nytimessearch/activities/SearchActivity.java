package com.iris.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.iris.nytimessearch.listeners.EndlessScrollListener;
import com.iris.nytimessearch.models.Article;
import com.iris.nytimessearch.adapters.ArticleArrayAdapter;
import com.iris.nytimessearch.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import static java.util.Arrays.asList;


import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    EditText etQuery;
    GridView gvResults;
    Button btnSearch;

    ArrayList<Article> articles;
    ArticleArrayAdapter articleAdapter;

    private final String API_KEY = "51ba1c50281b474d82ab5c30973ddb60";
    private final String URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

    // Constants for sort order
    public static final String QUERY_PARAM_SORT = "sort";
    public static final String SORT_ORDER_OLDEST = "oldest";
    public static final String SORT_ORDER_NEWEST = "newest";
    public static final List<String> SORT_ORDERS = asList(SORT_ORDER_NEWEST, SORT_ORDER_OLDEST);

    // Query Params
    public static final String QUERY_PARAM_BEGIN_DATE = "begin_date";

    //Query Params
    public  static final String NEWS_DESK_ARG = "fq";
    private static final String NEWS_DESK_FORMAT = "news_desk:(%s)";
    public static final String NEWS_DESK_ART = "Arts";
    public static final String NEWS_DESK_OPINION = "Opinion";
    public static final String NEWS_DESK_WORLD = "World";
    private List<String> newsDeskParams = asList();

    private String sortSetting = SORT_ORDER_NEWEST;
    private Calendar beginDateParam = null;

    private String searchTerm = null;

    public static final int SAVE_REQUEST_CODE = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpViews();
    }

    public void setUpViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        gvResults = (GridView) findViewById(R.id.gvResults);
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                loadArticles(page);
                return true;
            }
        });

        articles = new ArrayList<Article>();
        articleAdapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(articleAdapter);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = articles.get(position);
                i.putExtra("article", Parcels.wrap(article));
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_filter) {
            Intent i = new Intent(getApplicationContext(), FilterActivity.class);
            i.putExtra(SearchActivity.QUERY_PARAM_SORT, sortSetting);
            i.putExtra(SearchActivity.QUERY_PARAM_BEGIN_DATE, beginDateParam);
            i.putExtra(SearchActivity.NEWS_DESK_ARG, (Serializable) newsDeskParams);
            startActivityForResult(i, SAVE_REQUEST_CODE);
           return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SAVE_REQUEST_CODE) {
            handleSaveSettingResult(data, resultCode);
        }
    }

    private void handleSaveSettingResult(Intent data, int resultCode) {
        if (resultCode == RESULT_OK) {
            sortSetting = data.getStringExtra(QUERY_PARAM_SORT);
            beginDateParam = (Calendar) data.getSerializableExtra(QUERY_PARAM_BEGIN_DATE);
            newsDeskParams = (List<String>) data.getSerializableExtra(NEWS_DESK_ARG);
            if (searchTerm != null) {
                articleAdapter.clear();
                loadArticles(0);
            }
        }
   }

    private RequestParams setUpQueryParams(int page) {
        RequestParams params = new RequestParams();
        params.put("api-key", API_KEY);
        params.put("page", page);
        params.put("q", searchTerm);
        params.put("sort", sortSetting);
        if (beginDateParam != null) {
            SimpleDateFormat beginDateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = beginDateParam.getTime();
            String beginDateStr = beginDateFormat.format(date);
            params.put(QUERY_PARAM_BEGIN_DATE, beginDateStr);
        }
        if (! newsDeskParams.isEmpty()) {
            params.put(NEWS_DESK_ARG, convertToLucene(newsDeskParams));
        }
        return params;
    }

    private String convertToLucene(List<String> newsDeskValues) {
        String newsDeskStr = "", delimiter="";
        for (String newsDeskVal: newsDeskValues) {
            newsDeskStr += delimiter + "\"" + newsDeskVal + "\"";
            delimiter = " ";
        }
        return String.format(NEWS_DESK_FORMAT, newsDeskStr);
    }

    public void onArticleSearch(View view) {
        String query = etQuery.getText().toString();
        if (searchTerm == null || query != searchTerm) {
            searchTerm = query;
            articleAdapter.clear();
        }
        loadArticles(0);
    }

    public void loadArticles(int page) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = setUpQueryParams(page);

        client.get(URL, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray articleJsonResults =
                        response.getJSONObject("response").getJSONArray("docs");
                    articleAdapter.addAll(Article.fromJsonArray(articleJsonResults));
                } catch (JSONException e) {

                }
            }
        });
    }

}
