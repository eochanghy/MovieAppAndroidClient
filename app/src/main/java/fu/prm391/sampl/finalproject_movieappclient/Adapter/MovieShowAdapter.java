package fu.prm391.sampl.finalproject_movieappclient.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import fu.prm391.sampl.finalproject_movieappclient.Model.MovieItemClickListenerNew;
import fu.prm391.sampl.finalproject_movieappclient.Model.VideoDetail;
import fu.prm391.sampl.finalproject_movieappclient.R;

public class MovieShowAdapter extends RecyclerView.Adapter<MovieShowAdapter.MyViewHolder> {
    private Context mContext;
    private List<VideoDetail> uploads;
    private MovieItemClickListenerNew movieItemClickListenerNew;

    public MovieShowAdapter(Context mContext, List<VideoDetail> uploads, MovieItemClickListenerNew movieItemClickListenerNew) {
        this.mContext = mContext;
        this.uploads = uploads;
        this.movieItemClickListenerNew = movieItemClickListenerNew;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.movie_item_new, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        VideoDetail videoDetail = uploads.get(position);
        holder.txtTitle.setText(videoDetail.getVideoName());
        Glide.with(mContext).load(videoDetail.getVideoThumb()).into(holder.imgMovie);
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTitle;
        public ImageView imgMovie;
        public ConstraintLayout container;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.item_movie_title);
            imgMovie = itemView.findViewById(R.id.item_movie_img);
            container = itemView.findViewById(R.id.container);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    movieItemClickListenerNew.onMovieClick(uploads.get(getAdapterPosition()), imgMovie);
                }
            });
        }
    }
}
