package com.thunder.nytsearch.retrofit;

import com.thunder.nytsearch.models.ArticleSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by anlinsquall on 21/3/17.
 */

public interface NYTArticleSearchApiEndPoint {
    @GET("/svc/search/v2/articlesearch.json")
    Call<ArticleSearchResponse> getArticles(
            @Query("api-key") String apiKey,
            @Query("page") int page,
            @Query("q") String query,
            @Query("begin_date") String beginDate,
            @Query("sort") String sort,
            @Query("fq") String fq);
}
