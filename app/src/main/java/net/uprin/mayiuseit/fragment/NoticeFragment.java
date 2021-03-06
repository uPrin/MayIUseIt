package net.uprin.mayiuseit.fragment;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import net.uprin.mayiuseit.R;
import net.uprin.mayiuseit.util.VerticalLineDecorator;
import net.uprin.mayiuseit.rest.ApiClient;
import net.uprin.mayiuseit.rest.ApiInterface;
import net.uprin.mayiuseit.model.NoticeList;
import net.uprin.mayiuseit.model.NoticeListResponse;
import net.uprin.mayiuseit.adapter.NoticeListsAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by CJS on 2017-11-26.
 */

public class NoticeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = NoticeFragment.class.getSimpleName();
    private final int TOP_REFRESH = 1;
    private final int BOTTOM_REFRESH = 2;

    private SwipeRefreshLayout refreshLayout;
    private  int pageNum = 1;
    RecyclerView recyclerView;
    List<NoticeList> noticeLists;
    NoticeListsAdapter adapter;
    ApiInterface api;
    Context context;


    public static NoticeFragment createInstance() {
        NoticeFragment noticeFragmentFragment = new NoticeFragment();
        Bundle bundle = new Bundle();
        return noticeFragmentFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_notice_list, container, false);

        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLo);
        refreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        refreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) v.findViewById(R.id.notices_recycler_view);
        noticeLists = new ArrayList<>();

        adapter = new NoticeListsAdapter(this.getActivity(), noticeLists);
        adapter.setLoadMoreListener(new NoticeListsAdapter.OnLoadMoreListener() {
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new VerticalLineDecorator(2));
        recyclerView.setAdapter(adapter);

        api = ApiClient.createService(ApiInterface.class);
        load(pageNum);

        return v;

    }

    private void load(int index){
        Call<NoticeListResponse> call = api.getNotice(index);
        call.enqueue(new Callback<NoticeListResponse>() {
            @Override
            public void onResponse(Call<NoticeListResponse> call, Response<NoticeListResponse> response) {
                if(response.isSuccessful()){
                    noticeLists.addAll(response.body().getResults());
                    adapter.notifyDataChanged();
                    pageNum = pageNum +1;
                }else{
                    Log.e(TAG," Response Error "+String.valueOf(response.code()));
                }
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<NoticeListResponse> call, Throwable t) {
                Log.e(TAG," Response Error "+t.getMessage());
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadMore(int index){

        //add loading progress view
        noticeLists.add(new NoticeList(true));
        adapter.notifyItemInserted(noticeLists.size()-1);

        Call<NoticeListResponse> call = api.getNotice(index);
        call.enqueue(new Callback<NoticeListResponse>() {
            @Override
            public void onResponse(Call<NoticeListResponse> call, Response<NoticeListResponse> response) {
                if(response.isSuccessful()){

                    //remove loading view
                    noticeLists.remove(noticeLists.size()-1);

                    List<NoticeList> result = response.body().getResults();
                    if(result!=null){
                        Log.e(TAG,"Result is " + result.size());
                        //add loaded data
                        noticeLists.addAll(result);
                        pageNum =response.body().getPage()+1;
                    }else{//result size 0 means there is no more data available at server
                        adapter.setMoreDataAvailable(false);
                        //telling adapter to stop calling load more as no more server data available
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "마지막 페이지입니다", Snackbar.LENGTH_SHORT).setAction("확인", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).show();
                    }
                    adapter.notifyDataChanged();
                    //should call the custom method adapter.notifyDataChanged here to get the correct loading status
                }else{
                    Log.e(TAG," Load More Response Error "+String.valueOf(response.code()));
                }
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<NoticeListResponse> call, Throwable t) {
                Log.e(TAG," Load More Response Error "+t.getMessage());
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
                noticeLists.clear();
                pageNum=1;
                //load(pageNum);
                break;
        }
        // adapter.notifyDataSetChanged();

    }

}
