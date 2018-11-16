package com.xjj.freight.utils.photoselect;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.xjj.freight.BaseActivity;
import com.xjj.freight.R;
import com.xjj.freight.utils.NameThreadFactory;
import com.xjj.freight.utils.ProgressDialog;
import com.xjj.freight.utils.permission.PermissionCallBack;
import com.xjj.freight.utils.permission.Permissions;
import com.xjj.freight.utils.photoselect.adapter.FolderAdapter;
import com.xjj.freight.utils.photoselect.adapter.PhotoAdapter;
import com.xjj.freight.utils.photoselect.model.Photo;
import com.xjj.freight.utils.photoselect.model.PhotoFolder;
import com.xjj.freight.view.recyclerview.XtRecyclerView;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 图片选择界面（支持单选，多选）
 */
public class PhotoPickerActivity extends BaseActivity implements View.OnClickListener, PhotoAdapter.OnItemClickListener, PhotoAdapter.PhotoClickCallBack, FolderAdapter.OnItemClickListener {

    public final static String KEY_RESULT = "picker_result";
    public final static int REQUEST_CAMERA = 1;
    /***  是否显示相机 */
    public final static String EXTRA_SHOW_CAMERA = "is_show_camera";
    /*** 照片选择模式 */
    public final static String EXTRA_SELECT_MODE = "select_mode";
    /*** 最大选择数量 */
    public final static String EXTRA_MAX_MUN = "max_num";
    /***  单选 */
    public final static int MODE_SINGLE = 0;
    /*** 多选 */
    public final static int MODE_MULTI = 1;
    /***  默认最大选择数量 */
    public final static int DEFAULT_NUM = 9;

    private final static String ALL_PHOTO = "全部图片";
    /***  是否显示相机，默认不显示 */
    private boolean mIsShowCamera = false;
    /***  照片选择模式，默认是单选模式 */
    private int mSelectMode = MODE_SINGLE;
    /***  最大选择数量，仅多选模式有用 */
    private int mMaxNum;

    private TextView tvTitle;
    private RecyclerView xtRecyclerView;
    private View bottomTabBar;
    private TextView floderName;
    private TextView photoNum;
    private PhotoAdapter adapter;
    private FolderAdapter folderAdapter;
    private  RecyclerView mFolderRecycleview;
    private ProgressDialog mLoadDialog;
    private ScheduledThreadPoolExecutor executor;

