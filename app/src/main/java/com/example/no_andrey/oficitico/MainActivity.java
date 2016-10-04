package com.example.no_andrey.oficitico;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private DBHelper database;
    private Uri mImageCaptureUri;
    private ImageView profileImageView;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private  AlertDialog dialog;
    private  AlertDialog.Builder builder;
    private String mCurrentPhotoPath;
    private SharedPreferences sh;

//TODO: Resolver el problema de mantener el contexto en landscape y portrait
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String[] items = new String[]{"Cámara", "Memoria"};
        builder = new AlertDialog.Builder(this);
        sh = getSharedPreferences("andrey", Activity.MODE_PRIVATE);

        profileImageView = (ImageView) findViewById(R.id.imageProfileBtn); //where image profile will show



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
        builder.setTitle("Seleccione una imagen");
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int item ) {
                if (item == 0) {

                    Intent intent    =  getIntent();

                    try {
                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.cancel();
                } else {
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        } );




    }


    public void takeImage(View view)
    {
        dialog =  builder.create();
        dialog.show();
    }

    protected void onSaveInstanceState(Bundle icicle)
    {
        super.onSaveInstanceState(icicle);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }


    public  Intent getIntent()
    {
        Intent intent    = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager()) != null)
        {
            File photoFile = null;
            try
            {
                photoFile = createImageFile();
            }
            catch (IOException e)
            {

            }

            if(photoFile != null)
            {
                mImageCaptureUri = FileProvider.getUriForFile(this, "com.example.no_andrey.oficitico",photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile);
            }

        }

        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) return;

        mImageCaptureUri = data.getData();
        mCurrentPhotoPath = getRealPathFromURI(mImageCaptureUri);
        rotateImage(BitmapFactory.decodeFile(mCurrentPhotoPath));
    }

    /**
    private Bitmap getImageBitmap()
    {

        Bitmap bitmap = null;

        if (mCurrentPhotoPath == null)
            mCurrentPhotoPath = mImageCaptureUri.getPath(); //from File Manager

        if (mCurrentPhotoPath != null)
        {
            int targetW = profileImageView.getWidth();
            int targetH = profileImageView.getHeight();

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
             bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;


            bitmap =  BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        }


        return bitmap;
    }

     **/



    private void rotateImage(Bitmap bitmap)
    {
        ExifInterface exifInterface = null;

        try
        {
            exifInterface = new ExifInterface(mCurrentPhotoPath);
        }catch (IOException e)
        {

        }

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();

        switch (orientation)
        {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270);
                break;
            default:
        }

        Bitmap rbitmap = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        profileImageView.setImageBitmap(rbitmap);
    }



    public String getRealPathFromURI(Uri contentUri) {
        String [] proj      = {MediaStore.Images.Media.DATA};
        Cursor cursor       = managedQuery( contentUri, proj, null, null,null);

        if (cursor == null) return null;

        int column_index    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);
    }


    public void onPause()
    {
        super.onPause();

        SharedPreferences.Editor ed = sh.edit();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void secondActivityInformation(View view)
    {
        database = new DBHelper(this);
        long row;
        EditText name = (EditText) findViewById(R.id.editTextName);
        EditText last_name = (EditText) findViewById(R.id.editTextLastName);
        EditText phone = (EditText) findViewById(R.id.editTextPhone);
        EditText cellphone = (EditText) findViewById(R.id.editTextCellPhone);
        EditText address = (EditText) findViewById(R.id.editTextCellPhone);

        row = database.insertPersonalInformation(name.getText().toString(),last_name.getText().toString(), phone.getText().toString(), cellphone.getText().toString(), address.getText().toString(), mCurrentPhotoPath);

        if(row != -1) //if no error on insert method
        {
            Intent nextActivityInformation = new Intent(this, SecondActivityInformation.class);
            nextActivityInformation.putExtra("newPersonRow", row);
            startActivity(nextActivityInformation);
        }


    }

}
