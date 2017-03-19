package com.thunder.nytsearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thunder.nytsearch.R;
import com.thunder.nytsearch.adapters.ArticleArrayAdapter;
import com.thunder.nytsearch.adapters.EndlessScrollListener;
import com.thunder.nytsearch.fragments.FilterDialogFragment;
import com.thunder.nytsearch.models.Article;
import com.thunder.nytsearch.models.Filter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements FilterDialogFragment.FilterDialogListener {

    GridView gvResults;

    Filter currentFilter;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(currentFilter == null){
            currentFilter = new Filter();
        }

        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupView();
    }

    private void setupView() {
        gvResults = (GridView) findViewById(R.id.gvResults);
        articles = new ArrayList<Article>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        // hook up listner for grid click
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ArticleActiviy.class);
                // get the article to display
                Article article = articles.get(position);

                intent.putExtra("url", article.getWebUrl());

                startActivity(intent);
            }
        });

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            protected boolean onLoadMore(int page, int totalItemCount) {
                loadNextDataFromApi(page);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onArticleSearch(0);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.miFilter) {
            showFilterDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public  void showFilterDialog(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FilterDialogFragment filterDialogFragment = FilterDialogFragment.newInstance("Filter");
        filterDialogFragment.show(fragmentManager, "");
    }

    public void onArticleSearch(int offset) {
        if(!isNetworkAvailable()||!isOnline()){
            Toast.makeText(this, "The device is not online.", Toast.LENGTH_LONG).show();
            return;
        }

        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("api-key","YOUR_API_KEY");
        params.put("page", offset);
        params.put("q", query);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmdd");

        List<String> newsDeskArray = new ArrayList<String>();

        // fq=news_deck:("" "")
        if(currentFilter!=null){
            if(currentFilter.beginDate!=null)
                params.put("begin_date",simpleDateFormat.format(currentFilter.beginDate));
            if(currentFilter.sortOrderBy!=null)
                params.put("sort",currentFilter.sortOrderBy);
            if(currentFilter.isSports || currentFilter.isFashionSytles || currentFilter.isArts) {
                if (currentFilter.isArts)
                    newsDeskArray.add("\"Arts\"");
                if (currentFilter.isFashionSytles)
                    newsDeskArray.add("\"Fashion & Style\"");
                if (currentFilter.isSports)
                    newsDeskArray.add("\"Sports\"");
                params.put("fq", String.format("\"news_deck:(%s)\"", android.text.TextUtils.join(" ",newsDeskArray )));
            }
        }

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;
                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    adapter.addAll(Article.fromJsonArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("DEBUG", articles.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    @Override
    public void onSaveFilterDialog(Filter filter) {
        currentFilter = filter;
    }

    public void loadNextDataFromApi(int offset){
        onArticleSearch(offset);
    }

    private Boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo !=null && networkInfo.isConnectedOrConnecting();

    }

    private boolean isOnline(){
        Runtime runtime = Runtime.getRuntime();
        Process ipProcess = null;
        try {
            ipProcess = runtime.exec("system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue==0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
