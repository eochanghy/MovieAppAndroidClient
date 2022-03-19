package fu.prm391.sampl.finalproject_movieappclient.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import fu.prm391.sampl.finalproject_movieappclient.Model.SliderSide;
import fu.prm391.sampl.finalproject_movieappclient.MovieDetailActivity;
import fu.prm391.sampl.finalproject_movieappclient.MoviePlayerActivity;
import fu.prm391.sampl.finalproject_movieappclient.R;

public class SliderPagerAdapter extends PagerAdapter {
    private Context mContext;
    private List<SliderSide> mList;

    public SliderPagerAdapter(Context mContext, List<SliderSide> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View slideLayout = inflater.inflate(R.layout.slide_item, null);
        ImageView slideImage = slideLayout.findViewById(R.id.slide_img);
        TextView slideTitle = slideLayout.findViewById(R.id.slide_title);
        FloatingActionButton floatingActionButton = slideLayout.findViewById(R.id.floatingActionButton);
        Glide.with(mContext).load(mList.get(position).getVideoThumb()).into(slideImage);
        slideTitle.setText(mList.get(position).getVideoName());
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Play
                Intent intent = new Intent(mContext, MoviePlayerActivity.class);
                intent.putExtra("videoUri", mList.get(position).getVideoUrl());
                mContext.startActivity(intent);
            }
        });
        container.addView(slideLayout);
        return  slideLayout;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
