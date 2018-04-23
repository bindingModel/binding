package com.binding.demo.ui.home;

import android.databinding.ObservableInt;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.binding.demo.R;
import com.binding.demo.base.cycle.BaseFragment;
import com.binding.demo.databinding.ActivityHomeBinding;
import com.binding.demo.inject.qualifier.manager.ActivityFragmentManager;
import com.binding.model.model.ModelView;
import com.binding.model.model.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by arvin on 2017/11/27.
 */
@ModelView(R.layout.activity_home)
public class HomeModel extends ViewModel<HomeActivity,ActivityHomeBinding> {
    @Inject HomeModel() {}
    private static final List<HomeEntity> list = new ArrayList<>();
    @ActivityFragmentManager
    @Inject FragmentManager fm;
    private int currentTab = -1;
    public ObservableInt position = new ObservableInt(-1);
    static {
        list.add(new HomeEntity());
        list.add(new HomeEntity());
        list.add(new HomeEntity());
    }

    @Override
    public void attachView(Bundle savedInstanceState, HomeActivity homeActivity) {
        super.attachView(savedInstanceState, homeActivity);
        position.set(0);
        checkFragment(0);
    }

//        int position = radioGroup.indexOfChild(radioGroup.findViewById(checkedId));
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        checkFragment(position.get());
    }

    private void checkFragment(int position) {
        if (position < 0 || position >= list.size()) return;
        FragmentTransaction ft = fm.beginTransaction();
        if (position < currentTab) ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_right_out);
        else ft.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out);
        if (currentTab >= 0) {
            BaseFragment beforeFragment = list.get(currentTab).getItem(position, getDataBinding().homeFrame);
            beforeFragment.onPause();
            ft.remove(beforeFragment);
        }
        BaseFragment fragment = list.get(position).getItem(position, getDataBinding().homeFrame);
        ViewGroup viewGroup = getDataBinding().homeFrame;
        for (int i = 0; i < viewGroup.getChildCount() - list.size(); i++)
            viewGroup.removeView(viewGroup.getChildAt(i));
        if (fragment.isAdded()) fragment.onResume();
        else ft.add(R.id.home_frame, fragment);
        ft.show(fragment);
        ft.commitAllowingStateLoss();
        currentTab = position;
    }
}
