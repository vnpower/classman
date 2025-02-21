package vn.bkas.classman.ui.exercises;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.bkas.classman.databases.exercisedb.Exercises;
import vn.bkas.classman.databinding.ExerciseItemLayoutBinding;

public class RecyclerAdapterExercise extends RecyclerView.Adapter<RecyclerAdapterExercise.ExerciseViewHolder> {

    private Context context;
    private List<Exercises> exercises = new ArrayList<>();


    public static class ExerciseViewHolder extends RecyclerView.ViewHolder{

        ExerciseItemLayoutBinding exerciseItemLayoutBinding;

        public ExerciseViewHolder(@NonNull ExerciseItemLayoutBinding itemView) {
            super(itemView.getRoot());
            this.exerciseItemLayoutBinding = itemView;
        }
    }

    public RecyclerAdapterExercise(Context context, List<Exercises> exercises) {
        this.context = context;
        this.exercises = exercises;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerAdapterExercise.ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        ExerciseItemLayoutBinding exerciseItemLayoutBinding = ExerciseItemLayoutBinding.inflate(LayoutInflater.from(context), viewGroup, false);
        ExerciseViewHolder exerciseViewHolder = new ExerciseViewHolder(exerciseItemLayoutBinding);
        return exerciseViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterExercise.ExerciseViewHolder viewHolder, int position) {

        Exercises exercise = exercises.get(position);
        viewHolder.exerciseItemLayoutBinding.setExercise(exercise);

    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

}
