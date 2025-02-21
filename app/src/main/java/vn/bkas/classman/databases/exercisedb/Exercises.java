package vn.bkas.classman.databases.exercisedb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Blob;

@Entity (tableName = "Exercises")
public class Exercises {

    @PrimaryKey (autoGenerate = true)
    public int id;

    @ColumnInfo(name = "exerciseCode")
    public String exerciseCode;

    @ColumnInfo(name = "imageCircuitUri")
    public String imageCircuitUri;

    @ColumnInfo(name = "imageCircuit",typeAffinity = ColumnInfo.BLOB)
    public byte[] imageCircuit;

    @ColumnInfo(name = "exerciseElementsCount")
    public String exerciseElementsCount;

    @ColumnInfo(name = "exerciseEquationCount")
    public String exerciseEquationCount;

    @ColumnInfo(name = "exerciseNotes")
    public String exerciseNotes;

    public Exercises(int id,
                     String exerciseCode,
                     String imageCircuitUri,
                     byte[] imageCircuit,
                     String exerciseElementsCount,
                     String exerciseEquationCount,
                     String exerciseNotes) {
        this.id = id;
        this.exerciseCode = exerciseCode;
        this.imageCircuitUri = imageCircuitUri;
        this.imageCircuit = imageCircuit;
        this.exerciseElementsCount = exerciseElementsCount;
        this.exerciseEquationCount = exerciseEquationCount;
        this.exerciseNotes = exerciseNotes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExerciseCode() {
        return exerciseCode;
    }

    public void setExerciseCode(String exerciseCode) {
        this.exerciseCode = exerciseCode;
    }

    public String getImageCircuitUri() {
        return imageCircuitUri;
    }

    public void setImageCircuitUri(String imageCircuitUri) {
        this.imageCircuitUri = imageCircuitUri;
    }

    public byte[] getImageCircuit() {
        return imageCircuit;
    }

    public void setImageCircuit(byte[] imageCircuit) {
        this.imageCircuit = imageCircuit;
    }

    public String getExerciseElementsCount() {
        return exerciseElementsCount;
    }

    public void setExerciseElementsCount(String exerciseElementsCount) {
        this.exerciseElementsCount = exerciseElementsCount;
    }

    public String getExerciseEquationCount() {
        return exerciseEquationCount;
    }

    public void setExerciseEquationCount(String exerciseEquationCount) {
        this.exerciseEquationCount = exerciseEquationCount;
    }

    public String getExerciseNotes() {
        return exerciseNotes;
    }

    public void setExerciseNotes(String exerciseNotes) {
        this.exerciseNotes = exerciseNotes;
    }

    // Hien thi hinh anh mach dien
    @BindingAdapter("android:imageCircuitUrl")
    public static void LoadCircuit(View view, byte[] imageCircuit) {

        ImageView imageView = (ImageView) view;
        Bitmap image = BitmapFactory.decodeByteArray(imageCircuit, 0, imageCircuit.length);
        imageView.setImageBitmap(image);
    }
}
