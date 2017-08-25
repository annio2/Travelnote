package com.example.travelnote.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.travelnote.R;
import com.example.travelnote.imagehandle.MyImageLoder;
import com.example.travelnote.bean.Response;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by wuguanglin on 2017/8/7.
 */

public class RecyclerPullMoreAdapter extends RecyclerView.Adapter {
    private static final int VIEW_ITEM = 1;//正常item的viewType
    private static final int VIEW_FOOT = 2;//底部progressDialog的viewType
    private static final int VIEW_HEAD = 3;//顶部被标题栏覆盖的头部的viewType
    private Context context;
    private static final String HOST = "http://api.lvxingx.com";
    private List<Response> dataList;
    private LayoutInflater layoutInflater;
    private MyImageLoder myImageLoder;//自定义图片加载器
    private OnItemClickListener onItemClickListener;

    //RecyclerView的item点击事件监听接口
    public interface OnItemClickListener{
        void onItemClick(int position, String url);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }

    public RecyclerPullMoreAdapter(Context context, List<Response> dataList) {
        this.context = context;
        this.dataList = dataList;
        layoutInflater = LayoutInflater.from(context);
        myImageLoder = new MyImageLoder(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_HEAD){
            View v = layoutInflater.inflate(R.layout.item_header, parent, false);
            viewHolder = new HeadHolder(v);
        }else if (viewType == VIEW_FOOT){
            View v = layoutInflater.inflate(R.layout.item_footer, parent, false);
            viewHolder = new FootHolder(v);
        }else {
            View v = layoutInflater.inflate(R.layout.item_pull_more, parent, false);
            viewHolder = new ItemHolder(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemHolder){
            int pos = position - 1;
            String imgUrl = getImgUrl(dataList.get(pos).getPicInfo().getUrl());//图片url
            String imgCirUrl = getImgUrl(dataList.get(pos).getUserIconUrl());//头像url
            String zanNum = String.valueOf(dataList.get(pos).getZanNum());//赞数量
            String clickNum = String.valueOf(dataList.get(pos).getClickNum());//查看数量
            String commentNum = String.valueOf(dataList.get(pos).getCommentNum());//评论数量
            final String link = dataList.get(pos).getLink();//点击图片链接
            myImageLoder.display(imgUrl, ((ItemHolder) holder).cardItemImg);//加载图片
//            Picasso.with(context).load(imgUrl).into(((ItemHolder) holder).cardItemImg);
            if (TextUtils.isEmpty(dataList.get(pos).getUserIconUrl())){
                //获取到的头像路径为空时，加载默认图片
                ((ItemHolder) holder).cardItemCirImg.setImageResource(R.mipmap.default_cirmg);
            }else {
                myImageLoder.display(imgCirUrl, ((ItemHolder) holder).cardItemCirImg);
//                Picasso.with(context).load(imgCirUrl).into(((ItemHolder) holder).cardItemCirImg);
            }
            ((ItemHolder) holder).cardItemTitle.setText(dataList.get(pos).getTitle());//标题
            ((ItemHolder) holder).cardItemZan.setText(zanNum);
            ((ItemHolder) holder).cardItemLook.setText(clickNum);
            ((ItemHolder) holder).cardItemComent.setText(commentNum);
            ((ItemHolder) holder).cardItemImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getAdapterPosition() + 1;
                    onItemClickListener.onItemClick(pos, link);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        //添加了头部和尾部，总数加2
        return dataList.size() == 0 ? 0 : dataList.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return VIEW_HEAD;
        }else if (position == getItemCount() - 1){
            return VIEW_FOOT;
        }else {
            return VIEW_ITEM;
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder{
        private ImageView cardItemImg;//大图
        private CircleImageView cardItemCirImg;//头像
        private TextView cardItemTitle, cardItemZan, cardItemLook, cardItemComent;//标题、赞数、查看数量、评论数量
        private ItemHolder(View itemView) {
            super(itemView);
            cardItemImg = (ImageView)itemView.findViewById(R.id.cardItemImg);
            cardItemCirImg = (CircleImageView)itemView.findViewById(R.id.cardItemCirImg);
            cardItemTitle = (TextView)itemView.findViewById(R.id.cardItemTitle);
            cardItemZan = (TextView)itemView.findViewById(R.id.cardItemZan);
            cardItemLook = (TextView)itemView.findViewById(R.id.cardItemLook);
            cardItemComent = (TextView)itemView.findViewById(R.id.cardItemComent);
        }
    }

    class HeadHolder extends RecyclerView.ViewHolder{
        private HeadHolder(View itemView) {
            super(itemView);
        }
    }

    class FootHolder extends RecyclerView.ViewHolder{
        private ProgressBar progressBar;
        private FootHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBarLoad);
        }
    }

    private String getImgUrl(String urlString){
        String url;
        if (urlString != null && !urlString.equals("")){
            String topFourChar = urlString.substring(0, 3);
            if (topFourChar.equals("http")){
                url = urlString;
            }else {
                url = HOST + urlString;
            }
            return url;
        }else {
            return null;
        }
    }

    //将本地缓存记录同步到journal文件中
    public void flushCache(){
        myImageLoder.flushCache();
    }

    public void cancelAllTasks(){
        myImageLoder.cancelAllTasks();
    }
}
