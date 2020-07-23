package com.justice.shopmanagerfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextInputLayout nameInput;
    private TextInputLayout priceInput;
    private Button submitBtn;

    private Uri photo = null;

    private Product product = new Product();

    private ProgressDialog progressDialog;

    private boolean update = false;

    ////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        if (ApplicationClass.product != null) {
            update = true;
        }

        initWidgets();
        if (update) {
            setDefaultValues();
        }
        setOnClickListeners();
    }

    private void setDefaultValues() {


        Glide.with(this).load(ApplicationClass.product.getUrl()).into(imageView);
        nameInput.getEditText().setText(ApplicationClass.product.getName());
        priceInput.getEditText().setText("" + ApplicationClass.product.getPrice());

        photo = Uri.parse(ApplicationClass.product.getUrl());


    }

    private void setOnClickListeners() {
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitBtnTapped();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoto();
            }
        });
    }

    private void choosePhoto() {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                photo = result.getUri();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        Glide.with(this).applyDefaultRequestOptions(requestOptions).load(photo).into(imageView);

    }


    private void submitBtnTapped() {
        String name = nameInput.getEditText().getText().toString().trim();
        String price = priceInput.getEditText().getText().toString().trim();

        if (photo == null || name.isEmpty() || price.isEmpty()) {
            Snackbar.make(submitBtn, "please fill all fields", Snackbar.LENGTH_LONG).show();

            return;
        }

        if (update) {
            sendDataToDatabaseUpdate(name, price);
        } else {
            sendDataToDatabase(name, price);

        }


    }

    private void sendDataToDatabaseUpdate(String name, String price) {
        if (update) {
            ApplicationClass.product.setName(name);
            ApplicationClass.product.setPrice(Integer.parseInt(price));
            ApplicationClass.product.setUrl(photo.toString());

        }

        if (ApplicationClass.product.getUrl().equals(photo.toString())) {
            product.setUrl(ApplicationClass.product.getUrl());
            saveDataInFireStore(name, price);

        } else {

            sendDataToDatabase(name, price);
        }


    }

    private void sendDataToDatabase(final String name, final String price) {


        progressDialog.show();
        final StorageReference ref;

        if (update) {
            ref = FirebaseStorage.getInstance().getReferenceFromUrl(ApplicationClass.product.getUrl());

        } else {
            ref = FirebaseStorage.getInstance().getReference("product").child(UUID.randomUUID().toString());

        }


        UploadTask uploadTask = ref.putFile(photo);


        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    product.setUrl(downloadUri.toString());
                    Snackbar.make(submitBtn, "photo Uploaded", Snackbar.LENGTH_LONG).show();

                    saveDataInFireStore(name, price);

                } else {
                    String error = task.getException().getMessage();
                    Snackbar.make(submitBtn, "Error: " + error, Snackbar.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });

        resetEdtTxt();
    }

    private void resetEdtTxt() {

        nameInput.getEditText().setText(null);
        priceInput.getEditText().setText(null);

    }

    private void saveDataInFireStore(String name, String price) {
        product.setName(name);
        product.setPrice(Integer.parseInt(price));


        if (update) {

            progressDialog.setTitle("Update");
            progressDialog.setMessage("updating " + product.getName() + "...");
            progressDialog.show();

            FirebaseFirestore.getInstance().collection("product").document(ApplicationClass.product.getId()).set(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Snackbar.make(submitBtn, "product Updated", Snackbar.LENGTH_LONG).show();

                    } else {
                        Snackbar.make(submitBtn, "Error: " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();

                    }
                    progressDialog.dismiss();
                }
            });
        } else {
            progressDialog.setTitle("Adding");
            progressDialog.setMessage("adding " + product.getName() + "...");
            progressDialog.show();
            FirebaseFirestore.getInstance().collection("product").add(product).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        Snackbar.make(submitBtn, "product Uploaded", Snackbar.LENGTH_LONG).show();

                    } else {
                        Snackbar.make(submitBtn, "Error: " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();

                    }
                    progressDialog.dismiss();
                }

            });
        }


    }

    @Override
    public void onBackPressed() {
        ApplicationClass.product = null;
        super.onBackPressed();
    }

    private void initWidgets() {
        imageView = findViewById(R.id.imageView);
        nameInput = findViewById(R.id.nameInput);
        priceInput = findViewById(R.id.priceInput);
        submitBtn = findViewById(R.id.submitBtn);
        progressDialog = new ProgressDialog(this);
    }
}
