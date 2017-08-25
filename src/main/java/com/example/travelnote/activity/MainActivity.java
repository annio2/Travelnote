package com.example.travelnote.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.travelnote.R;
import com.example.travelnote.adapter.RecyclerPullMoreAdapter;
import com.example.travelnote.utils.JsonUtils;
import com.example.travelnote.bean.Response;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String HOST = "http://api.lvxingx.com";
    private RecyclerView recyclerView;
    private List<Response> dataList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerPullMoreAdapter adapter;
    private LinearLayoutManager layoutManager;
    private boolean isLoading = false;
    private int lastVisiablePosition;
    private int adType;
    private int pageNo;
    private int totalPage;
    private RelativeLayout relativeLayout;
    private LinearLayout titleLinear;
    private int scrollYDistance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        setPullListener();
        setLoadMoreListener();

    }

    void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerPullMore);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        /*设置过度页的gif*/
        relativeLayout = (RelativeLayout) findViewById(R.id.rl);
        GifImageView gitView = (GifImageView) findViewById(R.id.iv_base_progress_icon);
        gitView.setImageResource(R.mipmap.gif_heart_progress);
        titleLinear = (LinearLayout) findViewById(R.id.titleBar);
    }

    void initData() {
        adType = 1;
        pageNo = 1;
        new FirstGetDataTask().execute(String.valueOf(adType), String.valueOf(pageNo));
    }

    //显示系统内存以及APP内存
    private void displayBriefMemoey(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        Log.e("APP内存", "displayBriefMemoey: " + activityManager.getMemoryClass());
        Log.e("系统剩余内存", "displayBriefMemoey: " + info.availMem);
        Log.e("系统是否处于低内存运行", "displayBriefMemoey: " + info.lowMemory);
        Log.e("系统低内存线", "displayBriefMemoey: "+ info.threshold );
    }

    //第一次加载数据的异步任务
    class FirstGetDataTask extends AsyncTask<String, Void, List<Response>> {

        @Override
        protected List<Response> doInBackground(String... strings) {
            String adTypeString = strings[0];
            String pageNoString = strings[1];
            String url = getUrl(adTypeString, pageNoString);
            List<Response> list = JsonUtils.getTravelResponse(url);
            return list;
        }

        @Override
        protected void onPostExecute(List<Response> list) {
            super.onPostExecute(list);
            if (list != null) {
                totalPage = list.get(0).getTotalPage();
                dataList = list;
                relativeLayout.setVisibility(View.GONE);
                setAdapter(MainActivity.this, dataList);
            }

        }
    }

    void setAdapter(Context context, List<Response> list) {
        adapter = new RecyclerPullMoreAdapter(context, list);
        adapter.setOnItemClickListener(new RecyclerPullMoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String url) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    /*设置下拉刷新*/
    void setPullListener() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new PullMoreTask().execute();
            }
        });
    }

    /*设置上拉加载更多*/
    void setLoadMoreListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "onScrollStateChanged: " + newState);
                //判断RecyclerView当前滑动状态与最后一个可见位置，SCROLL_STATE_IDLE为停止滚动,SCROLL_STATE_DRAGGING为正在被拖拽
                //SCROLL_STATE_SETTLING为自动滚动开始
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiablePosition == adapter.getItemCount() - 1) {
                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    //如果正在下拉刷新，则取消加载，去掉recyclerview的底部
                    if (isRefreshing) {
                        adapter.notifyItemRemoved(adapter.getItemCount() - 1);
                        return;
                    }
                    //当前没有在加载过程中才能进行加载
                    if (!isLoading) {
                        isLoading = true;
                        pageNo ++;
                        if (pageNo <= totalPage){
                            new LoadMoreTask().execute(String.valueOf(adType), String.valueOf(pageNo));
                            Log.d(TAG, "onScrolled: Load完成");
                        }else {
                            Toast.makeText(MainActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
                            adapter.notifyItemRemoved(adapter.getItemCount() - 1);
                        }
                        isLoading = false;
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled: ");
                lastVisiablePosition = layoutManager.findLastVisibleItemPosition();
                /*设置标题栏透明度随滚动渐变*/
                scrollYDistance += dy;//累计竖直滚动距离，一旦停止dy为0；向上滚动dy为正
                int height = titleLinear.getHeight();
                if (scrollYDistance <= 0) {
                    titleLinear.getBackground().setAlpha(255);
                } else if (scrollYDistance > 0 && scrollYDistance <= height) {
                    float scall = (float) scrollYDistance / (float) height;
                    float alpha = (1 - scall) * 255;
                    Log.e(TAG, "onScrolled:"+ alpha + "/////" + 255 * (1 - scall) );
                    titleLinear.getBackground().setAlpha((int)alpha);
                    titleLinear.invalidate();
                }else {
                    titleLinear.getBackground().setAlpha(0);
                }
            }
        });
    }

    class PullMoreTask extends AsyncTask<Void, Void, List<Response>> {


        @Override
        protected List<Response> doInBackground(Void... voids) {
            String adTypeString = "1";
            String pageNoString = "1";
            String url = getUrl(adTypeString, pageNoString);
            return JsonUtils.getTravelResponse(url);
        }

        @Override
        protected void onPostExecute(List<Response> list) {
            super.onPostExecute(list);
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (list != null) {
                dataList = list;
                adapter.notifyDataSetChanged();
            }

        }
    }

    class LoadMoreTask extends AsyncTask<String, Void, List<Response>> {

        @Override
        protected List<Response> doInBackground(String... strings) {
            String adTypeString = strings[0];
            String pageNoString = strings[1];
            String url = getUrl(adTypeString, pageNoString);
            return JsonUtils.getTravelResponse(url);
        }

        @Override
        protected void onPostExecute(List<Response> list) {
            super.onPostExecute(list);
            if (list != null) {
                dataList.addAll(list);
                adapter.notifyItemRangeInserted(dataList.size() + 1, list.size());
                adapter.notifyItemRemoved(adapter.getItemCount() - 1);
                int totalMemory = (int) Runtime.getRuntime().totalMemory();
                Log.e("上拉加载后当前堆栈大小", "MemoryCacheUtils: " + totalMemory / (1024*1024));
            }
        }
    }

    //根据adType和pageNo拼接完整的url
    private String getUrl(String adType, String pageNo) {
        String url = "http://api.lvxingx.com/note/get?sig=4b9fe66fc5fdbe26e0a660fc0d6a542f&uid=suitcase&pageCount=20&userId=21580&ts=1502270352301"
                + "&adType=" + adType + "&pageNo=" + pageNo;
        return url;
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.cancelAllTasks();
    }
}
