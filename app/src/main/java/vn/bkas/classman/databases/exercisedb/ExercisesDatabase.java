package vn.bkas.classman.databases.exercisedb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import vn.bkas.classman.ui.exercises.ExercisesFragment;
//import vn.bkas.classman.ui.exercises.ExercisesFragment;

@Database(entities = {Exercises.class}, version = 1, exportSchema = false)
public abstract class ExercisesDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "Exercises.db";
    private static ExercisesDatabase instance;

    public static synchronized ExercisesDatabase getInstance(ExercisesFragment context) {
        if (instance == null) {
//            instance = Room.databaseBuilder(context.getApplicationContext(),
//                            ExercisesDatabase.class, DATABASE_NAME)
//                    .allowMainThreadQueries()
//                    .build();
            instance = Room.databaseBuilder(context.getActivity().getApplicationContext(),
                            ExercisesDatabase.class, DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build();
        }

        return instance;
    }

    public abstract ExercisesDAO exercisesDAO();
}
