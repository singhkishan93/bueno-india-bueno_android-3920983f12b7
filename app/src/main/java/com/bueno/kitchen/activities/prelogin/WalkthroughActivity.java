package com.bueno.kitchen.activities.prelogin;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bueno.kitchen.R;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WalkthroughActivity extends Activity {

    private static final int[] WALKTHROUGH_SCREENS = {R.drawable.walkthrough_1,
            R.drawable.walkthrough_2,
            R.drawable.walkthrough_3,
            R.drawable.walkthrough_4};

    @Bind(R.id.pager_main)
    public ViewPager viewPager;
    @Bind(R.id.pager_indicator)
    public CirclePageIndicator pageIndicator;
    @Bind(R.id.skip_button)
    public TextView skipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough);
        ButterKnife.bind(this);

        setUpUI();
    }

    private void setUpUI() {
        viewPager.setAdapter(new WalkThroughAdapter());
        pageIndicator.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                skipButton.setText(position >= WALKTHROUGH_SCREENS.length - 1 ? "Done" : "Skip");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick(R.id.skip_button)
    public void onClick(View v) {
        setResult(RESULT_OK);
        finish();
    }

    public class WalkThroughAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return WALKTHROUGH_SCREENS.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView row = (ImageView) getLayoutInflater().inflate(R.layout.cell_walkthrough, container, false);

            Picasso.with(WalkthroughActivity.this)
                    .load(WALKTHROUGH_SCREENS[position])
                    .fit()
                    .noFade()
                    .into(row);

            container.addView(row, 0);
            row.setTag(position);
            return row;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
