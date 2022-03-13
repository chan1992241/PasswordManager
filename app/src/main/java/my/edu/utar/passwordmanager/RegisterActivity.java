package my.edu.utar.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Password Manager");
        mydb = new DBHelper(this);

        TextView backToLogin = (TextView) findViewById(R.id.backToLogin);
        Button registerSubmitBtn = (Button) findViewById(R.id.registerSubmitBtn);

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        registerSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText username = (EditText) findViewById(R.id.username);
                EditText password = (EditText) findViewById(R.id.password);
                EditText confirmPassword = (EditText) findViewById(R.id.confirmPassword);
                String inputUsername = username.getText().toString().trim();
                String inputPassword = password.getText().toString().trim();
                String inputConfirmPassword = confirmPassword.getText().toString().trim();
                Drawable usernameEditDrawable = username.getBackground();
                Drawable passwordEdtDrawable = password.getBackground();
                Drawable confirmPasswordEditDrawable = confirmPassword.getBackground();

                if (inputUsername.matches("") || inputPassword.matches("") ||inputConfirmPassword.matches("")){
                    if (inputUsername.matches("")) {
                        Toast.makeText(RegisterActivity.this, "Username Must Be Filled", Toast.LENGTH_SHORT).show();
                        usernameEditDrawable = DrawableCompat.wrap(usernameEditDrawable);
                        DrawableCompat.setTint(usernameEditDrawable, Color.RED);
                    }
                    if (inputPassword.matches("")) {
                        Toast.makeText(RegisterActivity.this, "Password Must Be Filled", Toast.LENGTH_SHORT).show();
                        passwordEdtDrawable = DrawableCompat.wrap(passwordEdtDrawable);
                        DrawableCompat.setTint(passwordEdtDrawable, Color.RED);
                    }
                    if (inputConfirmPassword.matches("")) {
                        Toast.makeText(RegisterActivity.this, "Confirm Your Password", Toast.LENGTH_SHORT).show();
                        confirmPasswordEditDrawable = DrawableCompat.wrap((confirmPasswordEditDrawable));
                        DrawableCompat.setTint(confirmPasswordEditDrawable, Color.RED);
                    }
                }else{
                    if (!inputConfirmPassword.equals(inputPassword)){
                        Toast.makeText(RegisterActivity.this, "Please Correct Your Password", Toast.LENGTH_LONG).show();
                        usernameEditDrawable = DrawableCompat.wrap(usernameEditDrawable);
                        DrawableCompat.setTint(usernameEditDrawable, Color.parseColor("#FF3700B3"));
                        passwordEdtDrawable = DrawableCompat.wrap(passwordEdtDrawable);
                        DrawableCompat.setTint(passwordEdtDrawable, Color.RED);
                        confirmPasswordEditDrawable = DrawableCompat.wrap((confirmPasswordEditDrawable));
                        DrawableCompat.setTint(confirmPasswordEditDrawable, Color.RED);
                    }else{
                        if (!mydb.registerUser(inputUsername, inputPassword)){
                            Toast.makeText(RegisterActivity.this, "This User Had Been Registered", Toast.LENGTH_LONG).show();
                            usernameEditDrawable = DrawableCompat.wrap(usernameEditDrawable);
                            DrawableCompat.setTint(usernameEditDrawable, Color.RED);
                        }else{
                            //Everything is correct
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                            intent.putExtra("username", inputUsername);
                            intent.putExtra("password", inputConfirmPassword);
                            startActivity(intent);
                        }
                    }
                }


            }
        });
    }
}