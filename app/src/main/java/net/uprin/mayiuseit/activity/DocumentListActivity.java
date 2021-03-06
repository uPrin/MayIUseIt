package net.uprin.mayiuseit.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import net.uprin.mayiuseit.R;
import net.uprin.mayiuseit.rest.ApiClient;
import net.uprin.mayiuseit.rest.ApiError;
import net.uprin.mayiuseit.rest.ApiInterface;
import net.uprin.mayiuseit.model.DocumentList;
import net.uprin.mayiuseit.model.DocumentListResponse;
import net.uprin.mayiuseit.adapter.DocumentListsAdapter;
import net.uprin.mayiuseit.util.TokenManager;
import net.uprin.mayiuseit.util.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentListActivity extends AppCompatActivity {

    private static final String TAG = DocumentListActivity.class.getSimpleName();


    private  int pageNum = 1;
    private  int category= 0;
    private String rankBy = "rgsde";
    RecyclerView recyclerView;
    List<DocumentList> documentLists;
    DocumentListsAdapter adapter;
    ApiInterface api;
    Context context;
    TokenManager tokenManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_list);
        initToolbar();

        Intent intent = getIntent();
        category = intent.getExtras().getInt("category");
        rankBy = intent.getExtras().getString("rankBy");
        context = this;
        recyclerView = (RecyclerView) findViewById(R.id.documents_recycler_view);
        documentLists = new ArrayList<>();

        adapter = new DocumentListsAdapter(this, documentLists);
        adapter.setLoadMoreListener(new DocumentListsAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        loadMore(pageNum);
                    }
                });
                //Calling loadMore function in Runnable to fix the
                // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling error
            }
        });
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //recyclerView.addItemDecoration(new VerticalLineDecorator(2));
        recyclerView.setAdapter(adapter);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if(tokenManager.getToken() == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        api = ApiClient.createServiceWithAuth(ApiInterface.class, tokenManager);
        load(pageNum);
    }

    private void load(int index){


        Call<DocumentListResponse> call = api.getDocumentList(index, category,rankBy);
        call.enqueue(new Callback<DocumentListResponse>() {
            @Override
            public void onResponse(Call<DocumentListResponse> call, Response<DocumentListResponse> response) {
                if(response.isSuccessful()){
                    documentLists.addAll(response.body().getResults());
                    adapter.notifyDataChanged();
                    pageNum = pageNum +1;
                } else {
                    ApiError apiError = Utils.converErrors(response.errorBody());
                    Snackbar.make(getWindow().getDecorView().getRootView(), apiError.message(), Snackbar.LENGTH_SHORT).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                    // Toast.makeText(getApplicationContext(), response.errorBody()., Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<DocumentListResponse> call, Throwable t) {
                Log.e(TAG," Response Error "+t.getMessage());
            }
        });
    }

    private void loadMore(int index){

        //add loading progress view
        documentLists.add(new DocumentList(999));
        adapter.notifyItemInserted(documentLists.size()-1);

        Call<DocumentListResponse> call = api.getDocumentList(index, category,rankBy);
        call.enqueue(new Callback<DocumentListResponse>() {
            @Override
            public void onResponse(Call<DocumentListResponse> call, Response<DocumentListResponse> response) {
                if(response.isSuccessful()){

                    //remove loading view
                    documentLists.remove(documentLists.size()-1);

                    List<DocumentList> result = response.body().getResults();
                    if(result!=null){
                        Log.e(TAG,"Result is " + result.size());
                        //add loaded data
                        documentLists.addAll(result);
                        pageNum =response.body().getPage()+1;
                    }else{//result size 0 means there is no more data available at server
                        adapter.setMoreDataAvailable(false);
                        //telling adapter to stop calling load more as no more server data available
                        Snackbar.make(getWindow().getDecorView().getRootView(), "마지막 페이지입니다", Snackbar.LENGTH_SHORT).setAction("확인", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).show();
                    }
                    adapter.notifyDataChanged();
                    //should call the custom method adapter.notifyDataChanged here to get the correct loading status
                }else{
                    Log.e(TAG," Load More Response Error "+String.valueOf(response.code()));
                    documentLists.remove(documentLists.size()-1);
                    adapter.setMoreDataAvailable(false);
                    adapter.notifyDataChanged();
                    Snackbar.make(getWindow().getDecorView().getRootView(), "마지막 페이지입니다", Snackbar.LENGTH_SHORT).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                }
            }

            @Override
            public void onFailure(Call<DocumentListResponse> call, Throwable t) {
                Log.e(TAG," Load More Response Error "+t.getMessage());
                documentLists.remove(documentLists.size()-1);
                adapter.setMoreDataAvailable(false);
                adapter.notifyDataChanged();
            }
        });
    }
    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle("뒤로가기");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //mToolbar.setLogo(R.drawable.toolbar_logo);
        // ↓툴바에 홈버튼을 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
