package vn.bkas.classman.ui.exercises;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.service.autofill.Validators.or;

import static org.apache.poi.hssf.record.HSSFRecordTypes.DRAWING;
import static org.apache.poi.hssf.record.HSSFRecordTypes.HYPERLINK;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import static vn.bkas.classman.databases.DatabaseExporter.exportDatabaseToExcel;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.apache.commons.io.FileUtils;
import org.apache.poi.sl.usermodel.TableShape;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;


import vn.bkas.classman.R;
import vn.bkas.classman.databases.exercisedb.Exercises;
import vn.bkas.classman.databases.exercisedb.ExercisesDatabase;
import vn.bkas.classman.databinding.FragmentExercisesBinding;

public class ExercisesFragment extends Fragment {

    private FragmentExercisesBinding exercisesBinding;
    private RecyclerAdapterExercise recyclerAdapterExercise;

    private List<Exercises> mListExercise = new ArrayList<>();

    private EditText et_exerciseCode;
    private EditText et_exerciseElementsCount;
    private EditText et_exerciseEquationCount;
    private EditText et_exerciseNotes;

    private Button btnAddExercise;
    private Button btnImportExercise;
    private Button btnExportExercise;

    private ImageView imageView;

    private ActivityResultLauncher<Intent> pickMedia;

    //For excel
    ActivityResultLauncher<Intent> filePicker;

    private ActivityResultLauncher<Intent> filePickerLauncher;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ExercisesViewModel exercisesViewModel =
                new ViewModelProvider(this).get(ExercisesViewModel.class);

        exercisesBinding = FragmentExercisesBinding.inflate(inflater, container, false);
        View root = exercisesBinding.getRoot();

        exerciseInitUI(root);

        final TextView textView = exercisesBinding.textExercises;
        exercisesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        exercisesBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        exercisesBinding.recyclerView.setHasFixedSize(true);

        //recyclerAdapterExercise = new RecyclerAdapterExercise(getContext(), prepareExercise());


        mListExercise = ExercisesDatabase.getInstance(this).exercisesDAO().getAllExercises();
        recyclerAdapterExercise = new RecyclerAdapterExercise(getContext(), mListExercise);
        exercisesBinding.recyclerView.setAdapter(recyclerAdapterExercise);


        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                addExercise();
            }
        });

        btnImportExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        // Initialize the ActivityResultLauncher
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri fileUri = data.getData();
                            if (fileUri != null) {
                                // Handle the selected file URI
                                // readExcelFromUri(fileUri);
                                readExcelFromUriToRoomSQLite(fileUri);
                            }
                        }
                    }
                }
        );

        btnExportExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDatabaseToExcel(getContext(), "Exercises.db", "Exercises", "Exercises2");
            }
        });


        // Register the activity result callback for photo picker
        pickMedia = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        imageView.setImageURI(selectedImageUri);
                    }
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent class will help to go to next activity using
                // it's object named intent.
                // SecondActivty is the name of new created EmptyActivity.
