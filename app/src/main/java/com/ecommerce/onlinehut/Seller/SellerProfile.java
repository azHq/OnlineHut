package com.ecommerce.onlinehut.Seller;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.onlinehut.Buyer.BuyerDashboard;
import com.ecommerce.onlinehut.EngToBanConverter;
import com.ecommerce.onlinehut.R;
import com.ecommerce.onlinehut.SharedPrefManager;
import com.ecommerce.onlinehut.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

public class SellerProfile extends Fragment {

    TextView name_tv, email_tv, phone_number_tv, address_tv;
    TextView total_item_tv, total_sold_item_tv, total_unsold_item_tv, total_sell_tv;
    Button edit_name, edit_address, edit_phone_number, edit_email;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String user_id = "";
    ProgressDialog progressDialog;
    EngToBanConverter engToBanConverter;
    AlertDialog alertDialog;
    String image_path = "";
    CircleImageView profile_picture;
    private int PICK_IMAGE_REQUEST = 1;
    public final int WRITE_PERMISSION = 101;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_profile, container, false);
        engToBanConverter = EngToBanConverter.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getContext());
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        progressDialog.setMessage("Please Wait...");
        if (firebaseAuth.getCurrentUser() != null) user_id = firebaseAuth.getCurrentUser().getUid();

        db = FirebaseFirestore.getInstance();
        name_tv = view.findViewById(R.id.name);
        email_tv = view.findViewById(R.id.email);
        phone_number_tv = view.findViewById(R.id.phone_number);
        profile_picture = view.findViewById(R.id.profile_picture);
        address_tv = view.findViewById(R.id.address);
        total_item_tv = view.findViewById(R.id.total_item);
        total_sold_item_tv = view.findViewById(R.id.total_sold_item);
        total_unsold_item_tv = view.findViewById(R.id.total_unsold_item);
        total_sell_tv = view.findViewById(R.id.total_sell);
        edit_name = view.findViewById(R.id.edit_name);
        edit_email = view.findViewById(R.id.edit_email);
        edit_phone_number = view.findViewById(R.id.edit_phone_number);
        edit_address = view.findViewById(R.id.edit_address);
        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_profile_picture_change_dialog();
            }
        });
        edit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_edit_dialog(R.string.name_change, R.string.name, "user_name");
            }
        });
        edit_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_edit_dialog(R.string.email_change, R.string.email, "email");
            }
        });
        edit_phone_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_edit_dialog(R.string.phone_number_change, R.string.phone_number, "phone_number");
            }
        });
        edit_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_edit_dialog(R.string.address_change, R.string.address, "address");
            }
        });

        get_user_data();
        return view;
    }

    public void show_profile_picture_change_dialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.profile_picture_change_layout, null);
        alert.setView(view);
        alertDialog = alert.show();
        Button submit = view.findViewById(R.id.yes);
        Button cancel = view.findViewById(R.id.no);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                requestPermission();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    public void show_edit_dialog(int title, int hint, String key) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.edit_panel, null);
        alert.setView(view);
        alertDialog = alert.show();
        ;
        Button submit = view.findViewById(R.id.submit);
        Button cancel = view.findViewById(R.id.cancel);
        TextView title_tv = view.findViewById(R.id.title);
        title_tv.setText(title);
        EditText input = view.findViewById(R.id.input);
        input.setHint(hint);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = input.getText().toString();
                if (value.length() > 0) {
                    alertDialog.dismiss();
                    progressDialog.show();
                    update(key, value);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION);
            } else {
                openGallery();
            }
        } else {
            openGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            Toast.makeText(getContext(), "Please Give Permission To Upload Image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .start(getActivity(), this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri resultUri = result.getUri();
            byte[] bytes = compressImage1(resultUri);

            if (bytes != null) {
                upload_image_to_firebase(bytes);


            } else {
                Toast.makeText(getContext(), "Fail to Upload Image", Toast.LENGTH_LONG).show();
            }


        }


    }

    public void upload_image_to_firebase(byte[] bytes) {
        progressDialog.show();
        StorageReference ref = storageReference.child("Profile_Picture/" + user_id);
        ref.putBytes(bytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        Uri uri = task.getResult();
                        update("image_path", uri.toString());
                    }
                });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), getString(R.string.connection_problem), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void openGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 1);


    }

    public byte[] compressImage1(Uri resultUri) {

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), resultUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] imageBytes = baos.toByteArray();
            return imageBytes;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public byte[] compressImage2(Uri resultUri) {

        File filePathForCompress = new File(resultUri.getPath());
        Bitmap compressBitmap = null;
        try {
            compressBitmap = new Compressor(getContext())
                    .setMaxHeight(200)
                    .setMaxWidth(200)
                    .setQuality(50)
                    .compressToBitmap(filePathForCompress);

        } catch (IOException e) {

            e.printStackTrace();
        }

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        compressBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArray);

        final byte[] thumByte = byteArray.toByteArray();
        return thumByte;
    }


    public void update(String key, String value) {
        DocumentReference documentReference = db.collection("Users").document(user_id);
        Map<String, Object> user = new HashMap<>();
        user.put(key, value);

        documentReference.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                get_user_data();

            }
        });
    }

    public void get_user_data() {

        progressDialog.show();
        DocumentReference documentReference = db.collection("Users").document(user_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    progressDialog.dismiss();
                    if (document.exists()) {

                        Map<String, Object> map = document.getData();
                        if (map.containsKey("user_name")) {
                            name_tv.setText(map.get("user_name").toString());
                        }
                        if (map.containsKey("email") && map.get("email") != null && map.get("email").toString().length() > 0) {
                            email_tv.setText(map.get("email").toString());
                        }
                        if (map.containsKey("phone_number") && map.get("email") != null && map.get("phone_number").toString().length() > 10) {
                            phone_number_tv.setText(map.get("phone_number").toString());
                        }
                        if (map.containsKey("address") && map.get("address") != null && map.get("address").toString().length() > 0) {
                            address_tv.setText(map.get("address").toString());
                        }
                        if (map.containsKey("image_path") && map.get("image_path") != null) {
                            image_path = map.get("image_path").toString();
                            if (image_path.length() > 5) {
                                Picasso.get().load(image_path).into(profile_picture);
                            }
                        }

                        if (map.containsKey("total_item") && map.get("total_item") != null) {

                            String total_item = map.get("total_item").toString();
                            total_item_tv.setText(engToBanConverter.convert(total_item) + " টি");
                        } else {
                            String total_item = 0 + "";
                            total_item_tv.setText(engToBanConverter.convert(total_item) + " টি");
                        }
                        if (map.containsKey("total_sold_item") && map.get("total_sold_item") != null) {

                            String total_item = map.get("total_sold_item").toString();
                            total_sold_item_tv.setText(engToBanConverter.convert(total_item) + " টি");
                        } else {
                            String total_item = 0 + "";
                            total_sold_item_tv.setText(engToBanConverter.convert(total_item) + " টি");
                        }
                        if (map.containsKey("total_unsold_item") && map.get("total_unsold_item") != null) {

                            String total_item = map.get("total_unsold_item").toString();
                            total_unsold_item_tv.setText(engToBanConverter.convert(total_item) + " টি");
                        } else {

                            String total_item = 0 + "";
                            total_unsold_item_tv.setText(engToBanConverter.convert(total_item) + " টি");
                        }
                        if (map.containsKey("total_sell")) {

                            String total_item = map.get("total_sell").toString();
                            total_sell_tv.setText(engToBanConverter.convert(total_item) + " টি");
                        } else {

                            String total_item = 0 + "";
                            total_sell_tv.setText(engToBanConverter.convert(total_item) + " টি");
                        }


                    }
                } else {
                    Toast.makeText(getContext(), R.string.connection_problem, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
