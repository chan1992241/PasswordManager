package my.edu.utar.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

public class PasswordMangerMainActivity extends AppCompatActivity {
    private DBHelper mydb;
    private Context context;
    private CardView cardView;
    private LinearLayout.LayoutParams layoutParams;
    private String userID;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_manger_main);
        getSupportActionBar().setTitle("Password Manager");
        context = this.getApplicationContext();

        FloatingActionButton addNewPassword = (FloatingActionButton)findViewById(R.id.addNewPassword);

        userID = this.getIntent().getStringExtra("userID");
        mydb = new DBHelper(this);
        LinearLayout ll = findViewById(R.id.passwordListContainer);
        Cursor res = mydb.getPasswordList(userID);
        if (res.moveToFirst()){
            while (res.isAfterLast() == false){
                createPassCard(res.getString(res.getColumnIndex("siteName")), res.getString(res.getColumnIndex("id")) ,ll);
                res.moveToNext();
            }
        }

        addNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PasswordMangerMainActivity.this, AddNewPassword.class);
                intent.putExtra("userID", userID );
                startActivity(intent);
            }
        });
    }
    public void createPassCard(String siteName, String id, LinearLayout ll){
        cardView = new CardView(context);
        LayoutParams layoutparamsCard = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        cardView.setLayoutParams(layoutparamsCard);
        cardView.setRadius(15);
        cardView.setPadding(25, 25, 25, 25);
        cardView.setCardBackgroundColor(Color.WHITE);
        cardView.setMaxCardElevation(12);
        cardView.setPreventCornerOverlap(true);
        cardView.setUseCompatPadding(true);

        LinearLayout ll2 = new LinearLayout(context);
        ll2.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.weight = 2;
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.weight = 1;

        TextView textView = new TextView(context);
        if (siteName.length() > 11){
            siteName = siteName.substring(0, 10) + "...";
        }
        textView.setText(siteName);
        textView.setTextSize(20);
        textView.setPadding(20, 10, 10, 10);
        textView.setLayoutParams(lp);
        ll2.addView(textView);

        Button btn = new Button(context);
        btn.setText("View");
        btn.setLayoutParams(lp2);
        btn.setBackgroundColor(Color.parseColor("#FF6200EE"));
        btn.setTextColor(Color.WHITE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInPassword signInPassword = new SignInPassword(PasswordMangerMainActivity.this, id, userID);
                signInPassword.show();
            }
        });
        ll2.addView(btn);

        cardView.addView(ll2);
        ll.addView(cardView);
    }
}