package vn.bkas.classman.ui.exercises;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExercisesViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ExercisesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Thư viện bài tập môn học");
    }

    public LiveData<String> getText() {
        return mText;
    }
}