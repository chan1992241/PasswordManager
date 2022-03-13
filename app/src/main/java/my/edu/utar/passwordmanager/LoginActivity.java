package my.edu.utar.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    DBHelper mydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getSupportActionBar().setTitle("Password Manager");
        Button loginSubmitBtn = (Button)findViewById(R.id.loginSubmitBtn);
        TextView backToRegister = (TextView) findViewById(R.id.backToRegister);
        mydb = new DBHelper(this);

//        if (!this.getIntent().getStringExtra("username").equals("")){
//            System.out.println(this.getIntent().getStringExtra("username"));
//        }
        EditText usernameInput = findViewById(R.id.usernameInput);
        usernameInput.setText(this.getIntent().getStringExtra("username"));
        EditText passwordInput = findViewById(R.id.passwordInput);
        passwordInput.setText(this.getIntent().getStringExtra("password"));

        backToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int userID = mydb.checkUserPassword(usernameInput.getText().toString().trim(), passwordInput.getText().toString().trim());
                if (userID != -1){
                    //Correct username password
                    Intent intent = new Intent(LoginActivity.this, PasswordMangerMainActivity.class);
                    intent.putExtra("userID", String.valueOf(userID));
                    startActivity(intent);
                }else{
                    Drawable usernameEditDrawable = usernameInput.getBackground();
                    Drawable passwordEdtDrawable = passwordInput.getBackground();
                    usernameEditDrawable = DrawableCompat.wrap(usernameEditDrawable);
                    DrawableCompat.setTint(usernameEditDrawable, Color.RED);
                    passwordEdtDrawable = DrawableCompat.wrap(passwordEdtDrawable);
                    DrawableCompat.setTint(passwordEdtDrawable, Color.RED);
                    Toast.makeText(LoginActivity.this, "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}