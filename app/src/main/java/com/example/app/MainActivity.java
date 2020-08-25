package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private String MIME_TYPE = "application/vnd.android.package-archive";
    private String PROVIDER_PATH = ".provider";
    private String APP_INSTALL_PATH = "\"application/vnd.android.package-archive\"";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.hello);



    }

    private void download(){
        String filefolder = getFilesDir().getAbsolutePath();
        try {
            File devfile = new File(filefolder + "/app-release1.apk");
            if (!devfile.exists()) {
                copyFileTo(this, "launcher-release.apk", devfile.getAbsolutePath());
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(
                        this,
                        BuildConfig.APPLICATION_ID + PROVIDER_PATH,
                        devfile
                );
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                install.setData(contentUri);
                startActivity(install);
                // finish()
            } else {
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                install.setDataAndType(
                        Uri.fromFile(devfile),
                        APP_INSTALL_PATH
                );
                startActivity(install);
                // finish()
            }
        } catch (Exception e){
            Log.d("FILE ERROR", e.toString());
        }
    }
    private  boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    public boolean copyFileTo(Context c, String orifile,
                                     String desfile) throws IOException {
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(desfile);
        myInput = c.getAssets().open(orifile);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }

        myOutput.flush();
        myInput.close();
        myOutput.close();

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPackageInstalled("com.DefaultCompany.StartProject",getPackageManager())){
            button.setText("Launch");
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPackageInstalled("com.DefaultCompany.StartProject",getPackageManager())){
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.DefaultCompany.StartProject", "com.DefaultCompany.StartProject.MainActivity"));
                        startActivity(intent);
                    }
                }
            });
        } else {
            button.setText("Download and Install");
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    download();
                }
            });

        }

    }
}
