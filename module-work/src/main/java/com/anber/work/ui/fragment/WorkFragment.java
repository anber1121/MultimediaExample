package com.anber.work.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.goldze.base.base.BaseFragment;
import com.goldze.base.router.RouterFragmentPath;
import com.anber.work.BR;
import com.anber.work.R;
import com.anber.work.databinding.FragmentWorkBinding;
import com.anber.work.ui.viewmodel.WorkViewModel;


/**
 * Created by goldze on 2018/6/21
 */
@Route(path = RouterFragmentPath.Work.PAGER_WORK)
public class WorkFragment extends BaseFragment<FragmentWorkBinding, WorkViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_work;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
    }
}
