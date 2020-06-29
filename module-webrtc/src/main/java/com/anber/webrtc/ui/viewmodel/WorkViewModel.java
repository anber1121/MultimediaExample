package com.anber.webrtc.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.goldze.base.base.BaseViewModel;
import com.anber.webrtc.BR;
import com.anber.webrtc.R;

import me.tatarka.bindingcollectionadapter2.ItemBinding;

/**
 * Created by goldze on 2018/6/21.
 */

public class WorkViewModel extends BaseViewModel {
    public WorkViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        for (int i = 0; i < 10; i++) {
            observableList.add(new WorkItemViewModel(this, "条目" + i));
        }
    }
    //给RecyclerView添加ObservableList
    public ObservableList<WorkItemViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public ItemBinding<WorkItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.grid_work);
}
