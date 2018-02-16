package eu.lynxit.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eu.lynxit.popularmovies.api.model.MovieDTO;
import eu.lynxit.popularmovies.fragments.MovieDetailsFragment;
import eu.lynxit.popularmovies.fragments.MoviesListFragment;

public class MainActivity extends AppCompatActivity {
    private Fragment mCurrentFragment;
    private ImageView mBackButton;
    private ImageView mMoreButton;
    private TextView mTitle;
    private PopupWindow popup;
    public MainViewModel mViewModel;
    private View.OnClickListener mOnBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private View.OnClickListener mOnMoreClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            makeOptionsMenu();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mBackButton = findViewById(R.id.activity_main_toolbar_back);
        mBackButton.setOnClickListener(mOnBackClickListener);
        mMoreButton = findViewById(R.id.activity_main_toolbar_more);
        mMoreButton.setOnClickListener(mOnMoreClickListener);
        mTitle = findViewById(R.id.activity_main_toolbar_title);
        mTitle.setText(R.string.app_name);
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment != null && fragment.isVisible()) {
                        mCurrentFragment = fragment;
                        toggleToolbar();
                    }
                }
            }
        });
        if (savedInstanceState == null) {
            startFragment(new MoviesListFragment(), false, false);
        } else {
            List<Fragment> tmp = new ArrayList<Fragment>();
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment instanceof MoviesListFragment || fragment instanceof MovieDetailsFragment)
                    tmp.add(fragment);
            }
            if (tmp.size() > 0) {
                Fragment fragment = tmp.get(tmp.size() - 1);
                if (fragment instanceof MovieDetailsFragment || fragment instanceof MoviesListFragment) {
                    mCurrentFragment = fragment;
                }
            }
        }
        toggleToolbar();
        mViewModel.movies.observe(this, new Observer<List<MovieDTO>>() {
            @Override
            public void onChanged(@Nullable List<MovieDTO> movieDTOS) {
                if (mCurrentFragment != null && mCurrentFragment instanceof MoviesListFragment) {
                    ((MoviesListFragment) mCurrentFragment).reapplyMovies(movieDTOS);
                }
            }
        });
    }

    public void toggleToolbar() {
        if (mCurrentFragment instanceof MoviesListFragment) {
            mBackButton.setVisibility(View.GONE);
            mMoreButton.setVisibility(View.VISIBLE);
        } else if (mCurrentFragment instanceof MovieDetailsFragment) {
            mMoreButton.setVisibility(View.GONE);
            mBackButton.setVisibility(View.VISIBLE);
        }
    }

    public void startFragment(Fragment fragment, Boolean addToBackStack, Boolean pop) {
        if (fragment != null) {
            if (pop) {
                getSupportFragmentManager().popBackStack();
            }
            mCurrentFragment = fragment;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_frame_layout, fragment, null);
            if (addToBackStack) {
                transaction.addToBackStack(fragment.getTag());
            }
            toggleToolbar();
            transaction.commitAllowingStateLoss();
        }
    }

    private void makeOptionsMenu() {
        if (popup == null) {
            popup = new PopupWindow(this);
            View tmpView = LayoutInflater.from(this).inflate(R.layout.window_popup, null);
            tmpView.findViewById(R.id.popup_top_rated).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewModel.loadTopRated();
                    if (popup != null) {
                        popup.dismiss();
                    }
                }
            });
            tmpView.findViewById(R.id.popup_popular).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewModel.loadPopular();
                    if (popup != null) {
                        popup.dismiss();
                    }
                }
            });
            tmpView.findViewById(R.id.popup_favorites).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewModel.loadFavorites(MainActivity.this);
                    if (popup != null) {
                        popup.dismiss();
                    }
                }
            });
            popup.setOutsideTouchable(true);
            popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    popup = null;
                }
            });
            popup.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        if (popup != null) {
                            popup.dismiss();
                            popup = null;
                            return true;
                        }
                    }
                    return false;
                }
            });
            popup.setContentView(tmpView);
            tmpView.measure(2000, 2000);
            popup.setClippingEnabled(true);
            popup.showAtLocation(
                    mMoreButton,
                    Gravity.TOP | Gravity.LEFT,
                    mMoreButton.getRight() - tmpView.getMeasuredWidth(),
                    mMoreButton.getBottom() - 5
            );
        }
    }

}
