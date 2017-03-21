package com.thunder.nytsearch.activities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.thunder.nytsearch.adapters.EndlessRecyclerViewScrollListener;
import com.thunder.nytsearch.adapters.EndlessScrollListener;
import com.thunder.nytsearch.adapters.ItemClickSupport;
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

    RecyclerView rvArticles;

    Filter currentFilter;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    String mQuery;

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
        rvArticles = (RecyclerView) findViewById(R.id.rvArticles);
        articles = new ArrayList<Article>();
        adapter = new ArticleArrayAdapter(this, articles);
        rvArticles.setAdapter(adapter);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        rvArticles.setItemAnimator(new DefaultItemAnimator());
        rvArticles.setLayoutManager(gridLayoutManager);

        // hook up listener for click
        ItemClickSupport.addTo(rvArticles).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // get the article to display
                Article article = articles.get(position);

                Bitmap bitmap = BitmapFactory.decodeResource(getResources()
                        , R.drawable.ic_action_share);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, article.getWebUrl());

                int requestCode = 100;
                PendingIntent pendingIntent = PendingIntent.getActivity(SearchActivity.this,
                        requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
                String url = article.getWebUrl();
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                // set toolbar color and/or setting custom actions before invoking build()
                builder.setToolbarColor(ContextCompat.getColor(SearchActivity.this, R.color.colorPrimary));
                // share button
                builder.addDefaultShareMenuItem();
                builder.setActionButton(bitmap, "Share Link", pendingIntent, true);
                // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
                CustomTabsIntent customTabsIntent = builder.build();
                // and launch the desired Url with CustomTabsIntent.launchUrl()
                customTabsIntent.launchUrl(SearchActivity.this, Uri.parse(url));
            }
        });

        rvArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
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
                mQuery = query;
                articles.clear();
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
        params.put("q", mQuery);

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
                    articles.addAll(Article.fromJsonArray(articleJsonResults));
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
