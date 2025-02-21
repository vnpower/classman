package vn.bkas.classman.databases.exercisedb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExercisesDAO {

    @Insert
    void insertExercise(Exercises exercises);

    @Query("SELECT * FROM Exercises")
    List<Exercises> getAllExercises();

}
