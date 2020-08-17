package jujube.android.widgets.starter;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jujube.android.starter.adapter.RecyclerThrowableAdapter;
import jujube.android.starter.adapter.ViewHolder;
import jujube.android.starter.recyclerview.GridItemDecoration;
import jujube.android.widgets.R;

public class PhotoLayout extends LinearLayout {



    public static final int REQUEST_CODE_PHOTO = 0;

    public static final int REQUEST_CODE_VIDEO = 1;

    private int type = REQUEST_CODE_PHOTO;

    private int max = 3;

    private String path;

    private List<File> photoList = new ArrayList<>();

    private PhotoAdapter photoAdapter;

    private File currentPhoto;

    private Fragment fragment;

    public List<File> getPhotoList() {
        return photoList;
    }

    public PhotoLayout(Context context, String taskName) {
        super(context);
        getValue(context, null);
        init();
    }

    public PhotoLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getValue(context, attrs);
        init();
    }

    public PhotoLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getValue(context, attrs);
        init();
    }

    private void getValue(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PhotoLayout);
        max = typedArray.getInt(R.styleable.PhotoLayout_max_items, 3);
        type = typedArray.getInt(R.styleable.PhotoLayout_type, REQUEST_CODE_PHOTO);
        typedArray.recycle();
    }

    private void init() {
        RecyclerView recyclerView = new RecyclerView(getContext());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        recyclerView.setLayoutParams(lp);
        addView(recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        GridItemDecoration decoration = new GridItemDecoration.Builder().includeLREdge(false).includeTBEdge(false).spaceSize(36).spanCount(3).build();
        recyclerView.addItemDecoration(decoration);
        photoAdapter = new PhotoAdapter(this);
        recyclerView.setAdapter(photoAdapter);
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void loadPhotos(String[] photos) {
        File photoDir = new File(Environment.getExternalStorageDirectory(), path);
        List<File> result = new ArrayList<>();
        for (String fileName : photos) {
            result.add(new File(photoDir, fileName));
        }
        photoList.addAll(result);
        photoAdapter.notifyDataSetChanged();
    }

    public void onGetPhotos(Intent data) {
        photoList.add(currentPhoto);

        photoAdapter.notifyDataSetChanged();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory(), path);
        if (!storageDir.exists())
            storageDir.mkdirs();
        File image = File.createTempFile(
                imageFileName,
                type == REQUEST_CODE_PHOTO ? ".jpg" : ".mp4",
                storageDir
        );
        currentPhoto = image;
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent;

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (photoFile != null) {
            if (type == REQUEST_CODE_PHOTO) {
                takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "gov.zjch.uploader.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }else {
                takePictureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                Uri videoURI = FileProvider.getUriForFile(getContext(),
                        "gov.zjch.uploader.fileprovider",
                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
            }
            if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                fragment.startActivityForResult(takePictureIntent, type);
            }
        }
    }


    private class PhotoAdapter extends RecyclerThrowableAdapter<ViewHolder> {

        private final static int TYPE_ADD = 1;
        private final static int TYPE_PREVIEW = 2;

        private final int MAX;

        public PhotoAdapter(PhotoLayout photoLayout) {
            this.MAX = photoLayout.max;
        }

        @Override
        protected void bind(@NonNull ViewHolder holder, int position) throws Exception {
            int viewType = getItemViewType(position);
            if (viewType == TYPE_ADD) {
                holder.itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dispatchTakePictureIntent();
                    }
                });
            }else if (viewType == TYPE_PREVIEW){
                ImageView imageView = holder.itemView.findViewById(R.id.preview);
                ImageView deleteView = holder.itemView.findViewById(R.id.delete);

                int resize = getResources().getDimensionPixelSize(R.dimen.photo_grid_size);
                // add glide dependency
//                Glide.with(getContext())
//                        .load(photoList.get(position))
//                        .diskCacheStrategy(DiskCacheStrategy.DATA)
//                        .apply(new RequestOptions()
//                                .override(resize, resize)
//                                .centerCrop())
//                        .into(imageView);
                deleteView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photoList.remove(position);
                        notifyDataSetChanged();
                    }
                });
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_PREVIEW)
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_preview, parent, false));
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_add, parent, false));
        }

        @Override
        public int getItemCount() {
            int count = photoList.size() + 1;
            if (count > MAX) {
                count = MAX;
            }
            return count;
        }

        @Override
        public int getItemViewType(int position) {
            return (position == photoList.size() && position != MAX) ? TYPE_ADD : TYPE_PREVIEW;
        }
    }
}
