package com.xjj.freight.utils.photoselect.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.xjj.freight.R;
import com.xjj.freight.utils.photoselect.OtherUtils;
import com.xjj.freight.utils.photoselect.PhotoPickerActivity;
import com.xjj.freight.utils.photoselect.SquareImageView;
import com.xjj.freight.utils.photoselect.model.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe: 照片选择界面的适配器
 *
 * @author xujingjing
 * @date 2018/6/15 0015
 */
public class PhotoAdapter extends RecyclerView.Adapter{

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_PHOTO = 1;

    private List<Photo> mDatas;
    private Context mContext;
    /*** 帮助查找选中的图片*/
    private ViewGroup mParent;
    /*** 是否显示相机，默认不显示*/
    private boolean mIsShowCamera;
    /*** 照片选择模式，默认单选*/
    private int mSelectMode = PhotoPickerActivity.MODE_SINGLE;
    /*** 图片选择数量*/
    private int mMaxNum = PhotoPickerActivity.DEFAULT_NUM;
    /*** 存放已选中的Photo数据*/
    private List<String> mSelectedPhotosPath = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private PhotoClickCallBack mCallBack;

    public PhotoAdapter(Context mContext, List<Photo> datas) {
        this.mContext = mContext;
        this.mDatas = datas;
    }

    /**
     * 是否显示照相图片
     * @return
     */
    public boolean isShowCamera() {
        return mIsShowCamera;
    }

    public void setIsShowCamera(boolean isShowCamera) {
        this.mIsShowCamera = isShowCamera;
        if (mIsShowCamera) {
            Photo camera = new Photo(null);
            camera.setIsCamera(true);
            mDatas.add(0, camera);
        }
    }

    public void setMaxNum(int maxNum) {
        this.mMaxNum = maxNum;
    }

    public void setSelectMode(int selectMode) {
        this.mSelectMode = selectMode;
    }

    /**
     *得到选中的图片
     */
    public List<String> getSelectedPhotosPath(){
        return mSelectedPhotosPath;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setPhotoClickCallBack(PhotoClickCallBack callback) {
        mCallBack = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mParent = parent;
        return new PhotoHolder(mContext,parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final Photo photo = mDatas.get(position);
        final PhotoHolder photoHolder = (PhotoHolder) holder;

        if (getItemViewType(position) == TYPE_CAMERA){
            photoHolder.tvSel.setVisibility(View.GONE);
            Glide.with(mContext).load(photo.getPath()).placeholder(R.mipmap.img_camera).into(photoHolder.img);
        }else {
              Glide.with(mContext).load(photo.getPath()).placeholder(R.mipmap.photopicker_ic_photo_loading).into(photoHolder.img);
            //调整内存缓存的大小
             Glide.get(mContext).setMemoryCategory(MemoryCategory.HIGH);
        }
        //设置好tag,方便更改选中图片的值
        photoHolder.tvSel.setTag( photo.getPath());
        //设置当前图像是否选中
        setSelectBackground(photoHolder, photo.getPath());
        photoHolder.tvSel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = photo.getPath();
                if (mSelectedPhotosPath.contains(path)){
                    mSelectedPhotosPath.remove(path);
                }else{
                    //达到选择最大数量提示
                    if(mSelectedPhotosPath.size() >= mMaxNum) {
                        Toast.makeText(mContext, R.string.msg_maxi_capacity, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mSelectedPhotosPath.add(path);
                }
                setSelectBackground(photoHolder,path);
                setAllSelectBackground();
                if (mCallBack != null){
                    mCallBack.onPhotoClick();
                }
            }
        });
        photoHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSelectMode == PhotoPickerActivity.MODE_SINGLE) {
                    mSelectedPhotosPath.add(photo.getPath());
                }
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(mParent,position);
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if(getItem(position) != null && getItem(position).isCamera()) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PHOTO;
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    //更新当前照片索引值和背景
    private void setSelectBackground(PhotoHolder photoHolder,String path){
        photoHolder.tvSel.setBackgroundResource(mSelectedPhotosPath.contains(path) ? R.drawable.photopicker_multiple_selected : R.drawable.photopicker_multiple_normal);
        photoHolder.tvSel.setText(mSelectedPhotosPath.contains(path) ? String.valueOf(getSelectIndex(path)) : "");
        photoHolder.img.setAlpha(mSelectedPhotosPath.contains(path)?  0.7f: 1.0f);
    }

    //获取选中图片索引
    private int getSelectIndex(String path){
        for(int i=0;i<mSelectedPhotosPath.size();i++){
            if(mSelectedPhotosPath.get(i).equals(path)){
                return i+1;
            }
        }
        return -1;
    }

    //更新所有照片索引
    private void setAllSelectBackground(){
        for(int i=0;i<mSelectedPhotosPath.size();i++){
            View view = mParent.findViewWithTag(mSelectedPhotosPath.get(i));
            if(view != null){
                ((TextView)view).setText(String.valueOf(i+1));
            }
        }
    }

    class PhotoHolder extends RecyclerView.ViewHolder{
        private SquareImageView img;
        private TextView tvSel;

        public PhotoHolder(Context context,ViewGroup parent) {
            super(LayoutInflater.from(context).inflate(R.layout.photopicker_item_photo_layout,parent, false));
            img = itemView.findViewById(R.id.img);
            tvSel = itemView.findViewById(R.id.tv_sel);
            if(mSelectMode == PhotoPickerActivity.MODE_SINGLE) {
                tvSel.setVisibility(View.GONE);
            }
            OtherUtils.setTouchDelegate(tvSel,15);
        }
    }

    /**
     *获取图片信息
     */
    public Photo getItem(int position) {
        if(mDatas == null || mDatas.size() == 0){
            return null;
        }
        return mDatas.get(position);
    }

    /**
     * item点击事件接口
     */
    public interface OnItemClickListener{
        void onItemClick(ViewGroup parent,int position);
    }

    /**
     * 多选时，点击相片(选中或取消)的回调接口
     */
    public interface PhotoClickCallBack {
        void onPhotoClick();
    }

}
