package com.example.friends.fitfriend;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterWithEmailAndPwActivity extends AppCompatActivity {

    private EditText Email;
    private EditText Password;
    private EditText PasswordConfirm;
    private FirebaseAuth mAuth;
    private User user;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeremailpw);

        mAuth = FirebaseAuth.getInstance();

        Email = (EditText)findViewById(R.id.emailEdit);
        Password = (EditText)findViewById(R.id.newPasswordEdit);
        PasswordConfirm = (EditText)findViewById(R.id.newPasswordConfirmEdit);

        mDatabase = FirebaseDatabase.getInstance().getReference();


    }

    @Override
    public void onBackPressed() {
        Log.d("Back is pressed", "HELLOOOOOOOOO");
        Intent regView = new Intent(RegisterWithEmailAndPwActivity.this, RegisterActivity.class);
        startActivity(regView);
        RegisterWithEmailAndPwActivity.this.finish();
    }

    public void createNewAccountClick(View v){
        final String email = Email.getText().toString();
        String password = Password.getText().toString();
        String pwconfirm = PasswordConfirm.getText().toString();


        CheckRequired check = new CheckRequired(email, password, pwconfirm);
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
                                Intent mainIntent = new Intent(RegisterWithEmailAndPwActivity.this, LoginActivity.class);
                                mainIntent.putExtra("Email", Email.getText().toString());
                                mainIntent.putExtra("Password", Password.getText().toString());
                                //TODO: Make buttons for under
                                String name = "Thang Phan";
                                String birthday = "12/11/1993";
                                String gender = "Male";

                                //Add user to database here.
                                FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
                                user = new User(name, email, birthday, gender, userF.getUid());
                                EmailConverter emailCon = new EmailConverter(email);
                                String validEmail = emailCon.getEmail();
                                mDatabase.child("users").child(validEmail).setValue(user);
                                //Database done.

                                Toast.makeText(RegisterWithEmailAndPwActivity.this, "Account created, login!", Toast.LENGTH_SHORT).show();
                                RegisterWithEmailAndPwActivity.this.startActivity(mainIntent);
                                RegisterWithEmailAndPwActivity.this.finish();
                            }else {
                                Toast.makeText(RegisterWithEmailAndPwActivity.this, "Failed to create user.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });//Finish created user
                } else{
                    Toast.makeText(this, "Account not created", Toast.LENGTH_SHORT).show();
                }
        }
    }
}
