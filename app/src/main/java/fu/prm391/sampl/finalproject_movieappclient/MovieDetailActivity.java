package fu.prm391.sampl.finalproject_movieappclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fu.prm391.sampl.finalproject_movieappclient.Adapter.MovieShowAdapter;
import fu.prm391.sampl.finalproject_movieappclient.Model.MovieItemClickListenerNew;
import fu.prm391.sampl.finalproject_movieappclient.Model.VideoDetail;
import fu.prm391.sampl.finalproject_movieappclient.Service.FloatingWidgetService;

public class MovieDetailActivity extends AppCompatActivity implements MovieItemClickListenerNew {
    private MovieShowAdapter movieShowAdapter;
    private ImageView movieThumbnail, movieCoverImg;
    private TextView txtTitle, txtDescription;
    private FloatingActionButton play_fab;
    private RecyclerView rcvCast, rcvSimilarMovies;
    private DatabaseReference mDatabaseReference;
    private List<VideoDetail> uploads, actionMovies, sportMovies, comedyMovies, romanticMovies, adventureMovies;
    private String currentVideoUrl;
    private String currentVideoCategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        initView();
        initSimilarMovieRecycle();
        initSimilarMovie();

        play_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieDetailActivity.this, MoviePlayerActivity.class);
                intent.putExtra("videoUri", currentVideoUrl);
                startActivity(intent);
                stopService(new Intent(getApplicationContext(), FloatingWidgetService.class));
            }
        });
    }

    private void initSimilarMovie() {
        switch (currentVideoCategory) {
            case "Action":
                movieShowAdapter = new MovieShowAdapter(this, actionMovies, this);

                rcvSimilarMovies.setAdapter(movieShowAdapter);
                rcvSimilarMovies.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                movieShowAdapter.notifyDataSetChanged();
                break;
            case "Adventure":
                movieShowAdapter = new MovieShowAdapter(this, adventureMovies, this);

                rcvSimilarMovies.setAdapter(movieShowAdapter);
                rcvSimilarMovies.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                movieShowAdapter.notifyDataSetChanged();
                break;
            case "Comedy":
                movieShowAdapter = new MovieShowAdapter(this, comedyMovies, this);

                rcvSimilarMovies.setAdapter(movieShowAdapter);
                rcvSimilarMovies.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                movieShowAdapter.notifyDataSetChanged();
                break;
            case "Romantic":
                movieShowAdapter = new MovieShowAdapter(this, romanticMovies, this);

                rcvSimilarMovies.setAdapter(movieShowAdapter);
                rcvSimilarMovies.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                movieShowAdapter.notifyDataSetChanged();
                break;
            case "Sports":
                movieShowAdapter = new MovieShowAdapter(this, sportMovies, this);

                rcvSimilarMovies.setAdapter(movieShowAdapter);
                rcvSimilarMovies.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                movieShowAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void initSimilarMovieRecycle() {
        uploads = new ArrayList<>();
        sportMovies = new ArrayList<>();
        comedyMovies = new ArrayList<>();
        actionMovies = new ArrayList<>();
        adventureMovies = new ArrayList<>();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("videos");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot : snapshot.getChildren()) {
                    VideoDetail upload = postSnapshot.getValue(VideoDetail.class);
                    if(upload.getVideoCategory().equals("Action")) {
                        actionMovies.add(upload);
                    }
                    if(upload.getVideoCategory().equals("Adventure")) {
                        adventureMovies.add(upload);
                    }
                    if(upload.getVideoCategory().equals("Comedy")) {
                        comedyMovies.add(upload);
                    }
                    if(upload.getVideoCategory().equals("Romantic")) {
                        romanticMovies.add(upload);
                    }
                    if(upload.getVideoCategory().equals("Sports")) {
                        sportMovies.add(upload);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void initView() {
        play_fab = findViewById(R.id.play_fab);
        txtTitle = findViewById(R.id.detail_movie_title);
        txtDescription = findViewById(R.id.detail_movie_description);
        movieThumbnail = findViewById(R.id.detail_movie_img);
        movieCoverImg = findViewById(R.id.detail_movie_cover);
        rcvSimilarMovies = findViewById(R.id.rcv_similar_movies);
        String movieTitle = getIntent().getExtras().getString("title");
        String imgRecoresId = getIntent().getExtras().getString("imgUrl");
        String imgCover = getIntent().getExtras().getString("imgCover");
        String movieDetailText = getIntent().getExtras().getString("movieDetail");
        String movieUrl = getIntent().getExtras().getString("movieUrl");
        String movieCategory = getIntent().getExtras().getString("movieCategory");
        currentVideoUrl = movieUrl;
        currentVideoCategory = movieCategory;
        Glide.with(this).load(imgRecoresId).into(movieThumbnail);
        Glide.with(this).load(imgCover).into(movieCoverImg);
        txtTitle.setText(movieTitle);
        txtDescription.setText(movieDetailText);
        getSupportActionBar().setTitle(movieTitle);


    }

    @Override
    public void onMovieClick(VideoDetail videoDetail, ImageView imageView) {
        txtTitle.setText(videoDetail.getVideoName());
        getSupportActionBar().setTitle(videoDetail.getVideoName());
        Glide.with(this).load(videoDetail.getVideoThumb()).into(movieThumbnail);
        Glide.with(this).load(videoDetail.getVideoThumb()).into(movieCoverImg);
        txtDescription.setText(videoDetail.getVideoDescription());
        currentVideoUrl = videoDetail.getVideoUrl();
        currentVideoCategory = videoDetail.getVideoCategory();
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MovieDetailActivity.this, imageView, "sharedName");
        options.toBundle();
    }
}