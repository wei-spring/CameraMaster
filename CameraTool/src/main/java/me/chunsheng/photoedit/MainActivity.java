package me.chunsheng.photoedit;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.umeng.message.PushAgent;

import java.io.File;
import java.util.Set;

import ly.img.android.ui.activities.CameraPreviewActivity;
import ly.img.android.ui.activities.CameraPreviewIntent;
import ly.img.android.ui.activities.PhotoEditorIntent;
import ly.img.android.ui.utilities.PermissionRequest;

public class MainActivity extends FragmentActivity implements PermissionRequest.Response {

    private static final String FOLDER = "ImgLy";
    public static int CAMERA_PREVIEW_RESULT = 1;

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        String Url = "";
        Bundle bun = getIntent().getExtras();
        if (bun != null) {
            Set<String> keySet = bun.keySet();
            for (String WebUrl : keySet) {
                Url = bun.getString(WebUrl);
            }
        }

        Toast.makeText(this,"URL:"+ Url,Toast.LENGTH_SHORT).show();
        if (Url != "") {
            startActivity(new Intent(this, WebviewActivity.class).putExtra("WebUrl", Url));
        } else {
            new CameraPreviewIntent(this)
                    .setExportDir(CameraPreviewIntent.Directory.DCIM, FOLDER)
                    .setExportPrefix("img_")
                    .setEditorIntent(
                            new PhotoEditorIntent(this)
                                    .setExportDir(PhotoEditorIntent.Directory.DCIM, FOLDER)
                                    .setExportPrefix("result_")
                                    .destroySourceAfterSave(true)
                    )
                    .startActivityForResult(CAMERA_PREVIEW_RESULT);
        }

        PushAgent.getInstance(this).onAppStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_PREVIEW_RESULT) {
            final String path = data.getStringExtra(CameraPreviewActivity.RESULT_IMAGE_PATH);

            // Toast.makeText(this, "图片保存在: " + path, Toast.LENGTH_LONG).show();

            File mMediaFolder = new File(path);

            setContentView(R.layout.activity_main);
            TextView fullscreen_content = (TextView) findViewById(R.id.fullscreen_location);
            fullscreen_content.setText("图片保存在: " + path);

            final TextView share = (TextView) findViewById(R.id.fullscreen_content);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getUri() != null)
                        share("哈哈,我用相机大师拍照,编辑的图片,你也来试试啊.很不错哦", getUri());
                }
            });

            final ImageView fullscreen_pic = (ImageView) findViewById(R.id.fullscreen_pic);


            MediaScannerConnection.scanFile(this, new String[]{mMediaFolder.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, final Uri uri) {
                            setUri(uri);
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fullscreen_pic.setImageURI(uri);

                                    final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(MainActivity.this);

                                    dialogBuilder
                                            .withTitle("相机大师")                                  //.withTitle(null)  no title
                                            .withTitleColor("#FFFFFF")                                  //def
                                            .withDividerColor("#FFf50057")                              //def
                                            .withMessage("难道你忍心不与朋友分享你的照片吗？嘻嘻")                     //.withMessage(null)  no Msg
                                            .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                                            .withDialogColor("#CCff80ab")                               //def  | withDialogColor(int resid)
                                            .withIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                                            .withDuration(700)                                          //def
                                            .withButton1Text("乐意分享")                                      //def gone
                                            .withButton2Text("残忍拒绝")                                  //def gone
                                            .isCancelableOnTouchOutside(false)                          //def    | isCancelabe(true)
                                            .setButton1Click(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (uri != null)
                                                        share("哈哈,我用相机大师拍照,编辑的图片,你也来试试啊.很不错哦", uri);
                                                }
                                            })
                                            .setButton2Click(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialogBuilder.dismiss();
                                                }
                                            })
                                            .show();
                                }
                            });
                        }
                    }
            );
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void permissionGranted() {

    }

    @Override
    public void permissionDenied() {
        Toast.makeText(this, "你还没有授权相机大师拍照权限,请去设置里面,权限管理进行设置", Toast.LENGTH_LONG).show();
    }

    private void share(String content, Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (uri != null) { //uri 是图片的地址
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
            //当用户选择短信时使用sms_body取得文字
            shareIntent.putExtra("sms_body", content);
        } else {
            shareIntent.setType("text/plain");
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        //自定义选择框的标题
        startActivity(Intent.createChooser(shareIntent, "分享图片啦,秀一下吧>_<"));
        //系统默认标题
//        startActivity(shareIntent);
    }

}