    /*** 文件夹列表是否处于显示状态*/
    private boolean mIsFolderViewShow = false;
    /*** 文件夹列表是否被初始化，确保只被初始化一次*/
    private boolean mIsFolderViewInit = false;
    /**
     * 拍照时存储拍照结果的临时文件
     */
    private File mTmpFile;
    /*** 相册列表数据*/
    private Map<String, PhotoFolder> mFolderMap;
    /*** 所有照片数据*/
    private List<Photo> mPhotoLists = new ArrayList<>();
    /*** 选中的照片数据*/
    private ArrayList<String> mSelectList = new ArrayList<>();
    private final List<PhotoFolder> folders = new ArrayList<>();
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            getPhotosSuccess();
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);
        executor = new ScheduledThreadPoolExecutor(3, new NameThreadFactory(getClass().getSimpleName()));
        mLoadDialog = new ProgressDialog(this);
        getParams();
        initView();
        if (!OtherUtils.isExternalStorageAvailable()) {
            Toast.makeText(this, getString(R.string.no_sd_card), Toast.LENGTH_SHORT).show();
            return;
        }
        initdata();
    }

    private void getParams() {
        mIsShowCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, false);
        mSelectMode = getIntent().getIntExtra(EXTRA_SELECT_MODE, MODE_SINGLE);
        mMaxNum = getIntent().getIntExtra(EXTRA_MAX_MUN, DEFAULT_NUM);
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        xtRecyclerView = findViewById(R.id.xt_recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        xtRecyclerView.setLayoutManager(gridLayoutManager);
        xtRecyclerView.setHasFixedSize(true);
        bottomTabBar = findViewById(R.id.bottom_tab_bar);
        floderName = findViewById(R.id.floder_name);
        photoNum = findViewById(R.id.photo_num);
        floderName.setOnClickListener(this);
        photoNum.setOnClickListener(this);
        if (mSelectMode == MODE_SINGLE) {
            //确定按钮隐藏
            photoNum.setVisibility(View.GONE);
        }
        bottomTabBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //消费触摸事件，防止触摸底部tab栏也会选中图片
                return true;
            }
        });
    }

    private void initdata() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mLoadDialog.show();
                mFolderMap = getPhotos(PhotoPickerActivity.this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floder_name:
                toggleFolderList(folders);
                break;
            case R.id.photo_num:
                returnData();
                break;
            default:
                break;
        }
    }


    /**
     * 显示或者隐藏文件夹列表
     * @param folders
     */
    private void toggleFolderList(final List<PhotoFolder> folders) {
        //初始化文件夹列表
        if(!mIsFolderViewInit) {
            ViewStub folderStub = findViewById(R.id.floder_stub);
            folderStub.inflate();
            View dimLayout = findViewById(R.id.dim_layout);
            mFolderRecycleview = findViewById(R.id.floderRecycleview);
            LinearLayoutManager LayoutManager = new LinearLayoutManager(this);
            LayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mFolderRecycleview.setLayoutManager(LayoutManager);
            folderAdapter = new FolderAdapter(this, folders);
            mFolderRecycleview.setAdapter(folderAdapter);
            folderAdapter.setOnItemClickListener(this);
            dimLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mIsFolderViewShow) {
                        toggle();
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            initAnimation(dimLayout);
            mIsFolderViewInit = true;
        }
        toggle();
    }

    /**
     * 弹出或者收起文件夹列表
     */
    private void toggle() {
        if(mIsFolderViewShow) {
            outAnimatorSet.start();
            mIsFolderViewShow = false;
        } else {
            inAnimatorSet.start();
            mIsFolderViewShow = true;
        }
    }


    /**
     * 初始化文件夹列表的显示隐藏动画
     */
    AnimatorSet inAnimatorSet = new AnimatorSet();
    AnimatorSet outAnimatorSet = new AnimatorSet();
    private void initAnimation(View dimLayout) {
        ObjectAnimator alphaInAnimator, alphaOutAnimator, transInAnimator, transOutAnimator;
        //获取actionBar的高
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        /**
         * 这里的高度是，屏幕高度减去上、下tab栏，并且上面留有一个tab栏的高度
         * 所以这里减去3个actionBarHeight的高度
         */
        int height = OtherUtils.getHeightInPx(this) - 3*actionBarHeight;
        alphaInAnimator = ObjectAnimator.ofFloat(dimLayout, "alpha", 0f, 0.7f);
        alphaOutAnimator = ObjectAnimator.ofFloat(dimLayout, "alpha", 0.7f, 0f);
        transInAnimator = ObjectAnimator.ofFloat(mFolderRecycleview, "translationY", height , 0);
        transOutAnimator = ObjectAnimator.ofFloat(mFolderRecycleview, "translationY", 0, height);

        LinearInterpolator linearInterpolator = new LinearInterpolator();
        inAnimatorSet.play(transInAnimator).with(alphaInAnimator);
        inAnimatorSet.setDuration(300);
        inAnimatorSet.setInterpolator(linearInterpolator);
        outAnimatorSet.play(transOutAnimator).with(alphaOutAnimator);
        outAnimatorSet.setDuration(300);
        outAnimatorSet.setInterpolator(linearInterpolator);
    }

    @Override
    public void onItemClick(ViewGroup parent, int position) {
        switch (parent.getId()){
            //图片的点击
            case R.id.xt_recyclerview:
                if (adapter.isShowCamera() && position == 0) {
                    Permissions.getPermission(this).requestPermission(new PermissionCallBack() {
                        @Override
                        public void onGranted() {
                            showCamera();
                        }
                    }, Permission.Group.CAMERA);
                    return;
                }
                //根据选择模式来处理事件
                if (mSelectMode == MODE_SINGLE) {
                    if (adapter == null){
                        return;
                    }
                    mSelectList.clear();
                    mSelectList.addAll(adapter.getSelectedPhotosPath());
                    returnData();
                }else {
                    Toast.makeText(this, "我是 多选 图片" + position, Toast.LENGTH_SHORT).show();
                }
                break;
            //相册文件夹点击
            case R.id.floderRecycleview:
                for (PhotoFolder folder : folders) {
                    folder.setIsSelected(false);
                }
                PhotoFolder folder = folders.get(position);
                folder.setIsSelected(true);
                folderAdapter.notifyDataSetChanged();

                mPhotoLists.clear();
                mPhotoLists.addAll(folder.getPhotoList());
                //这里重新设置adapter而不是直接notifyDataSetChanged，是让GridView返回顶部
                if (ALL_PHOTO.equals(folder.getName())) {
                    adapter.setIsShowCamera(mIsShowCamera);
                } else {
                    adapter.setIsShowCamera(false);
                }
                xtRecyclerView.setAdapter(adapter);
                tvTitle.setText(folder.getName());
                toggle();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPhotoClick() {
        if (adapter == null){
            return;
        }
        mSelectList.clear();
        mSelectList.addAll(adapter.getSelectedPhotosPath());
        if (mSelectList.size() > 0){
            photoNum.setEnabled(true);
            photoNum.setText(String.format(getString(R.string.ok_s),String.valueOf(mSelectList.size())));
        }else {
            photoNum.setEnabled(false);
            photoNum.setText(getString(R.string.ok));
        }
    }

    /**
     * 调取相机
     */
    private void showCamera() {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // 设置系统相机拍照后的输出路径
            mTmpFile = OtherUtils.createFile(this);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                String authority = getPackageName() + ".fileprovider";
                uri = FileProvider.getUriForFile(this, authority, mTmpFile);
            } else {
                uri = Uri.fromFile(mTmpFile);
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else {
            Toast.makeText(this, R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 查询出所有图片并分组
     */
    private Map<String, PhotoFolder> getPhotos(Context context) {
        final Map<String, PhotoFolder> folderMap = new HashMap<>();
        String allPhotosKey = getString(R.string.whole_photo);
        PhotoFolder allFolder = new PhotoFolder();
        allFolder.setName(allPhotosKey);
        allFolder.setDirPath(allPhotosKey);
        allFolder.setPhotoList(new ArrayList<Photo>());
        folderMap.put(allPhotosKey, allFolder);

        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = context.getContentResolver();
        // 只查询jpeg和png的图片
        Cursor mCursor = mContentResolver.query(imageUri, null,
                MediaStore.Images.Media.MIME_TYPE + " in(?, ?)",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED + " desc");
        int pathIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATA);
        int degreeIndex = mCursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION);
        if (mCursor.moveToFirst()) {
            do {
                // 获取图片的路径
                String path = mCursor.getString(pathIndex);
                int degree = mCursor.getInt(degreeIndex);
                // 获取该图片的父路径名
                File parentFile = new File(path).getParentFile();
                if (parentFile == null) {
                    continue;
                }
                String dirPath = parentFile.getAbsolutePath();
                if (folderMap.containsKey(dirPath)) {
                    Photo photo = new Photo(path);
                    photo.setDegree(degree);
                    PhotoFolder photoFolder = folderMap.get(dirPath);
                    photoFolder.getPhotoList().add(photo);
                    folderMap.get(allPhotosKey).getPhotoList().add(photo);
                    continue;
                } else {
                    // 初始化imageFolder
                    PhotoFolder photoFolder = new PhotoFolder();
                    List<Photo> photoList = new ArrayList<>();
                    Photo photo = new Photo(path);
                    photo.setDegree(degree);
                    photoList.add(photo);
                    photoFolder.setPhotoList(photoList);
                    photoFolder.setDirPath(dirPath);
                    photoFolder.setName(dirPath.substring(dirPath.lastIndexOf(File.separator) + 1, dirPath.length()));
                    folderMap.put(dirPath, photoFolder);
                    folderMap.get(allPhotosKey).getPhotoList().add(photo);
                }
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        handler.sendEmptyMessage(0);
        return folderMap;
    }


    /**
     * 获取图片成功后操作
     */
    private void getPhotosSuccess() {
        mLoadDialog.dismiss();
        if (mFolderMap.get(ALL_PHOTO).getPhotoList() != null) {
            mPhotoLists.addAll(mFolderMap.get(ALL_PHOTO).getPhotoList());
        }
        adapter = new PhotoAdapter(this, mPhotoLists);
        adapter.setIsShowCamera(mIsShowCamera);
        adapter.setSelectMode(mSelectMode);
        adapter.setMaxNum(mMaxNum);
        adapter.setPhotoClickCallBack(this);
        xtRecyclerView.setAdapter(adapter);
        Set<String> keys = mFolderMap.keySet();
        for (String key : keys) {
            if (ALL_PHOTO.equals(key)) {
                PhotoFolder folder = mFolderMap.get(key);
                folder.setIsSelected(true);
                folders.add(0, folder);
            } else {
                folders.add(mFolderMap.get(key));
            }
        }
       adapter.setOnItemClickListener(this);
    }

    /**
     * 返回选择图片的路径
     */
    private void returnData() {
        // 返回已选择的图片数据
        Intent data = new Intent();
        data.putStringArrayListExtra(KEY_RESULT, mSelectList);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            //拍照返回
            case REQUEST_CAMERA:
                if (mTmpFile != null) {
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + mTmpFile.getAbsolutePath())));
                    mSelectList.add(mTmpFile.getAbsolutePath());
                    returnData();
                }
                break;
            default:
                break;
        }
    }
}
