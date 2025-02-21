package vn.bkas.classman.ui.students;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.bkas.classman.databinding.StudentItemLayoutBinding;

public class RecyclerAdapterStudent extends RecyclerView.Adapter<RecyclerAdapterStudent.StudentViewHolder> {

    private Context context;
    private List<Students> students = new ArrayList<>();

    public RecyclerAdapterStudent(Context context, List<Students> students) {
        this.context = context;
        this.students = students;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        StudentItemLayoutBinding studentItemLayoutBinding = StudentItemLayoutBinding.inflate(LayoutInflater.from(context), viewGroup, false);

        StudentViewHolder studentViewHolder = new StudentViewHolder(studentItemLayoutBinding);
        return studentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder viewHolder, int position) {

        Students student = students.get(position);
        viewHolder.studentItemLayoutBinding.setStudent(student);

    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder{

        StudentItemLayoutBinding studentItemLayoutBinding;

        public StudentViewHolder(@NonNull StudentItemLayoutBinding itemView) {
            super(itemView.getRoot());
            this.studentItemLayoutBinding = itemView;
        }
    }
}
