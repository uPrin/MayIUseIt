package net.uprin.mayiuseit.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.uprin.mayiuseit.R;
import net.uprin.mayiuseit.activity.DocumentListActivity;
import net.uprin.mayiuseit.activity.LoginActivity;
import net.uprin.mayiuseit.adapter.DocumentListsAdapter;
import net.uprin.mayiuseit.model.DocumentList;
import net.uprin.mayiuseit.model.DocumentListResponse;
import net.uprin.mayiuseit.model.NoticeList;
import net.uprin.mayiuseit.rest.ApiClient;
import net.uprin.mayiuseit.rest.ApiError;
import net.uprin.mayiuseit.rest.ApiInterface;
import net.uprin.mayiuseit.util.TokenManager;
import net.uprin.mayiuseit.util.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = DocumentListActivity.class.getSimpleName();
    private final int TOP_REFRESH = 1;

    private SwipeRefreshLayout refreshLayout;
    private LinearLayout oops_layout;
    private  int pageNum = 1;
    private  int category= 0;
    private String rankBy = "rgsde";
    RecyclerView recyclerView;
    List<DocumentList> documentLists;
    DocumentListsAdapter adapter;
    ApiInterface api;
    Context context;
    TokenManager tokenManager;

    public final static String ITEMS_COUNT_KEY = "MainActivityFragment$ItemsCount";

    public static HomeFragment createInstance() {
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        return homeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.home_swipeRefreshLo);
        oops_layout = (LinearLayout) v.findViewById(R.id.oops_layout);

        refreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        refreshLayout.setOnRefreshListener(this);

        context = getContext();
        recyclerView = (RecyclerView) v.findViewById(R.id.subscribe_recycler_view);
        documentLists = new ArrayList<>();

        adapter = new DocumentListsAdapter(context, documentLists);
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

        tokenManager = TokenManager.getInstance(this.getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE));

        if(tokenManager.getToken() == null){
            startActivity(new Intent(new Intent(super.getContext().getApplicationContext(), LoginActivity.class)));
            ((Activity)getContext()).finish();
        }
        api = ApiClient.createServiceWithAuth(ApiInterface.class, tokenManager);
        load(pageNum);

        return v;
    }

    private void load(int index){


        Call<DocumentListResponse> call = api.getSubcribeList(index);
        call.enqueue(new Callback<DocumentListResponse>() {
            @Override
            public void onResponse(Call<DocumentListResponse> call, Response<DocumentListResponse> response) {
                if(response.isSuccessful()){
                    oops_layout.setVisibility(View.GONE);
                    documentLists.addAll(response.body().getResults());
                    adapter.notifyDataChanged();
                    pageNum = pageNum +1;
                } else {
//                    oops_layout.setVisibility(View.VISIBLE);
                    ApiError apiError = Utils.converErrors(response.errorBody());
//                    Snackbar.make(getWindow().getDecorView().getRootView(), apiError.message(), Snackbar.LENGTH_SHORT).setAction("확인", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//
//                        }
//                    }).show();
                    // Toast.makeText(getApplicationContext(), response.errorBody()., Toast.LENGTH_LONG).show();

                }
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<DocumentListResponse> call, Throwable t) {
                Log.e(TAG," Response Error "+t.getMessage());
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadMore(int index){

        //add loading progress view
        documentLists.add(new DocumentList(999));
        adapter.notifyItemInserted(documentLists.size()-1);



        Call<DocumentListResponse> call = api.getSubcribeList(index);
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
//                        Snackbar.make(getWindow().getDecorView().getRootView(), "마지막 페이지입니다", Snackbar.LENGTH_SHORT).setAction("확인", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//
//                            }
//                        }).show();
                    }

                    adapter.notifyDataChanged();
                    //should call the custom method adapter.notifyDataChanged here to get the correct loading status
                }else{
                    Log.e(TAG," Load More Response Error "+String.valueOf(response.code()));
                    documentLists.remove(documentLists.size()-1);
                    adapter.setMoreDataAvailable(false);
                    adapter.notifyDataChanged();
//                    Snackbar.make(getWindow().getDecorView().getRootView(), "마지막 페이지입니다", Snackbar.LENGTH_SHORT).setAction("확인", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//
//                        }
//                    }).show();
                }
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<DocumentListResponse> call, Throwable t) {
                Log.e(TAG," Load More Response Error "+t.getMessage());
                documentLists.remove(documentLists.size()-1);
                adapter.setMoreDataAvailable(false);
                adapter.notifyDataChanged();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        dataOption(TOP_REFRESH);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
        Toast.makeText(getContext(),"로딩완료",Toast.LENGTH_SHORT).show();

    }

    private void dataOption(int option){
        switch (option) {
            case TOP_REFRESH:
                documentLists.clear();
                pageNum=1;
                //load(pageNum);
                break;
        }
        // adapter.notifyDataSetChanged();

    }
}
