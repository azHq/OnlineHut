package com.ecommerce.onlinehut.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ecommerce.onlinehut.Animal;
import com.ecommerce.onlinehut.CustomAlertDialog;
import com.ecommerce.onlinehut.CustomPhotoGalleryActivity;
import com.ecommerce.onlinehut.EngToBanConverter;
import com.ecommerce.onlinehut.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditAnimalInfo extends AppCompatActivity {

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
    ArrayList<String[]> imagesPathList=new ArrayList<>();
    private int PICK_IMAGE_REQUEST = 1,PICK_IMAGE_MULTIPLE=2,VIDEO_REQUEST_CODE=3;
    public final int WRITE_PERMISSION=101;
    CircleImageView circleImageView;
    ProgressDialog progressDialog;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String user_id,animal_id="";
    RecycleAdapter recycleAdapter;
    RecyclerView recyclerView;
    private static String POPUP_CONSTANT = "mPopup";
    private static String POPUP_FORCE_SHOW_ICON = "setForceShowIcon";
    public int crop_or_change_id=-1;
    private byte[][] bytes_array;
    DocumentReference documentReference;
    VideoView videoView;
    MediaController mediaController;
    Button play_btn;
    public final long VIDEO_MAX_SIZE=50*1024*1024;
    Uri video_uri;
    Button submit_btn;
    TextView percentage,num_of_file;
    ProgressBar progressBar;
    LinearLayout progressBar_layout;
    boolean[] completed=new boolean[3];
    int count=0;
    Animal animal;
    public String temp_image_path="";
    File localFile = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_animal_info);
        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        animal_id=getIntent().getStringExtra("animal_id");
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
        db=FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        animal_name_et=findViewById(R.id.name);
        animal_price_et=findViewById(R.id.price);
        videoView=findViewById(R.id.video_view);
        play_btn=findViewById(R.id.play_btn);
        submit_btn=findViewById(R.id.submit);
        percentage=findViewById(R.id.percentage);
        num_of_file=findViewById(R.id.number_of_file);
        progressBar=findViewById(R.id.progress);
        progressBar_layout=findViewById(R.id.progress_layout);
        mediaController  = new MediaController(this);
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(video_path.length()>0){
                    videoView.start();
                    play_btn.setVisibility(View.GONE);
                }
                else  show_error_dialog(R.string.input_error,getString(R.string.first)+" "+getString(R.string.video_upload));
            }
        });
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
        String[] str={"1",""};
        imagesPathList.add(str);
        imagesPathList.add(str);
        imagesPathList.add(str);
        imagesPathList.add(str);
        recyclerView=findViewById(R.id.recycle);
        recycleAdapter=new RecycleAdapter(imagesPathList);
        recyclerView.setAdapter(recycleAdapter);
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

                    month=position-1;
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
        colors.add(getString(R.string.white));
        colors.add(getString(R.string.black));
        colors.add(getString(R.string.red));
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
        get_animal_data();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void get_animal_data(){
        progressDialog.show();
        DocumentReference documentReference=db.collection("AllAnimals").document(animal_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                imagesPathList.clear();
                DocumentSnapshot documentSnapshot=task.getResult();
                if(documentSnapshot.exists()){
                    Map<String,Object> map=documentSnapshot.getData();
                    String animal_id=map.get("animal_id").toString();
                    String user_id=map.get("user_id").toString();
                    String type=map.get("type").toString();
                    String name=map.get("name").toString();
                    int price=Integer.parseInt(map.get("price").toString());
                    float age=Integer.parseInt(map.get("age").toString());
                    String color=map.get("color").toString();
                    float weight=Float.parseFloat(map.get("weight").toString());
                    float height=Float.parseFloat(map.get("height").toString());
                    int teeth2=Integer.parseInt(map.get("teeth").toString());
                    String born=map.get("born").toString();
                    String compress_image_path=""; map.get("compress_image_path").toString();
                    String[] image_paths=map.get("original_image_path").toString().split(",");
                    String image_path=image_paths[0];
                    System.out.println("image path:"+image_path+" length:"+image_paths.length);
                    String video_path=map.get("video_path").toString();
                    int highest_bid=Integer.parseInt(map.get("highest_bid").toString());
                    int total_bid=Integer.parseInt(map.get("total_bid").toString());
                    String animal_alt_id=map.get("alternative_id").toString();
                    animal=new Animal(animal_id,animal_alt_id,user_id,type,name,price,age,color,weight,height,teeth2,born,image_path,compress_image_path,video_path,highest_bid,total_bid);

                    for(int i=0;i<image_paths.length;i++){

                        String[] str={"2",image_paths[i]};
                        imagesPathList.add(str);
                    }
                    recycleAdapter.notifyDataSetChanged();
                    int year=(int)(age/12);
                    int month=(int)(age%12);
                    animal_name_et.setText(animal.name);
                    animal_price_et.setText(animal.price+"");
                    year_sp.setSelection(years.indexOf(EngToBanConverter.getInstance().convert(year+"")+" "+getString(R.string.year)));
                    month_sp.setSelection(months.indexOf(EngToBanConverter.getInstance().convert(month+"")+" "+getString(R.string.month)));
                    weight_et.setText(animal.weight+"");
                    height_et.setText(animal.height+"");
                    born_et.setText(animal.born);
                    number_of_teeth_sp.setSelection(teeth.indexOf(EngToBanConverter.getInstance().convert(animal.teeth+"")));
                    animal_color_type.setSelection(colors.indexOf(animal.color));
                    animal_type_sp.setSelection(animal_types.indexOf(animal.animal_type));
                    if(video_path.length()>5){
                        videoView.setVideoPath(video_path);
                    }
                    mediaController.setAnchorView(videoView);
                    videoView.setMediaController(mediaController);
                    EditAnimalInfo.this.video_path=video_path;
                }
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {

        CustomAlertDialog.getInstance().show_exit_dialog(EditAnimalInfo.this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == PICK_IMAGE_MULTIPLE){
                imagesPathList.clear();
                String[] imagesPath = data.getStringExtra("data").split("\\|");

                try{
                    //lnrImages.removeAllViews();
                }catch (Throwable e){
                    e.printStackTrace();
                }
                for (int i=0;i<imagesPath.length;i++){
                    String[] str={"1",imagesPath[i]};
                    imagesPathList.add(str);
                }
                recycleAdapter.notifyDataSetChanged();
            }
            if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                imagesPathList.remove(crop_or_change_id);
                String[] str={"1",resultUri.getPath()};
                imagesPathList.add(crop_or_change_id,str);
                recycleAdapter.notifyDataSetChanged();

            }
            if(requestCode==PICK_IMAGE_REQUEST){

                if(data.getData()!=null){
                    imagesPathList.remove(crop_or_change_id);
                    String[] str={"1",getRealPathFromURI(data.getData())};
                    imagesPathList.add(crop_or_change_id, str);
                    recycleAdapter.notifyDataSetChanged();
                }

            }
            if(requestCode==VIDEO_REQUEST_CODE) {

                if (data.getData() != null) {
                    video_uri=data.getData();
                    video_path=data.getData().getPath();
                    videoView.setVideoURI(data.getData());
                    mediaController.setAnchorView(videoView);
                    videoView.setMediaController(mediaController);
                }
            }

        }

    }
    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null
                , MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewAdapter>{

        ArrayList<String[]> animals;
        public RecycleAdapter(ArrayList<String[]> animals){
            this.animals=animals;
        }
        public  class ViewAdapter extends RecyclerView.ViewHolder{

            View mView;
            Button option_menu;
            TextView image_name;
            RelativeLayout item;
            ImageView animal_image;
            public ViewAdapter(View itemView) {
                super(itemView);
                mView=itemView;
                animal_image=mView.findViewById(R.id.animal_image);
                image_name=mView.findViewById(R.id.image_name);
                item=mView.findViewById(R.id.item_layout);
                option_menu=mView.findViewById(R.id.option_btn);
            }


        }
        @NonNull
        @Override
        public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.animal_images2,parent,false);
            return new ViewAdapter(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewAdapter holder, final int position) {


            String[] image_path=animals.get(position);
            if(image_path[1].length()>0){
                holder.item.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_scale));
                holder.animal_image.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_transition_animation));

                if(image_path[0].equalsIgnoreCase("1")){

                    holder.animal_image.setImageBitmap(BitmapFactory.decodeFile(image_path[1]));
                }
                else{

                    Picasso.get().load(image_path[1]).into(holder.animal_image);
                }
                holder.image_name.setVisibility(View.GONE);
            }else{
                holder.image_name.setVisibility(View.VISIBLE);
                holder.image_name.setText("Image-"+(position+1));
            }

            holder.option_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(image_path[1].length()>0)  showPopup(v,position);
                    else{
                        Toast.makeText(getApplicationContext(),getString(R.string.image_upload),Toast.LENGTH_LONG).show();
                    }
                }
            });



        }

        @Override
        public int getItemCount() {
            return animals.size();
        }



    }
    public void showPopup(View view,int id) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.edit_option_menu, popup.getMenu());
        MenuCompat.setGroupDividerEnabled(popup.getMenu(), true);
        try {
            // Reflection apis to enforce show icon
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(POPUP_CONSTANT)) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(POPUP_FORCE_SHOW_ICON, boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                crop_or_change_id=id;
                if(item.getItemId()==R.id.change){

                    Intent galleryIntent=new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), PICK_IMAGE_REQUEST);
                }
                else if(item.getItemId()==R.id.crop){


                    try {
                        localFile = File.createTempFile("images", "jpg");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(imagesPathList.get(crop_or_change_id)[0].equalsIgnoreCase("2")&&imagesPathList.get(crop_or_change_id)[1].length()>5){
                        progressDialog.show();
                        StorageReference storageReference=FirebaseStorage.getInstance().getReferenceFromUrl(imagesPathList.get(crop_or_change_id)[1]);
                        storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                temp_image_path=localFile.getAbsolutePath();
                                Uri uri=Uri.fromFile(new File(temp_image_path));
                                CropImage.activity(uri)
                                        .start(EditAnimalInfo.this);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                progressDialog.dismiss();
                            }
                        });
                    }
                    else if(imagesPathList.get(crop_or_change_id)[1].length()>5){
                        Uri uri=Uri.fromFile(new File(imagesPathList.get(crop_or_change_id)[1]));
                        CropImage.activity(uri)
                                .start(EditAnimalInfo.this);
                    }



                }

                return true;
            }
        });
        popup.show();
    }

    private void openGallery() {

        Intent intent = new Intent(EditAnimalInfo.this, CustomPhotoGalleryActivity.class);
        startActivityForResult(intent,PICK_IMAGE_MULTIPLE);
    }
    public void upload_video(View view) {
        Intent galleryIntent=new Intent();
        play_btn.setVisibility(View.VISIBLE);
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("video/*");
        startActivityForResult(galleryIntent, VIDEO_REQUEST_CODE);
    }

    public byte[] compressImage1(Uri resultUri,int quality){

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            byte[] imageBytes = baos.toByteArray();
            return  imageBytes;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    public byte[] compressImage2(Uri resultUri,int quality){

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            byte[] imageBytes = baos.toByteArray();
            return  imageBytes;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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

        submit_btn.setClickable(false);
        name=animal_name_et.getText().toString();
        if(name.length()<=0){
            show_error_dialog(R.string.input_error,getString(R.string.animal_name)+" "+getString(R.string.write));

            return;
        }
        if(animal_type.length()<=0){
            show_error_dialog(R.string.input_error,getString(R.string.animal_type)+" "+getString(R.string.select));

            return;
        }
        price=EngToBanConverter.getInstance().convert_bangla_to_english(animal_price_et.getText().toString());
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
        if(tooth.length()<=0){

            show_error_dialog(R.string.input_error,getString(R.string.teeth)+" "+getString(R.string.select));
            return;
        }
        tooth=EngToBanConverter.getInstance().convert_bangla_to_english(tooth);
        weight=EngToBanConverter.getInstance().convert_bangla_to_english(weight_et.getText().toString());
        if(weight.length()<=0){
            show_error_dialog(R.string.input_error,getString(R.string.weight)+" "+getString(R.string.write));
            return;
        }
        height=EngToBanConverter.getInstance().convert_bangla_to_english(height_et.getText().toString());
        if(height.length()<=0){
            show_error_dialog(R.string.input_error,getString(R.string.height)+" "+getString(R.string.write));
            return;
        }

        born=born_et.getText().toString();
        if(born.length()<=0){
            show_error_dialog(R.string.input_error,getString(R.string.born)+" "+getString(R.string.write));
            return;
        }
        int count=0;
        bytes_array=new byte[imagesPathList.size()][];
        for(int i=0;i<imagesPathList.size();i++){
            Log.d("Edit:",imagesPathList.get(i)[1]);
            if(imagesPathList.get(i)[1].length()>0){
                count++;
            }
        }
        if(count<4||count>6){
            show_error_dialog(R.string.input_error,getString(R.string.minimum_image));
            return;
        }



       if(video_uri!=null){
           Cursor returnCursor =
                   getContentResolver().query(video_uri, null, null, null, null);
           int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
           returnCursor.moveToFirst();
           long video_size=returnCursor.getLong(sizeIndex);
           if(video_size>VIDEO_MAX_SIZE){
               show_error_dialog(R.string.input_error,getString(R.string.video_size));
               return;
           }
       }
       else {
           completed[2]=true;
       }

        documentReference= db.collection("AllAnimals").document(animal_id);
        age=year*12+month+"";
        age=EngToBanConverter.getInstance().convert_bangla_to_english(age);
        update_animal_info(user_id);


    }
    public void show_error_dialog(int title,String body){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.connection_error_layout,null);
        alert.setView(view);
        submit_btn.setClickable(true);
        progressDialog.dismiss();
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


    public void update_animal_info(String user_id){
        progressDialog.show();
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
        user.put("highest_bid", 0);
        user.put("total_bid", 0);
        user.put("create_at", FieldValue.serverTimestamp());
        documentReference.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                animal_name_et.setText("");
                animal_price_et.setText("");
                weight_et.setText("");
                height_et.setText("");
                born_et.setText("");
                submit_btn.setClickable(true);
                animal_type_sp.setSelection(0);
                animal_color_type.setSelection(0);
                number_of_teeth_sp.setSelection(0);
                upload_images();
            }
        });
    }
    public void upload_images(){

        //upload images
        for(int i=0;i<imagesPathList.size();i++){

           if(imagesPathList.get(i)[0].equalsIgnoreCase("1")){

               upload_image_to_firebase("original_image_path",Uri.fromFile(new File(imagesPathList.get(i)[1])),i);
           }
           else{
               count++;
               if(count>=imagesPathList.size()){

                   completed[0]=true;
                   if(completed[1]&&completed[2]){
                       finish();
                   }
               }
           }
        }
       if(imagesPathList.get(0)[0].equalsIgnoreCase("1")){
           byte[] compress_bytes=compressImage1(Uri.fromFile(new File(imagesPathList.get(0)[1])),70);
           upload_compress_image_to_firebase("compress_image_path",compress_bytes,imagesPathList.size());
       }
       else{
           completed[1]=true;
       }

        if(video_uri!=null) upload_video_to_firebase("video_path",video_uri,0);
        if(completed[0]&&completed[1]&&completed[2]) finish();
    }

    public void upload_compress_image_to_firebase(String key,byte[] bytes,int index){
        StorageReference ref= storageReference.child( "Animal_Pictures/"+ animal_id+index);
        ref.putBytes(bytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                System.out.println("compress image uploaded");
                ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        Uri uri=task.getResult();
                        update(key,uri.toString());
                        completed[1]=true;
                        if(completed[0]&&completed[2]){

                            finish();
                        }

                    }
                });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),getString(R.string.connection_problem),Toast.LENGTH_LONG).show();
                    }
                });
    }
    public void get_image_path(String image_path,int index){
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isComplete()){


                    DocumentSnapshot documentSnapshot=task.getResult();
                    if(documentSnapshot.exists()){
                        Map<String,Object> map=documentSnapshot.getData();
                        String[] image_paths=map.get("original_image_path").toString().split(",");
                        image_paths[index]=image_path;
                        String path="";
                        for(int i=0;i<image_paths.length;i++){

                            if(i<image_paths.length-1) path+=image_paths[i]+",";
                            else path+=image_paths[i]+",";
                        }
                        update("original_image_path",path);

                    }
                    else{
                        update("original_image_path",image_path);
                    }
                    count++;
                    if(count>=imagesPathList.size()){

                        completed[0]=true;
                        if(completed[1]&&completed[2]){
                            finish();
                        }
                    }
                }
                else{

                }
            }
        });
    }
    public void upload_image_to_firebase(String key,Uri image_uri,int index){
        StorageReference ref= storageReference.child( "Animal_Pictures/"+ animal_id+index);
        ref.putFile(image_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        Uri uri=task.getResult();
                        if(uri!=null) get_image_path(uri.toString(),index);
                    }
                });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),getString(R.string.connection_problem),Toast.LENGTH_LONG).show();
                    }
                });
    }
    public void upload_video_to_firebase(String key,Uri video_uri,int index){

        progressBar_layout.setVisibility(View.VISIBLE);
        StorageReference ref= storageReference.child( "Animal_Videos/"+ animal_id);
        ref.putFile(video_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        submit_btn.setClickable(true);
                        video_path="";
                        videoView.setVideoURI(null);
                        recycleAdapter.notifyDataSetChanged();
                        progressBar_layout.setVisibility(View.GONE);
                        Uri uri=task.getResult();
                        update(key,uri.toString());
                        completed[2]=true;
                        if(completed[0]&&completed[1]){
                            finish();
                        }

                    }
                });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),getString(R.string.connection_problem),Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                show_progress(taskSnapshot);

            }
        });
    }
    public void show_progress(UploadTask.TaskSnapshot taskSnapshot){
        float uploaded_size= taskSnapshot.getBytesTransferred();
        float file_size=taskSnapshot.getTotalByteCount();
        int percent=(int)((uploaded_size/file_size)*100);
        num_of_file.setText(percent+"/"+100);
        progressBar.setProgress(percent);
        percentage.setText(percent+"%");
    }
    public void update(String key,String value){

        Map<String, Object> user = new HashMap<>();
        user.put(key, value);
        documentReference.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


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
