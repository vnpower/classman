package vn.bkas.classman.ui.lectures;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import vn.bkas.classman.databinding.FragmentLecturesBinding;

public class LecturesFragment extends Fragment {

    private FragmentLecturesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LecturesViewModel homeViewModel =
                new ViewModelProvider(this).get(LecturesViewModel.class);

        binding = FragmentLecturesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textLectures;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}