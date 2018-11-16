package com.xjj.freight.utils.photoselect.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xjj.freight.R;
import com.xjj.freight.utils.photoselect.model.PhotoFolder;

import java.util.List;

/**
 * Describe: 相册选择界面的适配器
 *
 * @author xujingjing
 * @date 2018/6/15 0015
 */
public class FolderAdapter extends RecyclerView.Adapter {

    private List<PhotoFolder> mDatas;
    private Context mContext;
    private ViewGroup mParent;
    private OnItemClickListener onItemClickListener;
    public FolderAdapter(Context context, List<PhotoFolder> mDatas) {
        this.mDatas = mDatas;
        this.mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public PhotoFolder getItem(int position) {
        if (mDatas == null || mDatas.size() == 0) {
            return null;
        }
        return mDatas.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mParent = parent;
        return new FolderHolder(mContext,parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final FolderHolder folderHolder = (FolderHolder) holder;
        final PhotoFolder photoFolder = mDatas.get(position);
        Glide.with(mContext).load(photoFolder.getPhotoList().get(0).getPath()).into(folderHolder.photoIV);
        folderHolder.selectIV.setVisibility(View.GONE);
        if(photoFolder.isSelected()) {
            folderHolder.selectIV.setVisibility(View.VISIBLE);
        }
        folderHolder.folderNameTV.setText(photoFolder.getName());
        folderHolder.photoNumTV.setText(photoFolder.getPhotoList().size() + "张");
        folderHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(mParent,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    class FolderHolder extends RecyclerView.ViewHolder{
        private ImageView photoIV;
        private TextView folderNameTV;
        private TextView photoNumTV;
        private ImageView selectIV;

       public FolderHolder(Context context,ViewGroup parent) {
           super(LayoutInflater.from(context).inflate(R.layout.photopicker_item_folder_layout,parent, false));
           photoIV = itemView.findViewById(R.id.imageview_folder_img);
           folderNameTV = itemView.findViewById(R.id.textview_folder_name);
           photoNumTV = itemView.findViewById(R.id.textview_photo_num);
           selectIV = itemView.findViewById(R.id.imageview_folder_select);
       }
   }

    /**
     * item点击事件接口
     */
    public interface OnItemClickListener{
        void onItemClick(ViewGroup parent,int position);
    }

}
