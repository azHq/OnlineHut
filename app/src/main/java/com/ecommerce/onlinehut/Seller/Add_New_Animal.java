package com.ecommerce.onlinehut.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.onlinehut.Buyer.BuyerDashboard;
import com.ecommerce.onlinehut.EngToBanConverter;
import com.ecommerce.onlinehut.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class Add_New_Animal extends AppCompatActivity {

    EditText animal_name_et,animal_price_et,age_et,weight_et,height_et,teeth_et,born_et;
    String name,price,age,color,weight,height,tooth,born,compress_image_path="",original_image_path="",video_path="",animal_type="";
    AlertDialog alertDialog;
    Spinner animal_color_type,animal_type_sp,number_of_teeth_sp,year_sp,month_sp;
    int year=1,month=1;
    ArrayList<String> animal_types=new ArrayList<>();
    ArrayList<String> colors=new ArrayList<>();
    ArrayList<String> teeth=new ArrayList<>();
    ArrayList<String> years=new ArrayList<>();
    ArrayList<String> months=new ArrayList<>();
    private int PICK_IMAGE_REQUEST = 1;
    public final int WRITE_PERMISSION=101;
    CircleImageView circleImageView;
    ProgressDialog progressDialog;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__new__animal);
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        db=FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        animal_name_et=findViewById(R.id.name);
        animal_price_et=findViewById(R.id.price);
        age_et=findViewById(R.id.age);
        born_et=findViewById(R.id.born);
        animal_type_sp=findViewById(R.id.animal_type);
        year_sp=findViewById(R.id.year);
        month_sp=findViewById(R.id.month);
        animal_type_sp=findViewById(R.id.animal_type);
        animal_color_type=findViewById(R.id.animal_color_type);
        number_of_teeth_sp=findViewById(R.id.number_of_teeth);
        weight_et=findViewById(R.id.weight);
        height_et=findViewById(R.id.height);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");

        years.add(getString(R.string.year));
        years.add(EngToBanConverter.getInstance().convert("1 ")+getString(R.string.year));
        years.add(EngToBanConverter.getInstance().convert("2 ")+getString(R.string.year));
        years.add(EngToBanConverter.getInstance().convert("3 ")+getString(R.string.year));
        years.add(EngToBanConverter.getInstance().convert("4 ")+getString(R.string.year));
        years.add(EngToBanConverter.getInstance().convert("5 ")+getString(R.string.year));
        years.add(EngToBanConverter.getInstance().convert("6 ")+getString(R.string.year));
        years.add(EngToBanConverter.getInstance().convert("7 ")+getString(R.string.year));
        years.add(EngToBanConverter.getInstance().convert("8 ")+getString(R.string.year));
        years.add(EngToBanConverter.getInstance().convert("9 ")+getString(R.string.year));
        years.add(EngToBanConverter.getInstance().convert("10 ")+getString(R.string.year));
        year_sp.setAdapter(new CustomAdapter(getApplicationContext(),0,years));
        year_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position>0){

                    year=position;
                }
                else{
                    year=0;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        months.add(getString(R.string.month));
        months.add(EngToBanConverter.getInstance().convert("0 ")+getString(R.string.month));
        months.add(EngToBanConverter.getInstance().convert("1 ")+getString(R.string.month));
        months.add(EngToBanConverter.getInstance().convert("2 ")+getString(R.string.month));
        months.add(EngToBanConverter.getInstance().convert("3 ")+getString(R.string.month));
        months.add(EngToBanConverter.getInstance().convert("4 ")+getString(R.string.month));
        months.add(EngToBanConverter.getInstance().convert("5 ")+getString(R.string.month));
        months.add(EngToBanConverter.getInstance().convert("6 ")+getString(R.string.month));
        months.add(EngToBanConverter.getInstance().convert("7 ")+getString(R.string.month));
        months.add(EngToBanConverter.getInstance().convert("8 ")+getString(R.string.month));
        months.add(EngToBanConverter.getInstance().convert("9 ")+getString(R.string.month));
        months.add(EngToBanConverter.getInstance().convert("10 ")+getString(R.string.month));
        months.add(EngToBanConverter.getInstance().convert("11 ")+getString(R.string.month));
        month_sp.setAdapter(new CustomAdapter(getApplicationContext(),0,months));
        month_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position>0){

                    month=position;
                }
                else{

                    month=0;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        animal_types.add(getString(R.string.animal_type));
        animal_types.add(getString(R.string.cow));
        animal_types.add(getString(R.string.buffalo));
        animal_types.add(getString(R.string.camel));
        animal_types.add(getString(R.string.dumba));
        animal_types.add(getString(R.string.goat));
        animal_types.add(getString(R.string.sheep));
        animal_type_sp.setAdapter(new CustomAdapter(getApplicationContext(),0,animal_types));
        animal_type_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position>0){

                    animal_type=animal_types.get(position);
                }
                else{
                    animal_type="";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        colors.add(getString(R.string.color));
        colors.add(getString(R.string.red));
        colors.add(getString(R.string.black));
        colors.add(getString(R.string.brown));
        colors.add(getString(R.string.gray));
        colors.add(getString(R.string.mixed));
        animal_color_type.setAdapter(new CustomAdapter(getApplicationContext(),0,colors));
        animal_color_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position>0){
                    color=colors.get(position);
                }
                else{
                    color="";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        teeth.add(getString(R.string.teeth));
        teeth.add(EngToBanConverter.getInstance().convert("1"));
        teeth.add(EngToBanConverter.getInstance().convert("2"));
        teeth.add(EngToBanConverter.getInstance().convert("3"));
        teeth.add(EngToBanConverter.getInstance().convert("4"));
        teeth.add(EngToBanConverter.getInstance().convert("5"));
        teeth.add(EngToBanConverter.getInstance().convert("6"));
        number_of_teeth_sp.setAdapter(new CustomAdapter(getApplicationContext(),0,teeth));
        number_of_teeth_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position>0){
                    tooth=teeth.get(position);
                }
                else{
                    tooth="";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void requestPermission() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION);
            } else {
                openGallery();
            }
        }
        else{
            openGallery();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        }
        else{
            Toast.makeText(getApplicationContext(),"Please Give Permission To Upload Image",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {

            Uri imageUri=data.getData();
            CropImage.activity(imageUri)
                    .start(this);


        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode == RESULT_OK){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri resultUri = result.getUri();
            byte[] bytes=compressImage1(resultUri);

            if(bytes!=null){
                String image = getStringImage(bytes);
                //progressDialog.show();

            }
            else {
                Toast.makeText(getApplicationContext(),"Fail to Upload Image",Toast.LENGTH_LONG).show();
            }



        }


    }
    private void openGallery() {

        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,1);



    }

    public byte[] compressImage1(Uri resultUri){

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] imageBytes = baos.toByteArray();
            return  imageBytes;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    public byte[] compressImage2(Uri resultUri){

        File filePathForCompress=new File(resultUri.getPath());
        Bitmap compressBitmap=null;
        try{
            compressBitmap=new Compressor(getApplicationContext())
                    .setMaxHeight(200)
                    .setMaxWidth(200)
                    .setQuality(50)
                    .compressToBitmap(filePathForCompress);

        }catch(IOException e){

            e.printStackTrace();
        }

        ByteArrayOutputStream byteArray=new ByteArrayOutputStream();
        compressBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArray);

        final byte[] thumByte=byteArray.toByteArray();
        return  thumByte;
    }
    public String getStringImage(byte[] imageBytes) {

        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }
    public void add_image(View view){
        requestPermission();
    }
    public void add_video(View view){
        requestPermission();
    }
    public void submit(View view){

        name=animal_name_et.getText().toString();
        if(name.length()<=0){
            show_error_dialog(R.string.input_error,getString(R.string.animal_name)+" "+getString(R.string.write));
            return;
        }
        if(animal_type.length()<=0){
            show_error_dialog(R.string.input_error,getString(R.string.animal_type)+" "+getString(R.string.select));
            return;
        }
        price=animal_price_et.getText().toString();
        if(price.length()<=0){
            show_error_dialog(R.string.input_error,getString(R.string.animal_price)+" "+getString(R.string.write));
            return;
        }
        if(year<=0){
            show_error_dialog(R.string.input_error,getString(R.string.year)+" "+getString(R.string.select));
            return;
        }
        if(color.length()<=0){
            show_error_dialog(R.string.input_error,getString(R.string.animal_name)+" "+getString(R.string.select));
            return;
        }
        weight=weight_et.getText().toString();
        if(weight.length()<=0){
            show_error_dialog(R.string.input_error,getString(R.string.weight)+" "+getString(R.string.write));
            return;
        }
        height=height_et.getText().toString();
        if(height.length()<=0){
            show_error_dialog(R.string.input_error,getString(R.string.height)+" "+getString(R.string.write));
            return;
        }

        born=born_et.getText().toString();
        if(born.length()<=0){
            show_error_dialog(R.string.input_error,getString(R.string.born)+" "+getString(R.string.write));
            return;
        }
        age=year*12+month+"";
        upload_animal_info(user_id);

    }

    public void upload_animal_info(String user_id){
        progressDialog.show();
        DocumentReference documentReference= db.collection("AllAnimals").document();
        String animal_id=documentReference.getId();
        Map<String, Object> user = new HashMap<>();
        user.put("user_id", user_id);
        user.put("animal_id",animal_id);
        user.put("type", animal_type);
        user.put("name", name);
        user.put("price", price);
        user.put("age", age);
        user.put("color", color);
        user.put("teeth", tooth);
        user.put("weight", weight);
        user.put("height", height);
        user.put("born", born);
        user.put("compress_image_path",compress_image_path);
        user.put("original_image_path",original_image_path);
        user.put("video_path",video_path);
        user.put("create_at", FieldValue.serverTimestamp());
        documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                animal_name_et.setText("");
                animal_price_et.setText("");
                age_et.setText("");
                weight_et.setText("");
                height_et.setText("");
                born_et.setText("");
                animal_type_sp.setSelection(0);
                animal_color_type.setSelection(0);
                number_of_teeth_sp.setSelection(0);
            }
        });
    }
    public void update_animal_info(String user_id,String document_id){
        progressDialog.show();
        DocumentReference documentReference= db.collection("AllAnimals").document(document_id);
        Map<String, Object> user = new HashMap<>();
        user.put("user_id", user_id);
        user.put("type", animal_type);
        user.put("name", name);
        user.put("price", price);
        user.put("age", age);
        user.put("color", color);
        user.put("teeth", tooth);
        user.put("weight", weight);
        user.put("height", height);
        user.put("born", born);
        user.put("create_at", FieldValue.serverTimestamp());
        documentReference.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
            }
        });
    }
    public void show_error_dialog(int title,String body){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.connection_error_layout,null);
        alert.setView(view);
        alertDialog=alert.show();;
        Button btn=view.findViewById(R.id.ok);
        TextView title_tv=view.findViewById(R.id.title);
        title_tv.setText(title);
        TextView body_tv=view.findViewById(R.id.body);
        body_tv.setText(body);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }



    public static class CustomAdapter extends BaseAdapter {
        Context context;
        ArrayList<String> user_types;
        LayoutInflater inflter;
        int flag;

        public CustomAdapter(Context applicationContext, int flag, ArrayList<String> user_types) {
            this.context = applicationContext;
            this.flag = flag;
            this.user_types = user_types;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return user_types.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.animal_type_layout, null);
            final TextView names =view.findViewById(R.id.user_type);
            names.setText(user_types.get(i));



            return view;
        }
    }
}
