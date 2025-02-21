package vn.bkas.classman.ui.students;

import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

import de.hdodenhof.circleimageview.CircleImageView;

public class Students {
    public String studentName, studentEmail;
    public int imageAvatarId;

    public Students(String studentName, String studentEmail, int imageAvatarId) {
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.imageAvatarId = imageAvatarId;
    }

    @BindingAdapter("android:imageUrl")
    public static void LoadAvatar(View view, int imageAvatarId) {

        CircleImageView circleImageView = (CircleImageView) view;
        circleImageView.setImageDrawable(ContextCompat.getDrawable(view.getContext(), imageAvatarId));
    }
}
