package fu.prm391.sampl.finalproject_movieappclient.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import fu.prm391.sampl.finalproject_movieappclient.Model.VideoDetail;
import fu.prm391.sampl.finalproject_movieappclient.R;

public class MovieFavoriteAdapter extends RecyclerView.Adapter<MovieFavoriteAdapter.MovieFavoriteViewHolder>{
    private Context mContext;
    private List<VideoDetail> mListVideo;
    private HandleFavoriteItemListener handleFavoriteItemListener;

    public interface HandleFavoriteItemListener {
        public void checkFavorite(VideoDetail video, boolean isChecked);
        public void clickItem(VideoDetail video, ImageView imageView);
    }

    public MovieFavoriteAdapter(Context mContext, HandleFavoriteItemListener handleFavoriteItemListener) {
        this.mContext = mContext;
        this.handleFavoriteItemListener = handleFavoriteItemListener;
    }
    public void setData(List<VideoDetail>list) {
        mListVideo = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieFavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new MovieFavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieFavoriteViewHolder holder, int position) {
        VideoDetail videoDetail = mListVideo.get(position);
        if(videoDetail == null) {
            return;
        }
        Glide.with(mContext).load(videoDetail.getVideoThumb()).into(holder.imgVideo);
        holder.txtName.setText(videoDetail.getVideoName());
        holder.cbFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handleFavoriteItemListener.checkFavorite(videoDetail, isChecked);
            }
        });
        holder.imgVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFavoriteItemListener.clickItem(videoDetail, holder.imgVideo);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListVideo != null) {
            return mListVideo.size();
        }
        return 0;
    }

    public class MovieFavoriteViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgVideo;
        public TextView txtName;
        public CheckBox cbFavorite;
        public MovieFavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            imgVideo = itemView.findViewById(R.id.img_favorite_video_thumb);
            txtName = itemView.findViewById(R.id.txt_favorite_video_name);
            cbFavorite = itemView.findViewById(R.id.check_item_favorite);
        }


    }
}
