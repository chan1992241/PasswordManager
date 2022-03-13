package my.edu.utar.passwordmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class RevealPassword extends Dialog {
    private Context context;
    private String passwordID;
    private DBHelper mydb;
    private String userID;

    public RevealPassword(@NonNull Context context, String passwordID, String userID) {
        super(context);
        this.context = context;
        this.passwordID = passwordID;
        this.userID = userID;
    }

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reveal_password);
        mydb = new DBHelper(context);

        EditText siteNameInput = findViewById(R.id.revealSiteName);
        EditText usernameInput = findViewById(R.id.revealUsername);
        EditText passwordInput = findViewById(R.id.revealPassword);

        HashMap res = mydb.getPassword(passwordID);
        siteNameInput.setText((String)res.get("siteName"));
        usernameInput.setText((String)res.get("username"));
        passwordInput.setText((String)res.get("password"));


        Button saveChangeBtn = findViewById(R.id.btn_save);
        Button ok = findViewById(R.id.btn_ok);
        Button deleteBtn = findViewById(R.id.btn_delete);
        saveChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText siteNameInput = findViewById(R.id.revealSiteName);
                EditText usernameInput = findViewById(R.id.revealUsername);
                EditText passwordInput = findViewById(R.id.revealPassword);
                String siteNameValue = siteNameInput.getText().toString().trim();
                String usernameValue = usernameInput.getText().toString().trim();
                String passwordValue = passwordInput.getText().toString().trim();
                Drawable usernameEditDrawable = usernameInput.getBackground();
                Drawable passwordEdtDrawable = passwordInput.getBackground();
                Drawable siteNameEditDrawable = siteNameInput.getBackground();
                if (siteNameValue.equals("") || usernameValue.equals("") || passwordValue.equals("")){
                    usernameEditDrawable = DrawableCompat.wrap(usernameEditDrawable);
                    DrawableCompat.setTint(usernameEditDrawable, Color.RED);
                    passwordEdtDrawable = DrawableCompat.wrap(passwordEdtDrawable);
                    DrawableCompat.setTint(passwordEdtDrawable, Color.RED);
                    siteNameEditDrawable = DrawableCompat.wrap(siteNameEditDrawable);
                    DrawableCompat.setTint(siteNameEditDrawable, Color.RED);
                    Toast.makeText(context, "These Field Must Be Filled", Toast.LENGTH_SHORT).show();
                }else{
                    if (mydb.updatePassword(passwordID, userID, siteNameValue, usernameValue, passwordValue)){
                        Intent intent = new Intent(context, PasswordMangerMainActivity.class);
                        intent.putExtra("userID", userID);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        dismiss();
                        context.startActivity(intent);
                    }else{
                        System.out.println("Something wrong");
                    }
                }
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = mydb.deletePassword(passwordID);
                if (result){
                    Intent intent = new Intent(context, PasswordMangerMainActivity.class);
                    intent.putExtra("userID", userID);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    dismiss();
                    context.startActivity(intent);
                }else{
                    dismiss();
                }
            }
        });

    }
}