//                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
//                startActivity(intent);
                // Launch the photo picker
                launchPhotoPicker();
                Toast.makeText(getContext(), "Click image", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        exercisesBinding = null;
    }

    private List<Exercises> prepareExercise() {
        String[] exerciseCode = getResources().getStringArray(R.array.exerciseCodes);
        //String[] exerciseElementsCount = getResources().getStringArray(R.array.exerciseElementsCount);
        //String[] exerciseEquationCount = getResources().getStringArray(R.array.exerciseEquationCount);
        //String[] exerciseNotes = getResources().getStringArray(R.array.exerciseNotes);
        List<String> exerciseElementsCount = Arrays.asList(getResources().getStringArray(R.array.exerciseElementsCount));
        List<String> exerciseEquationCount = Arrays.asList(getResources().getStringArray(R.array.exerciseEquationCount));
        List<String> exerciseNotes = Arrays.asList(getResources().getStringArray(R.array.exerciseNotes));

        int[] imageCircuitId = {R.drawable.circuit1, R.drawable.circuit2, R.drawable.circuit3};

        List<Exercises> Exercises = new ArrayList<Exercises>();

        int count = 0;
//        mListExercise = ExercisesDatabase.getInstance(this).exercisesDAO().getAllExercises();
//        int size = mListExercise.size();
//        for (String code : exerciseCode) {
//            Exercises.add(new Exercises("Mã bài tập: " + code,
//                    "Số phần tử:" + exerciseElementsCount.get(count),
//                    "Số phương trình:" + exerciseEquationCount.get(count),
//                    "Ghi chú: " + exerciseNotes.get(count),
//                    imageCircuitId[count]));
//            count++;
//        }

        return Exercises;
    }

    private void exerciseInitUI(View view){
        et_exerciseCode = (EditText) view.findViewById(R.id.et_exerciseCode);
        et_exerciseElementsCount = (EditText) view.findViewById(R.id.et_exerciseElementsCount);
        et_exerciseEquationCount = (EditText) view.findViewById(R.id.et_exerciseEquationCount);
        et_exerciseNotes = (EditText) view.findViewById(R.id.et_exerciseNotes);
        btnAddExercise = (Button) view.findViewById(R.id.btnAddExercise);
        btnImportExercise = (Button) view.findViewById(R.id.btnImportExercise);
        btnExportExercise = (Button) view.findViewById(R.id.btnExportExercise);
        imageView = (ImageView) view.findViewById(R.id.circuitImage);


    }

    private void addExercise() {
        int size = mListExercise.size();
        String uri = "https://bkas.vn/vinh.jpg";
        String exerciseCode = et_exerciseCode.getText().toString();

        String exerciseElementsCount = et_exerciseElementsCount.getText().toString();
        String exerciseEquationCount = et_exerciseEquationCount.getText().toString();
        String exerciseNotes = et_exerciseNotes.getText().toString();


        Bitmap bitmap = getBitmapFromImageView(imageView);
        if (bitmap == null){
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circuit1);
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] img = byteArrayOutputStream.toByteArray();

            if (exerciseCode.isEmpty() || exerciseElementsCount.isEmpty() || exerciseEquationCount.isEmpty() || exerciseNotes.isEmpty()) {
//                et_exerciseCode.setError("Không được để trống");
//                tv_exerciseElementsCount.setError("Không được để trống");
//                tv_exerciseEquationCount.setError("Không được để trống");
//                tv_exerciseNotes.setError("Không được để trống");
                return;
            }
            mListExercise = ExercisesDatabase.getInstance(this).exercisesDAO().getAllExercises();
            Exercises exercise = new Exercises(size +  1,
                                                exerciseCode,
                                                uri,
                                                img,
                                                exerciseElementsCount,
                                                exerciseEquationCount,
                                                exerciseNotes
                                                );

            ExercisesDatabase.getInstance(this).exercisesDAO().insertExercise(exercise);

            Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
            et_exerciseCode.setText("");
            et_exerciseElementsCount.setText("");
            et_exerciseEquationCount.setText("");
            et_exerciseNotes.setText("");

            mListExercise = ExercisesDatabase.getInstance(this).exercisesDAO().getAllExercises();
            //recyclerAdapterExercise.setExercises(mListExercise);
            recyclerAdapterExercise = new RecyclerAdapterExercise(getContext(), mListExercise);
            exercisesBinding.recyclerView.setAdapter(recyclerAdapterExercise);
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // For .xlsx files
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            filePickerLauncher.launch(Intent.createChooser(intent, "Select an Excel File"));
        } catch (ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this.getContext(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    private void readExcelFromUri(Uri fileUri) {
        InputStream inputStream = null;
        Workbook workbook = null;
        try {
            inputStream = this.getContext().getContentResolver().openInputStream(fileUri);
            if (inputStream != null) {
                workbook = new XSSFWorkbook(inputStream); // For .xlsx files
                Sheet sheet = workbook.getSheetAt(0); // Get the first sheet
                List<List<String>> excelData = new ArrayList<>();
                for (Row row : sheet) {
                    List<String> rowData = new ArrayList<>();
                    for (Cell cell : row) {
                        switch (cell.getCellType()) {
                            case STRING:
                                rowData.add(cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    rowData.add(cell.getDateCellValue().toString());
                                } else {
                                    rowData.add(String.valueOf(cell.getNumericCellValue()));
                                }
                                break;
                            case BOOLEAN:
                                rowData.add(String.valueOf(cell.getBooleanCellValue()));
                                break;
                            case FORMULA:
                                rowData.add(cell.getCellFormula());
                                break;
//                            case DRAWING:
//                                rowData.add("DRAWING");
//                                break;
//                            case HYPERLINK:
//                                rowData.add("HYPERLINK");
//                                break;
                            default:
                                rowData.add("");
                        }
                    }
                    excelData.add(rowData);
                }
                // Process the data
                if (!excelData.isEmpty()) {
                    for (List<String> row : excelData) {
                        Log.d(TAG, "Row: " + row.toString());
                    }
                }

                //Xu ly hinh anh
                List<PictureData> lst = (List<PictureData>) workbook.getAllPictures();
                List<byte[]> imageBytesList = new ArrayList<>();
                for (PictureData pict : lst) {
                    byte[] data = pict.getData();
                    imageBytesList.add(data);
                }


            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading Excel file", e);
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error closing resources", e);
            }
        }
    }

    public static List<List<String>> readExcelFromAssets(Context context, String fileName) {
        List<List<String>> data = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        Workbook workbook = null;

        try {
            inputStream = assetManager.open(fileName);
            workbook = new XSSFWorkbook(inputStream); // For .xlsx files

            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet

            int rowNum = sheet.getLastRowNum();

            for (Row row : sheet) {
                List<String> rowData = new ArrayList<>();
                for (Cell cell : row) {
                    switch (cell.getCellType()) {
                        case STRING:
                            rowData.add(cell.getStringCellValue());
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                rowData.add(cell.getDateCellValue().toString());
                            } else {
                                rowData.add(String.valueOf(cell.getNumericCellValue()));
                            }
                            break;
                        case BOOLEAN:
                            rowData.add(String.valueOf(cell.getBooleanCellValue()));
                            break;
                        case FORMULA:
                            rowData.add(cell.getCellFormula());
                            break;
                        default:
                            rowData.add("");
                    }
                }
                data.add(rowData);
            }

        } catch (IOException e) {
            Log.e(TAG, "Error reading Excel file", e);
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error closing resources", e);
            }
        }

        return data;
    }

    boolean isNotHeader(Row row) {
        return !row.getCell(0).toString().toLowerCase().contains("name");
    }

    Pair<Integer, byte[]> toMapEntry(Picture picture) {
        byte[] data = picture.getPictureData().getData();
        ClientAnchor anchor = picture.getClientAnchor();
        return Pair.create(anchor.getRow1(), data);
    }

    static class Image {
        String fileName;
        byte[] bytes;

        Image(String fileName, byte[] bytes) {
            this.fileName = fileName;
            this.bytes = bytes;
        }
    }

    private void addExercise1() {
        int size = mListExercise.size();
        String uri = "https://bkas.vn/vinh.jpg";
        String exerciseCode = et_exerciseCode.getText().toString();

        String exerciseElementsCount = et_exerciseElementsCount.getText().toString();
        String exerciseEquationCount = et_exerciseEquationCount.getText().toString();
        String exerciseNotes = et_exerciseNotes.getText().toString();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circuit1);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] img = byteArrayOutputStream.toByteArray();

        if (exerciseCode.isEmpty() || exerciseElementsCount.isEmpty() || exerciseEquationCount.isEmpty() || exerciseNotes.isEmpty()) {
//                et_exerciseCode.setError("Không được để trống");
//                tv_exerciseElementsCount.setError("Không được để trống");
//                tv_exerciseEquationCount.setError("Không được để trống");
//                tv_exerciseNotes.setError("Không được để trống");
            return;
        }
        mListExercise = ExercisesDatabase.getInstance(this).exercisesDAO().getAllExercises();
        Exercises exercise = new Exercises(size +  1,
                exerciseCode,
                uri,
                img,
                exerciseElementsCount,
                exerciseEquationCount,
                exerciseNotes
        );

        ExercisesDatabase.getInstance(this).exercisesDAO().insertExercise(exercise);

        Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
        et_exerciseCode.setText("");
        et_exerciseElementsCount.setText("");
        et_exerciseEquationCount.setText("");
        et_exerciseNotes.setText("");

        mListExercise = ExercisesDatabase.getInstance(this).exercisesDAO().getAllExercises();
        //recyclerAdapterExercise.setExercises(mListExercise);
        recyclerAdapterExercise = new RecyclerAdapterExercise(getContext(), mListExercise);
        exercisesBinding.recyclerView.setAdapter(recyclerAdapterExercise);
    }

    private void readExcelFromUriToRoomSQLite(Uri fileUri) {
        InputStream inputStream = null;
        Workbook workbook = null;

        //Check existing database of Exercises
        mListExercise = ExercisesDatabase.getInstance(this).exercisesDAO().getAllExercises();
        int excerciseDbsize = mListExercise.size();

        try {
            inputStream = this.getContext().getContentResolver().openInputStream(fileUri);
            if (inputStream != null) {
                workbook = new XSSFWorkbook(inputStream); // For .xlsx files
                Sheet sheet = workbook.getSheetAt(0); // Get the first sheet
                //Xu ly hinh anh
                List<PictureData> picturelist = (List<PictureData>) workbook.getAllPictures();
                List<byte[]> imageBytesList = new ArrayList<>();

                for (PictureData pict : picturelist) {

//                    Bitmap bitmap = BitmapFactory.decodeByteArray(pict.getData(), 0, pict.getData().length);
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                    byte[] data = byteArrayOutputStream.toByteArray();
//                    imageBytesList.add(data);
//                    byteArrayOutputStream.close();

                    byte[] data = pict.getData();
                    imageBytesList.add(data);
                    Log.println(Log.ASSERT, "Image type: ", "" + pict.getPictureType());
                    Log.println(Log.ASSERT, "Image MIME type: ", "" + pict.getMimeType());
                    Log.println(Log.ASSERT, "Suggest Image type: ", "" + pict.suggestFileExtension());
                }

                int rowNum = sheet.getLastRowNum();//Số hàng dữ liệu - 1

                Log.println(Log.ASSERT, "So hang trong file excel: ", "" + rowNum);

                int colNum = sheet.getRow(0).getLastCellNum();//Số cột dữ liệu

                int picNum = imageBytesList.size();
                Log.println(Log.ASSERT, "So anh trong file excel: ", "" + picNum);

                //Lấy dữ liệu từ hàng thứ 2
                for (int rowIndex = 1; rowIndex <= rowNum; rowIndex++) {
                    Log.println(Log.ASSERT, "Row: ", "" + rowIndex);
                    Row row = sheet.getRow(rowIndex);
                    int exerciseIndex = excerciseDbsize + rowIndex;
                    String exerciseCode = getStringFromCell(row.getCell(1));
                    String pic_uri = fileUri + getStringFromCell(row.getCell(0));
                    String exerciseElementsCount = getStringFromCell(row.getCell(3));
                    String exerciseEquationCount = getStringFromCell(row.getCell(4));
                    String exerciseNotes = getStringFromCell(row.getCell(5));
                    byte[] circuitimg;
                    if (rowIndex <= picNum){
                        circuitimg = imageBytesList.get(rowIndex-1);
                    } else {
                        circuitimg = imageBytesList.get(picNum-1);
                    }
                    // Ghi Exercise vào database
                    Exercises exercise = new Exercises(exerciseIndex,
                                exerciseCode,
                                pic_uri,
                                circuitimg,
                                exerciseElementsCount,
                                exerciseEquationCount,
                                exerciseNotes);

                    ExercisesDatabase.getInstance(this).exercisesDAO().insertExercise(exercise);
                }
            }
        }
        catch (IOException e)
        {
            Log.e(TAG, "Error reading Excel file", e);
        }
        finally
        {
            try
            {
                if (workbook != null) {
                    workbook.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (IOException e)
            {
                Log.e(TAG, "Error closing resources", e);
            }
        }

        mListExercise = ExercisesDatabase.getInstance(this).exercisesDAO().getAllExercises();
        //recyclerAdapterExercise.setExercises(mListExercise);
        recyclerAdapterExercise = new RecyclerAdapterExercise(getContext(), mListExercise);
        exercisesBinding.recyclerView.setAdapter(recyclerAdapterExercise);
    }

    private void readExcelFromUriToRoomSQLite1(Uri fileUri) {
        InputStream inputStream = null;
        Workbook workbook = null;

        //Check existing database of Exercises
        mListExercise = ExercisesDatabase.getInstance(this).exercisesDAO().getAllExercises();
        int excerciseDbsize = mListExercise.size();

        try {
            inputStream = this.getContext().getContentResolver().openInputStream(fileUri);
            if (inputStream != null) {
                workbook = new XSSFWorkbook(inputStream); // For .xlsx files
                Sheet sheet = workbook.getSheetAt(0); // Get the first sheet
                //Xu ly hinh anh
                XSSFDrawing drawing = ((XSSFSheet)sheet).createDrawingPatriarch();

                // Get the shapes
                List<Picture> pictures = Collections.emptyList();
                List<byte[]> imageBytesList = new ArrayList<>();
                for (XSSFShape shape : drawing.getShapes()) {
                    if (shape instanceof Picture) {
                        XSSFPicture picture = (XSSFPicture) shape;
                        pictures.add(picture);
                        byte[] data = picture.getPictureData().getData();
                        imageBytesList.add(data);
                    }
                }

                int rowNum = sheet.getLastRowNum();//Số hàng dữ liệu - 1

                Log.println(Log.ASSERT, "So hang trong file excel: ", "" + rowNum);

                int colNum = sheet.getRow(0).getLastCellNum();//Số cột dữ liệu

                int picNum = imageBytesList.size();
                Log.println(Log.ASSERT, "So anh trong file excel: ", "" + picNum);

                //Lấy dữ liệu từ hàng thứ 2
                for (int rowIndex = 1; rowIndex <= rowNum; rowIndex++) {
                    Log.println(Log.ASSERT, "Row: ", "" + rowIndex);
                    Row row = sheet.getRow(rowIndex);
                    int exerciseIndex = excerciseDbsize + rowIndex;
                    String exerciseCode = getStringFromCell(row.getCell(1));
                    String pic_uri = fileUri + getStringFromCell(row.getCell(0));
                    String exerciseElementsCount = getStringFromCell(row.getCell(3));
                    String exerciseEquationCount = getStringFromCell(row.getCell(4));
                    String exerciseNotes = getStringFromCell(row.getCell(5));
                    byte[] circuitimg;
                    if (rowIndex <= picNum){
                        circuitimg = imageBytesList.get(rowIndex-1);
                    } else {
                        circuitimg = imageBytesList.get(picNum-1);
                    }
                    // Ghi Exercise vào database
                    Exercises exercise = new Exercises(exerciseIndex,
                            exerciseCode,
                            pic_uri,
                            circuitimg,
                            exerciseElementsCount,
                            exerciseEquationCount,
                            exerciseNotes);

                    ExercisesDatabase.getInstance(this).exercisesDAO().insertExercise(exercise);
                }
            }
        }
        catch (IOException e)
        {
            Log.e(TAG, "Error reading Excel file", e);
        }
        finally
        {
            try
            {
                if (workbook != null) {
                    workbook.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (IOException e)
            {
                Log.e(TAG, "Error closing resources", e);
            }
        }

        mListExercise = ExercisesDatabase.getInstance(this).exercisesDAO().getAllExercises();
        //recyclerAdapterExercise.setExercises(mListExercise);
        recyclerAdapterExercise = new RecyclerAdapterExercise(getContext(), mListExercise);
        exercisesBinding.recyclerView.setAdapter(recyclerAdapterExercise);
    }

    //Nếu image type là TIFF thì chuyển nó sang JPEG
    private void readExcelFromUriToRoomSQLite2(Uri fileUri) {
        InputStream inputStream = null;
        Workbook workbook = null;

        //Check existing database of Exercises
        mListExercise = ExercisesDatabase.getInstance(this).exercisesDAO().getAllExercises();
        int excerciseDbsize = mListExercise.size();

        try {
            inputStream = this.getContext().getContentResolver().openInputStream(fileUri);
            if (inputStream != null) {
                workbook = new XSSFWorkbook(inputStream); // For .xlsx files
                Sheet sheet = workbook.getSheetAt(0); // Get the first sheet
                //Xu ly hinh anh
                List<PictureData> picturelist = (List<PictureData>) workbook.getAllPictures();
                List<byte[]> imageBytesList = new ArrayList<>();

                for (PictureData pict : picturelist) {

                    byte[] data = pict.getData();
                    imageBytesList.add(data);
                    Log.println(Log.ASSERT, "Image type: ", "" + pict.getPictureType());
                    Log.println(Log.ASSERT, "Image MIME type: ", "" + pict.getMimeType());
                    Log.println(Log.ASSERT, "Suggest Image type: ", "" + pict.suggestFileExtension());


                }

                int rowNum = sheet.getLastRowNum();//Số hàng dữ liệu - 1

                Log.println(Log.ASSERT, "So hang trong file excel: ", "" + rowNum);

                int colNum = sheet.getRow(0).getLastCellNum();//Số cột dữ liệu

                int picNum = imageBytesList.size();
                Log.println(Log.ASSERT, "So anh trong file excel: ", "" + picNum);

                //Lấy dữ liệu từ hàng thứ 2
                for (int rowIndex = 1; rowIndex <= rowNum; rowIndex++) {
                    Log.println(Log.ASSERT, "Row: ", "" + rowIndex);
                    Row row = sheet.getRow(rowIndex);
                    int exerciseIndex = excerciseDbsize + rowIndex;
                    String exerciseCode = getStringFromCell(row.getCell(1));
                    String pic_uri = fileUri + getStringFromCell(row.getCell(0));
                    String exerciseElementsCount = getStringFromCell(row.getCell(3));
                    String exerciseEquationCount = getStringFromCell(row.getCell(4));
                    String exerciseNotes = getStringFromCell(row.getCell(5));
                    byte[] circuitimg;
                    if (rowIndex <= picNum){
                        circuitimg = imageBytesList.get(rowIndex-1);
                    } else {
                        circuitimg = imageBytesList.get(picNum-1);
                    }
                    // Ghi Exercise vào database
                    Exercises exercise = new Exercises(exerciseIndex,
                            exerciseCode,
                            pic_uri,
                            circuitimg,
                            exerciseElementsCount,
                            exerciseEquationCount,
                            exerciseNotes);

                    ExercisesDatabase.getInstance(this).exercisesDAO().insertExercise(exercise);
                }
            }
        }
        catch (IOException e)
        {
            Log.e(TAG, "Error reading Excel file", e);
        }
        finally
        {
            try
            {
                if (workbook != null) {
                    workbook.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (IOException e)
            {
                Log.e(TAG, "Error closing resources", e);
            }
        }

        mListExercise = ExercisesDatabase.getInstance(this).exercisesDAO().getAllExercises();
        //recyclerAdapterExercise.setExercises(mListExercise);
        recyclerAdapterExercise = new RecyclerAdapterExercise(getContext(), mListExercise);
        exercisesBinding.recyclerView.setAdapter(recyclerAdapterExercise);
    }

    private XSSFDrawing getDrawing(XSSFSheet sheet) {
        XSSFDrawing drawing = sheet.createDrawingPatriarch();

        // Get the shapes
        for (XSSFShape shape : drawing.getShapes()) {
            if (shape instanceof Picture) {
                XSSFPicture picture = (XSSFPicture)shape;

                ClientAnchor anchor = picture.getPreferredSize();

                System.out.println("Row1="+anchor.getRow1());
                System.out.println("Col1="+anchor.getCol1());
                System.out.println("Row2="+anchor.getRow2());
                System.out.println("Col2="+anchor.getCol2());
                System.out.println("Dx1="+anchor.getDx1());
                System.out.println("Dy1="+anchor.getDy1());
                System.out.println("Dx2="+anchor.getDx2());
                System.out.println("Dy2="+anchor.getDy2());

//                Point2D.Double p1 = new Point2D.Double(ssparser.calcXaxis(sheet,scale,anchor.getCol1(), anchor.getDx1()), ssparser.calcYaxis(xssfsht,scale,(short)anchor.getRow1(), anchor.getDy1()));
//                Point2D.Double p2 = new Point2D.Double(ssparser.calcXaxis(xssfsht,scale,anchor.getCol2(), anchor.getDx2()), ssparser.calcYaxis(xssfsht,scale,(short)anchor.getRow2(), anchor.getDy2()));
//
//                if(p1.x < 0 || p2.x < 0)
//                    return;
//                TableShape shp = new TableShape();
//                shp.setRow1(anchor.getRow1());
//                shp.setCol1(anchor.getCol1());
//                shp.setRow2(anchor.getRow2());
//                shp.setCol2(anchor.getCol2());
//                shp.setData(picture.getPictureData().getData());
//                shp.setP1(p1);
//                shp.setP2(p2);
//                shp.setType(ShapeType.IMAGE);
//                shp.setSubType(ImageType.PNG);
//                table.getShapeList().add(shp);
            }
        }

        return drawing;
    }

    private String getStringFromCell(Cell cell){
        String cellData="";
        switch (cell.getCellType()) {
            case STRING:
                cellData = cell.getStringCellValue();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellData = cell.getDateCellValue().toString();
                } else {
                    cellData = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                cellData = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:
                cellData = cell.getCellFormula();
                break;
            default:
                cellData = "";
        }
        return cellData;
    }

    public void imageClick(View view){
        Toast.makeText(getContext(), "Click image", Toast.LENGTH_SHORT).show();
    }

    private void launchPhotoPicker() {
        // Create an intent to pick an image from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        // Launch the photo picker using the registered ActivityResultLauncher
        pickMedia.launch(intent);
    }

    public static Bitmap getBitmapFromImageView(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();

        if (drawable == null) {
            // No image is set in the ImageView
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            // If the drawable is a BitmapDrawable, we can directly get the Bitmap
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            // If the drawable is not a BitmapDrawable (e.g., a VectorDrawable),
            // we need to create a Bitmap from it.
            // This part is more complex and depends on the type of drawable.
            // For simplicity, we'll return null here.
            // You might need to handle other drawable types differently.
            return null;
        }
    }

}