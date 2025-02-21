package vn.bkas.classman.ui.students;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StudentsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public StudentsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Danh sách sinh viên");
    }

    public LiveData<String> getText() {
        return mText;
    }
}