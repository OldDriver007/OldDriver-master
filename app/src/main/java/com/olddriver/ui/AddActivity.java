package com.olddriver.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.olddriver.R;
import com.olddriver.data.AVService;

import java.io.IOException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AddActivity extends Activity {

    private static final int IMAGE_PICK_REQUEST = 0;
    Context context;

    @BindView(R.id.editText)
    EditText editText;

    @BindView(R.id.image)
    ImageView imageView;

    @BindView(R.id.imageAction)
    Button imageAction;
    boolean haveImage = false;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);
        setButtonAndImage();
    }

    void setButtonAndImage() {
        imageView.setImageBitmap(bitmap);
        if (haveImage) {
            imageAction.setText(R.string.status_cancelImage);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageAction.setText(R.string.status_addImage);
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.send)
    void send() {
        String content = editText.getText().toString();
        if (TextUtils.isEmpty(content) == false || bitmap != null) {

            SaveCallback saveCallback=new SaveCallback() {
                @Override
                public void done(AVException e) {
                    // done方法一定在UI线程执行
                    if (e != null) {
                        Log.e("CreateTodo", "Update todo failed.", e);
                    }
               setResult(RESULT_OK);
                       finish();

                }
            };

            AVService.createOrUpdateTodo(content, bitmap, saveCallback);

        }
    }

    public static void pickImage(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, requestCode);
    }

    @OnClick(R.id.imageAction)
    void imageAction() {
        if (haveImage == false) {
            pickImage(this, IMAGE_PICK_REQUEST);
        } else {
            bitmap = null;
            haveImage = false;
            setButtonAndImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_REQUEST) {
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    haveImage = true;
                    setButtonAndImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
