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
import android.widget.Toast;

public class AddNewPassword extends AppCompatActivity {
    DBHelper mydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_password);
        Button addSubmitBtn = findViewById(R.id.addSubmitBtn);
        mydb = new DBHelper(this);

        String userID = this.getIntent().getStringExtra("userID");

        addSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText siteName = (EditText)findViewById(R.id.pm_siteName);
                EditText username = (EditText) findViewById(R.id.pm_username);
                EditText password = (EditText) findViewById(R.id.pm_password);
                String siteNameVal = siteName.getText().toString().trim();
                String usernameVal = username.getText().toString().trim();
                String passwordVal = password.getText().toString().trim();
                Drawable usernameEditDrawable = username.getBackground();
                Drawable passwordEdtDrawable = password.getBackground();
                Drawable siteNameEditDrawable = siteName.getBackground();
                if (siteNameVal.equals("") || usernameVal.equals("") || passwordVal.equals("")){
                    usernameEditDrawable = DrawableCompat.wrap(usernameEditDrawable);
                    DrawableCompat.setTint(usernameEditDrawable, Color.RED);
                    passwordEdtDrawable = DrawableCompat.wrap(passwordEdtDrawable);
                    DrawableCompat.setTint(passwordEdtDrawable, Color.RED);
                    siteNameEditDrawable = DrawableCompat.wrap(siteNameEditDrawable);
                    DrawableCompat.setTint(siteNameEditDrawable, Color.RED);
                    Toast.makeText(AddNewPassword.this, "These Field Must Be Filled", Toast.LENGTH_SHORT).show();
                }else{
                    if (mydb.insertNewPassword(userID, siteNameVal, usernameVal, passwordVal) == true){
                        Toast.makeText(AddNewPassword.this, "Successfully Added", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddNewPassword.this, PasswordMangerMainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("userID", userID);
                        startActivity(intent);
                    }
                }
            }
        });
    }
}