package vn.bkas.classman.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DatabaseExporter {
    private static final String TAG = "DatabaseExporter";

    public static void exportDatabaseToExcel(Context context, String databaseName, String tableName, String excelFileName) {
        // Get the database file path
        File dbFile = context.getDatabasePath(databaseName);
        if (!dbFile.exists()) {
            Log.e(TAG, "Database file does not exist: " + dbFile.getAbsolutePath());
            return;
        }

        // Open the database
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        if (db == null) {
            Log.e(TAG, "Failed to open database: " + dbFile.getAbsolutePath());
            return;
        }

        // Create a new Excel workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(tableName);
        sheet.setColumnWidth(3, 5000); // Set column width

        try {
            // Query all data from the table
            Cursor cursor = db.query(tableName, null, null, null, null, null, null);
            if (cursor == null) {
                Log.e(TAG, "Failed to query table: " + tableName);
                return;
            }

            // Write the column headers
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(cursor.getColumnName(i));
            }

            // Write the data rows
            int rowNum = 1;
            while (cursor.moveToNext()) {
                Row row = sheet.createRow(rowNum++);
                row.setHeightInPoints(100); // Set row he
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    Cell cell = row.createCell(i);

                    switch (cursor.getType(i)) {
                        case Cursor.FIELD_TYPE_INTEGER:
                            cell.setCellValue(cursor.getInt(i));
                            break;
                        case Cursor.FIELD_TYPE_FLOAT:
                            cell.setCellValue(cursor.getDouble(i));
                            break;
                        case Cursor.FIELD_TYPE_STRING:
                            cell.setCellValue(cursor.getString(i));
                            break;
                        case Cursor.FIELD_TYPE_BLOB:
                            // Handle BLOB data (e.g., images) if needed
                            //cell.setCellValue("BLOB Data");

                            // Handle BLOB data (e.g., images)
                            byte[] blobData = cursor.getBlob(i);
                            if (blobData != null && blobData.length > 0) {
                                // Try to decode as Bitmap
                                Bitmap bitmap = BitmapFactory.decodeByteArray(blobData, 0, blobData.length);
                                if (bitmap != null) {
                                    // Add image to excel
                                    addImageToExcel(workbook, sheet, bitmap, rowNum - 1, i);
                                } else {
                                    cell.setCellValue("BLOB Data (Not an Image)");
                                }
                            } else {
                                cell.setCellValue("Empty BLOB");
                            }
                            break;
                        case Cursor.FIELD_TYPE_NULL:
                            cell.setCellValue("");
                            break;
                    }
                }
            }

            // Close the cursor
            cursor.close();

            // Save the Excel file
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File excelFile = new File(downloadsDir, excelFileName + ".xlsx");
            FileOutputStream fileOutputStream = new FileOutputStream(excelFile);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            Log.d(TAG, "Excel file saved to: " + excelFile.getAbsolutePath());

        } catch (IOException e) {
            Log.e(TAG, "Error exporting database to Excel", e);
        } finally {
            // Close the database
            if (db != null && db.isOpen()) {
                db.close();
            }
            try {
                workbook.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing workbook", e);
            }
        }
    }

    private static void addImageToExcel(Workbook workbook, Sheet sheet, Bitmap bitmap, int rowNum, int colNum) throws IOException {
        // Convert Bitmap to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        Log.d(TAG, "Image size: " + imageBytes.length + " bytes");

        // Add picture data to workbook
        int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
        byteArrayOutputStream.close();
        Log.d(TAG, "Picture index: " + pictureIdx);

        // Create drawing patriarch
        //CreationHelper helper = workbook.getCreationHelper();
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        Log.d(TAG, "Drawing: " + drawing);

        // Create an anchor
        //ClientAnchor anchor = helper.createClientAnchor();
        XSSFClientAnchor anchor = new XSSFClientAnchor();
        anchor.setCol1(colNum);
        anchor.setRow1(rowNum);
        anchor.setCol2(colNum+1);
        anchor.setRow2(rowNum+1);
        Log.d(TAG, "Anchor: " + anchor);

        // Creates a picture
        Picture picture = drawing.createPicture(anchor, pictureIdx);
        Log.d(TAG, "Picture: " + picture);

        // Resize image
        //picture.resize();
    }
}
