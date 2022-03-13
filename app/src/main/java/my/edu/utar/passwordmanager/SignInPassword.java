package my.edu.utar.passwordmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignInPassword extends Dialog {

    private DBHelper mydb;
    private Context context;
    private String passwordID;
    private String userID;

    public SignInPassword(@NonNull Context context, String passwordID, String userID) {
        super(context);
        this.context = context;
        this.passwordID = passwordID;
        this.userID = userID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_password);
        mydb = new DBHelper(context);
        Button cancelBtn = findViewById(R.id.btn_cancel);
        Button okBtn = findViewById(R.id.btn_ok);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText usernameInput = findViewById(R.id.signInUsername);
                EditText passwordInput = findViewById(R.id.signInPassword);
                boolean result = mydb.checkUserPasswordwithUserId(usernameInput.getText().toString().trim(), passwordInput.getText().toString().trim(), userID);
                if (result == true){
                    //Correct username password
                    dismiss();
                    RevealPassword revealPassword = new RevealPassword(context, passwordID, userID);
                    revealPassword.show();
                }else{
                    Drawable usernameEditDrawable = usernameInput.getBackground();
                    Drawable passwordEdtDrawable = passwordInput.getBackground();
                    usernameEditDrawable = DrawableCompat.wrap(usernameEditDrawable);
                    DrawableCompat.setTint(usernameEditDrawable, Color.RED);
                    passwordEdtDrawable = DrawableCompat.wrap(passwordEdtDrawable);
                    DrawableCompat.setTint(passwordEdtDrawable, Color.RED);
                    Toast.makeText(context, "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}