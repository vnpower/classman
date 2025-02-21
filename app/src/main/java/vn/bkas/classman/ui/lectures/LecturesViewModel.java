package vn.bkas.classman.ui.lectures;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LecturesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LecturesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Các bài giảng");
    }

    public LiveData<String> getText() {
        return mText;
    }
}