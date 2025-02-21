package vn.bkas.classman.ui.students;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.bkas.classman.R;
import vn.bkas.classman.databinding.FragmentStudentsBinding;


public class StudentsFragment extends Fragment {

    private FragmentStudentsBinding studentsBinding;
    private RecyclerAdapterStudent recyclerAdapterStudent;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StudentsViewModel galleryViewModel =
                new ViewModelProvider(this).get(StudentsViewModel.class);

        studentsBinding = FragmentStudentsBinding.inflate(inflater, container, false);
        View root = studentsBinding.getRoot();

        final TextView textView = studentsBinding.textStudents;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        studentsBinding.recyclerViewStudents.setLayoutManager(new LinearLayoutManager(getContext()));
        studentsBinding.recyclerViewStudents.setHasFixedSize(true);

        recyclerAdapterStudent = new RecyclerAdapterStudent(getContext(), prepareStudent());
        studentsBinding.recyclerViewStudents.setAdapter(recyclerAdapterStudent);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        studentsBinding = null;
    }

    private List<Students> prepareStudent() {
        String[] studentName = getResources().getStringArray(R.array.studentNames);
        List<String> studentEmail = Arrays.asList(getResources().getStringArray(R.array.studentEmails));
        int[] imageAvatarId = {R.drawable.person1, R.drawable.person2, R.drawable.person3,R.drawable.person4,
                R.drawable.person5, R.drawable.person6, R.drawable.person7};
        List<Students> Students = new ArrayList<Students>();
        int count = 0;
        for (String name : studentName) {
            Students.add(new Students(name, studentEmail.get(count), imageAvatarId[count]));
            count++;
        }

        return Students;
    }
}