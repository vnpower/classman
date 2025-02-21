package vn.bkas.classman.ui.classroom;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ClassroomViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ClassroomViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Quản lý lớp học");
    }

    public LiveData<String> getText() {
        return mText;
    }
}