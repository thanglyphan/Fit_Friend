package com.example.friends.fitfriend;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FacebookRegActivity extends AppCompatActivity {
    EditText nameView;
    EditText emailView;
    EditText birthdayView;
    EditText passwordView;
    EditText passwordConfirmedView;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_facebook_reg);

        mAuth = FirebaseAuth.getInstance();

        Log.d("FACEBOOK ACTIVITY", "LOOOOOOOOOOL");
        nameView = (EditText)findViewById(R.id.facebook_name_text);
        emailView = (EditText)findViewById(R.id.facebook_email);
        birthdayView = (EditText)findViewById(R.id.faceboook_birthday);
        passwordView = (EditText)findViewById(R.id.facebook_password);
        passwordConfirmedView = (EditText)findViewById(R.id.facebook_password_confirmed);

        Bundle inBundle = getIntent().getExtras();
        String name = inBundle.get("name").toString();
        String surname = inBundle.get("surname").toString();
        String email = inBundle.get("email").toString();
        String birthday = inBundle.get("birthday").toString();

        nameView.setText("" + name + " " + surname);
        emailView.setText("" + email);
        birthdayView.setText("" + birthday);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LoginManager.getInstance().logOut();
        Intent goBackToReg = new Intent(FacebookRegActivity.this, RegisterActivity.class);
        mAuth.signOut();
        startActivity(goBackToReg);
        this.finish();
    }


    public void createAccount(View view){
        String name = nameView.getText().toString();
        final String email = emailView.getText().toString();
        String birthday = birthdayView.getText().toString();
        String password = passwordView.getText().toString();
        String passwordConfirmed = passwordConfirmedView.getText().toString();


        CheckRequired check = new CheckRequired(email, password, passwordConfirmed);
        switch (check.recquired()){
            case "email": Toast.makeText(this, "Email is not valid", Toast.LENGTH_SHORT).show();break;
            case "password": Toast.makeText(this, "Password minimum 6 characters", Toast.LENGTH_SHORT).show();break;
            case "pwconfirm": Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();break;
            default:
                if(check.checkRequiredFields() && check.checkPwConfirmed()){
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("LOL", "createUserWithEmail:onComplete:" + task.isSuccessful());
                            if(task.isSuccessful()){
                                LoginManager.getInstance().logOut();
                                Intent mainIntent = new Intent(FacebookRegActivity.this, LoginActivity.class);
                                mainIntent.putExtra("Email", emailView.getText().toString());
                                mainIntent.putExtra("Password", passwordView.getText().toString());

                                Toast.makeText(FacebookRegActivity.this, "Account created, login!", Toast.LENGTH_SHORT).show();


                                startActivity(mainIntent);
                                FacebookRegActivity.this.finish();
                            }else {
                                LoginManager.getInstance().logOut();
                                Toast.makeText(FacebookRegActivity.this, "Email adresse " + email + " has been taken.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });//Finish created user
                } else{
                    Toast.makeText(this, "Account not created", Toast.LENGTH_SHORT).show();
                }
        }

    }
}
