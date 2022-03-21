package fu.prm391.sampl.finalproject_movieappclient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fu.prm391.sampl.finalproject_movieappclient.Adapter.MovieFavoriteAdapter;
import fu.prm391.sampl.finalproject_movieappclient.Model.VideoDetail;

public class MovieFavoriteActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MovieFavoriteAdapter.HandleFavoriteItemListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private MovieFavoriteAdapter adapter;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser currentUser;
    private List<VideoDetail> mList;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_favorite);

        initView();
        initToolbar();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        mList = new ArrayList<>();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("favoriteMovies").child(currentUser.getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildren() != null) {
                    mList.clear();
                    for (DataSnapshot item: snapshot.getChildren()) {
                        VideoDetail video = item.getValue(VideoDetail.class);
                        mList.add(video);
                    }
                    adapter.setData(mList);
                    recyclerView.setAdapter(adapter);
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    private void initView() {
        recyclerView = findViewById(R.id.rcv_favorite);
        adapter = new MovieFavoriteAdapter(this, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    public void initToolbar() {
        Toolbar toolbar = findViewById(R.id.favorite_toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.favorite_drawer_layout);
        navigationView = findViewById(R.id.favorite_nav_view);
        navigationView.getMenu().findItem(R.id.nav_favorite).setChecked(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                startActivity(homeIntent);
                finishAffinity();
                break;
            case R.id.nav_favorite:

                break;
            case R.id.nav_account:

                break;
            case R.id.nav_sign_out:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void checkFavorite(VideoDetail video, boolean isChecked) {
        DatabaseReference favoriteListRef = FirebaseDatabase.getInstance().getReference().child("favoriteMovies");
        if(!isChecked) {
            //add to wishlist
            favoriteListRef.child(currentUser.getUid()).child(video.getVideoId()).removeValue();
        }
    }

    @Override
    public void clickItem(VideoDetail video, ImageView imageView) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("video", video);

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                imageView, "sharedName");
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}