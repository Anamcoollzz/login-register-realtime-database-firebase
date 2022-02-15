package com.example.logindong.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.logindong.Constants;
import com.example.logindong.R;
import com.example.logindong.data.model.User;
import com.example.logindong.ui.login.LoginViewModel;
import com.example.logindong.ui.login.LoginViewModelFactory;
import com.example.logindong.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private ArrayList<User> users;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(this);
        Log.e("SINIDONG", "HAHAHAHA");
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef
//                    .child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        users = new ArrayList<User>();
//                    Log.e("ERRORHE", dataSnapshot.getChildren().get);
                        for (DataSnapshot mDataSnapshot : dataSnapshot.getChildren()) {
                            User barang = mDataSnapshot.getValue(User.class);
//                        barang.setId(mDataSnapshot.getKey());
                            users.add(barang);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Toast.makeText(LoginActivity.this,
                                databaseError.getDetails() + " " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }

                });
//            myRef.child("users").setValue("ANAMKUN");
        Log.e("SINIDONG", "HIHIHIHI");
//            myRef.child("barang").setValue("Hello, World!").addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    Log.e("SUCCESSMESSAGE", "");
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.e("ERRORMESSAGE2", e.getMessage());
//                }
//            });


        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText nameEditText = binding.name;
        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final Button registerButton = binding.register;
        final ProgressBar loadingProgressBar = binding.loading;
        final TextView toLoginTextView = binding.loginTo;
        final TextView toRegisterTextView = binding.registerTo;

        int sizeInDP = 96;
        int marginInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizeInDP, getResources().getDisplayMetrics());
        int margin10InDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        ViewGroup.MarginLayoutParams m = (ViewGroup.MarginLayoutParams) usernameEditText.getLayoutParams();
        m.setMargins(0, marginInDp, 0, 0);
        usernameEditText.requestLayout();

        toRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEditText.setVisibility(View.VISIBLE);
//                usernameEditText
                loginButton.setVisibility(View.GONE);
                registerButton.setVisibility(View.VISIBLE);
                toLoginTextView.setVisibility(View.VISIBLE);
                toRegisterTextView.setVisibility(View.GONE);
                ViewGroup.MarginLayoutParams m = (ViewGroup.MarginLayoutParams) usernameEditText.getLayoutParams();
                m.setMargins(0, margin10InDp, 0, 0);
                usernameEditText.requestLayout();
            }
        });
        toLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEditText.setVisibility(View.GONE);
//                usernameEditText
                loginButton.setVisibility(View.VISIBLE);
                registerButton.setVisibility(View.GONE);
                toLoginTextView.setVisibility(View.GONE);
                toRegisterTextView.setVisibility(View.VISIBLE);
                ViewGroup.MarginLayoutParams m = (ViewGroup.MarginLayoutParams) usernameEditText.getLayoutParams();
                m.setMargins(0, marginInDp, 0, 0);
                usernameEditText.requestLayout();
            }
        });


        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadingProgressBar.setVisibility(View.VISIBLE);
//                loginViewModel.login(usernameEditText.getText().toString(),
//                        passwordEditText.getText().toString());
                boolean ketemu = false;
                int i=0;
                String email = "";
                String myPass = "";
                String name = "";
                for (i = 0; i < users.size(); i++) {
                    email = users.get(i).getEmail();
                    myPass = users.get(i).getPassword();
                    name = users.get(i).getName();
                    boolean condition = email.equals(usernameEditText.getText().toString()) && myPass.equals(passwordEditText.getText().toString());
                    if (condition) {
                        ketemu = true;
                        break;
                    }
                }
                if (ketemu) {
                    Toast.makeText(LoginActivity.this, "User ditemukan", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, LoggedInActivity.class);
                    intent.putExtra(Constants.NAME, name);
                    intent.putExtra(Constants.EMAIL, email);
                    intent.putExtra(Constants.PASSWORD, myPass);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "User tidak ditemukan", Toast.LENGTH_LONG).show();
                }
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String email = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                loadingProgressBar.setVisibility(View.VISIBLE);
//                loginViewModel.login(usernameEditText.getText().toString(),
//                        passwordEditText.getText().toString());

                User newUser = new User(name, email, password);
                myRef.push().setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(LoginActivity.this, "Register successfully", Toast.LENGTH_LONG).show();
                        toLoginTextView.performClick();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Register failed", Toast.LENGTH_LONG).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        });


    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}