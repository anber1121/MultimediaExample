package com.anber.rtmp.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.goldze.base.base.BaseViewModel;
import com.goldze.base.base.ItemViewModel;

/**
 * Created by goldze on 2017/7/17.
 */

public class WorkItemViewModel extends ItemViewModel {
    public ObservableField<String> text = new ObservableField<>();

    public WorkItemViewModel(@NonNull BaseViewModel viewModel, String text) {
        super(viewModel);
        this.text.set(text);
    }
}
