package fu.prm391.sampl.finalproject_movieappclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fu.prm391.sampl.finalproject_movieappclient.Adapter.MovieShowAdapter;
import fu.prm391.sampl.finalproject_movieappclient.Adapter.SliderPagerAdapter;
import fu.prm391.sampl.finalproject_movieappclient.Model.MovieItemClickListenerNew;
import fu.prm391.sampl.finalproject_movieappclient.Model.SliderSide;
import fu.prm391.sampl.finalproject_movieappclient.Model.VideoDetail;

public class MainActivity extends AppCompatActivity implements MovieItemClickListenerNew, NavigationView.OnNavigationItemSelectedListener {
    private MovieShowAdapter movieShowAdapter;
    private DatabaseReference mDatabaseReference;
    private List<VideoDetail> uploads, uploadListLatest, uploadListPoppular;
    private List<VideoDetail> actionMovies, sportMovies, comedyMovies, romanticMovies, fantasyMovies;
    private ViewPager sliderPager;
    private List<SliderSide> uploadsSlider;
    private TabLayout indicator, tabMovieAction;
    private RecyclerView rcvMovies, rcvMoviesWeek, tab;
    ProgressDialog progressDialog;

    //Toolbar
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private ImageView imgAvatar;
    private TextView txtName, txtEmail;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.nav_drawer_open,
                R.string.nav_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.actionbar);
        progressDialog = new ProgressDialog(this);

        showUserInformation();
        addAllMovies();
        moviesViewTab();


    }

    private void addAllMovies() {
        uploads = new ArrayList<>();
        uploadListLatest = new ArrayList<>();
        uploadListPoppular = new ArrayList<>();
        actionMovies = new ArrayList<>();
        fantasyMovies = new ArrayList<>();
        comedyMovies = new ArrayList<>();
        sportMovies = new ArrayList<>();
        romanticMovies = new ArrayList<>();
        uploadsSlider = new ArrayList<>();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("videos");
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploadListLatest.removeAll(uploadListLatest);
                uploadListPoppular.removeAll(uploadListPoppular);
                uploadsSlider.removeAll(uploadsSlider);
                actionMovies.removeAll(actionMovies);
                fantasyMovies.removeAll(fantasyMovies);
                comedyMovies.removeAll(comedyMovies);
                romanticMovies.removeAll(romanticMovies);
                sportMovies.removeAll(sportMovies);
                for(DataSnapshot postSnapshot : snapshot.getChildren()) {
                    VideoDetail upload = postSnapshot.getValue(VideoDetail.class);
                    SliderSide slide = postSnapshot.getValue(SliderSide.class);
                    if(upload.getVideoType().equals("Latest Movie")) {
                        uploadListLatest.add(upload);
                    }
                    if(upload.getVideoType().equals("Best Popular Movie")) {
                        uploadListPoppular.add(upload);
                    }
                    if(upload.getVideoSlide().equals("Slide Movie")) {
                        uploadsSlider.add(slide);
                    }
                    if(upload.getVideoCategory().equals("Action")) {
                        actionMovies.add(upload);
                    }
                    if(upload.getVideoCategory().equals("Fantasy")) {
                        fantasyMovies.add(upload);
                    }
                    if(upload.getVideoCategory().equals("Cartoon")) {
                        comedyMovies.add(upload);
                    }
                    if(upload.getVideoCategory().equals("Romantic")) {
                        romanticMovies.add(upload);
                    }
                    if(upload.getVideoCategory().equals("Sports")) {
                        sportMovies.add(upload);
                    }
//                    uploads.removeAll(uploads);
                    uploads.add(upload);
                }
                initSlider();
                iniPopularMovies();
                iniWeekMovies();

                refreshTabMovies();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void refreshTabMovies() {
        switch (tabMovieAction.getSelectedTabPosition()) {
            case 0:
                iniActionMovies();
                break;
            case 1:
                initFantasyMovies();
                break;
            case 2:
                initCartoonMovies();
                break;
            case 3:
                iniRomanticMovies();
                break;
            case 4:
                iniSportMovies();
                break;
        }
    }

    private void initSlider() {
        SliderPagerAdapter sliderPagerAdapter = new SliderPagerAdapter(this, uploadsSlider);
        sliderPager.setAdapter(sliderPagerAdapter);
        sliderPagerAdapter.notifyDataSetChanged();

        //timer
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 10000, 6000);
        indicator.setupWithViewPager(sliderPager, true);
    }

    private void iniWeekMovies() {
        movieShowAdapter = new MovieShowAdapter(this, uploadListLatest, this);
        rcvMoviesWeek.setAdapter(movieShowAdapter);
        rcvMoviesWeek.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }

    private void iniPopularMovies() {
        movieShowAdapter = new MovieShowAdapter(this, uploadListPoppular, this);
        rcvMovies.setAdapter(movieShowAdapter);
        rcvMovies.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }

    private void iniActionMovies() {
        movieShowAdapter = new MovieShowAdapter(this, actionMovies, this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }
    private void iniSportMovies() {
        movieShowAdapter = new MovieShowAdapter(this, sportMovies, this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }
    private void initFantasyMovies() {
        movieShowAdapter = new MovieShowAdapter(this, fantasyMovies, this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }
    private void iniRomanticMovies() {
        movieShowAdapter = new MovieShowAdapter(this, romanticMovies, this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }
    private void initCartoonMovies() {
        movieShowAdapter = new MovieShowAdapter(this, comedyMovies, this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        movieShowAdapter.notifyDataSetChanged();
    }

    private void moviesViewTab() {

        tabMovieAction.addTab(tabMovieAction.newTab().setText("Action"));
        tabMovieAction.addTab(tabMovieAction.newTab().setText("Fantasy"));
        tabMovieAction.addTab(tabMovieAction.newTab().setText("Cartoon"));
        tabMovieAction.addTab(tabMovieAction.newTab().setText("Romantic"));
        tabMovieAction.addTab(tabMovieAction.newTab().setText("Sports"));
        tabMovieAction.setTabGravity(TabLayout.GRAVITY_FILL);
        tabMovieAction.setTabTextColors(ColorStateList.valueOf(Color.WHITE));
        tabMovieAction.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        iniActionMovies();
                        break;
                    case 1:
                        initFantasyMovies();
                        break;
                    case 2:
                        initCartoonMovies();
                        break;
                    case 3:
                        iniRomanticMovies();
                        break;
                    case 4:
                        iniSportMovies();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    private void initViews() {
        tabMovieAction = findViewById(R.id.tabActionMovies);
        sliderPager = findViewById(R.id.sliderPager);
        indicator = findViewById(R.id.indicator);
        rcvMoviesWeek = findViewById(R.id.rcvMovies_week);
        rcvMovies = findViewById(R.id.rcvMovies);
        tab = findViewById(R.id.tabRecycler);

        navigationView = findViewById(R.id.navigation_view);

    }

    public void showUserInformation() {
        imgAvatar = navigationView.getHeaderView(0).findViewById(R.id.img_avatar);
        txtName = navigationView.getHeaderView(0).findViewById(R.id.txt_account_name);
        txtEmail = navigationView.getHeaderView(0).findViewById(R.id.txt_account_email);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            return;
        }
        String strName = user.getDisplayName();
        String strEmail = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        if(strName == null) {
            txtName.setVisibility(View.GONE);
        } else {
            txtName.setVisibility(View.VISIBLE);
            txtName.setText(strName);
        }
        txtEmail.setText(strEmail);
        Glide.with(this).load(photoUrl).error(R.drawable.ic_avatar_default).into(imgAvatar);

    }

    @Override
    public void onMovieClick(VideoDetail videoDetail, ImageView imageView) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("video", videoDetail);

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,
                imageView, "sharedName");
        startActivity(intent, options.toBundle());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                return false;
            case R.id.nav_favorite:
                Intent intentFavor = new Intent(this, MovieFavoriteActivity.class);
                startActivity(intentFavor);
                break;
            case R.id.nav_account:
                Intent intentAccount = new Intent(this, AccountActivity.class);
                startActivity(intentAccount);
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
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public class SliderTimer extends TimerTask {
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(sliderPager.getCurrentItem() < uploadsSlider.size()-1) {
                        sliderPager.setCurrentItem(sliderPager.getCurrentItem()+1);
                    }else {
                        sliderPager.setCurrentItem(0);
                    }
                }
            });
        }
    }
}