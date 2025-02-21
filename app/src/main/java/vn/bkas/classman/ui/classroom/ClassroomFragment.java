package vn.bkas.classman.ui.classroom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import vn.bkas.classman.databinding.FragmentClassroomBinding;

public class ClassroomFragment extends Fragment {

    private FragmentClassroomBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ClassroomViewModel classroomViewModel =
                new ViewModelProvider(this).get(ClassroomViewModel.class);

        binding = FragmentClassroomBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textClassroom;
        classroomViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}