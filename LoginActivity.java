package com.example.raghavsinghrajput.digitalshopping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.raghavsinghrajput.digitalshopping.Model.Users;
import com.example.raghavsinghrajput.digitalshopping.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.widget.Toast.makeText;
import static com.example.raghavsinghrajput.digitalshopping.R.id.not_admin_panel_link;

public class LoginActivity extends AppCompatActivity {
    private EditText InputPhoneNumber,InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private TextView AdminLink,NotAdminLink;

    private String parentDbName ="Users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        LoginButton=(Button) findViewById(R.id.login_btn);
        InputPassword=(EditText) findViewById(R.id.login_password_input);
        InputPhoneNumber=(EditText) findViewById(R.id.login_phone_number_input);
        AdminLink =(TextView) findViewById(R.id.admin_panel_link);
        NotAdminLink =(TextView) findViewById(R.id.not_admin_panel_link);
        loadingBar=new ProgressDialog(this);
        AdminLink.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbName="Admins";
            }
        });
        NotAdminLink.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName="Users";
            }
        }));
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginUser();
            }
            private void LoginUser()
            {

                String phone = InputPhoneNumber.getText().toString();
                String  password= InputPassword.getText().toString();
                if (TextUtils.isEmpty(phone))
                {
                    Toast.makeText(LoginActivity.this, "Please enter your Phone", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(password))
                {
                    Toast.makeText(LoginActivity.this, "Please enter your Password", Toast.LENGTH_SHORT).show();
                }

                {
                    loadingBar.setTitle("Login Account");
                    loadingBar.setMessage("Plase wait , While we are Checking the Credentials.");
                   // loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();


                    AllowAccessToAccount(phone,password);
                }
            }
            private void AllowAccessToAccount(final String phone , final String password)
            {
                final DatabaseReference RootRef;
                RootRef=FirebaseDatabase.getInstance().getReference();

                RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(parentDbName).child(phone).exists())
                        {
                             Users usersData =dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                            if (usersData.getPhone().equals(phone))
                            {
                                if (usersData.getPassword().equals(password))
                                {
                                   if(parentDbName.equals("Admins"))
                                    {
                                        Toast.makeText(LoginActivity.this, "Welcome Admin you are Logged in Sucessfully...", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent=new Intent(LoginActivity.this, AdminCategoryActivity.class);

                                        startActivity(intent);
                                    }
                                    else if (parentDbName.equals("Users"))
                                    {
                                        Toast.makeText(LoginActivity.this, "Login Sucessfully...", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent=new Intent(LoginActivity.this, HomeActivity.class);
                                        Prevalent.currentOnlineUser=usersData;
                                        startActivity(intent);
                                    }
                                }
                            }
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Account with this" +phone +"number do not exists", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "You need to create New Account", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }
}
