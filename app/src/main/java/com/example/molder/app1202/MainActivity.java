package com.example.molder.app1202;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private ImageView imageView;
    private Button btTakePictureLarge, btPickPicture;
    private File file;
    private static final int REQUEST_TAKE_PICTURE_LARGE = 0;
    private static final int REQUEST_PICK_PICTURE = 1;

    private final static String TAG = "GeocoderActivity";
    private GoogleMap map;
    private Geocoder geocoder;
    private LatLng taroko;
    private Marker marker_taroko;
    private Bitmap headimage;
    private double x,y;
    private String text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        geocoder = new Geocoder(this);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void findViews() {
        imageView = findViewById(R.id.ivPicture);
        btTakePictureLarge = findViewById(R.id.btnTakePicture);
        btPickPicture = findViewById(R.id.btnPickPicture);
    }

    public void onTakePictureLargeClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //打開外部儲存目錄
//        file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        file = getExternalFilesDir(MediaStore.Images.Media.DATA);
        //建立照片檔案名稱
//        file = new File(MediaStore.Images.Media.DATA,"picture.jpg");
//        file = new File(file, "picture.jpg");
        String strImage = Environment.getExternalStorageDirectory().getAbsolutePath()+"/picture.jpg";

         file = new File(strImage);
//        //將照片先存在暫存位置(getPackageName() + ".provider")之後系統會將照片轉移到實體位置儲存(file)
//        Uri contentUri = FileProvider.getUriForFile(
//                this, getPackageName() + ".provider", file);
        Uri contentUri = FileProvider.getUriForFile(
                this, getPackageName() + ".provider", file);
//        Uri contentUri = Uri.fromFile(file);
        //照片儲存
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);





//        String strImage = Environment.getExternalStorageDirectory().getAbsolutePath()+"/mypicture.jpg";
//        File myImage = new File(strImage);
//        Uri uriMyImage = Uri.fromFile(myImage);
//
//        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uriMyImage);















            //檢查是不是有人支援照相功能,有就開始照相
        if (isIntentAvailable(this, intent)) {
            startActivityForResult(intent, REQUEST_TAKE_PICTURE_LARGE);
//            intent = new Intent(Intent.ACTION_PICK,
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            startActivityForResult(intent, REQUEST_PICK_PICTURE);
        } else {
            Toast.makeText(this, R.string.textNoCameraAppsFound,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onPickPictureClick(View view) {
        //指定圖庫
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_PICTURE);
    }

    public boolean isIntentAvailable(Context context, Intent intent) {
        //尋求系統層級
        PackageManager packageManager = context.getPackageManager();
        //提供預設照相功能
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //如果拍照完按下打勾
        if (resultCode == RESULT_OK) {
            int newSize = 512;
            //判斷你是用哪種模式
            switch (requestCode) {
                case REQUEST_TAKE_PICTURE_LARGE:
                    Bitmap srcPicture = BitmapFactory.decodeFile(file.getPath());
                    Bitmap downsizedPicture = Common.downSize(srcPicture, newSize);
                    imageView.setImageBitmap(downsizedPicture);
                    headimage = downsizedPicture;
                    break;
                case REQUEST_PICK_PICTURE:
                    //取得uri table 裡面欄位的data 裡面的值(存著照片路徑)
                    Uri uri = intent.getData();
                    if (uri != null) {
                        //照片路徑
                        String[] columns = {MediaStore.Images.Media.DATA};
                        //收尋欄位 SQLine
                        Cursor cursor = getContentResolver().query(uri, columns,
                                null, null, null);
                        //move = 移動指標從data to path照片路徑
                        if (cursor != null && cursor.moveToFirst()) {
                            //取出照片 指定欄位
                            String imagePath = cursor.getString(0);
                            cursor.close();
                            Bitmap srcImage = BitmapFactory.decodeFile(imagePath);
                            Bitmap downsizedImage = Common.downSize(srcImage, newSize);
                            imageView.setImageBitmap(downsizedImage);
                            headimage = downsizedImage;
                        }
                    }
                    break;
            }
        }
    }





    public void onSubmitClick(View view) {
        EditText etLocationName = findViewById(R.id.etAddress);
        String locationName = etLocationName.getText().toString().trim();
        text = locationName;
        if (locationName.length() > 0) {
            locationNameToMarker(locationName);
        } else {
            showToast(R.string.msg_LocationNameIsEmpty);
        }
    }

    private void locationNameToMarker(String locationName) {
        map.clear();
        List<Address> addressList = null;
        //收尋上限
        int maxResults = 1;
        try {
            //將使用者輸入的地址轉成物件
            addressList = geocoder
                    .getFromLocationName(locationName, maxResults);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        if (addressList == null || addressList.isEmpty()) {
            showToast(R.string.msg_LocationNameNotFound);
        } else {
            //收尋多筆要用for ench
            Address address = addressList.get(0);

            // 取得緯度經度 （將地址轉成緯度經度）
            LatLng position = new LatLng(address.getLatitude(),
                    address.getLongitude());
            x = address.getLatitude();
            y = address.getLongitude();
            String snippet = address.getAddressLine(0);
            //將以上資訊存到物件裡
            MarkerOptions markerOptions = new MarkerOptions().position(position)
                    .title(locationName).snippet(snippet);
            //在地圖上打圖標
            addMarker(markerOptions);
        }
    }

    private void addMarker(MarkerOptions markerOptions) {
//        map.addMarker(markerOptions);
        //以圖標為中心將畫面移動
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(markerOptions.getPosition()).zoom(15).build();
        map.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        taroko = new LatLng(x, y);
        marker_taroko = map.addMarker(new MarkerOptions()
                .position(taroko)
                .title(text)
                .snippet(text)
                .icon(BitmapDescriptorFactory.fromBitmap(headimage)));
    }

    private void showToast(int messageResId) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }





    @Override
    protected void onStart() {
        super.onStart();
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Common.askPermissions(this, permissions, Common.REQ_EXTERNAL_STORAGE);
        askPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Common.REQ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    btTakePictureLarge.setEnabled(true);
                    btPickPicture.setEnabled(true);
                } else {
                    btTakePictureLarge.setEnabled(false);
                    btPickPicture.setEnabled(false);
                }
                break;
        }
    }






    private static final int REQ_PERMISSIONS = 0;

    // New Permission see Appendix A
    private void askPermissions() {
        //因為是群組授權，所以請求ACCESS_COARSE_LOCATION就等同於請求ACCESS_FINE_LOCATION，因為同屬於LOCATION群組
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        Set<String> permissionsRequest = new HashSet<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsRequest.add(permission);
            }
        }

        if (!permissionsRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    REQ_PERMISSIONS);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        setUpMap();
    }

    private void setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
        map.getUiSettings().setZoomControlsEnabled(true);

    }

}
