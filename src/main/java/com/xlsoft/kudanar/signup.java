package com.xlsoft.kudanar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class signup extends AppCompatActivity {
    EditText email,pass,c_pass;
    Button login_page,signup;
    //private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_signup);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
      //  mAuth = FirebaseAuth.getInstance();
        email=findViewById(R.id.editText3);
        pass=findViewById(R.id.editText4);
        c_pass=findViewById(R.id.editText5);
        signup=findViewById(R.id.button5);
        login_page=findViewById(R.id.button6);
        login_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signup.this,login.class));
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* String str= String.valueOf(email.getText());
                String str2= String.valueOf(pass.getText());
                String str3= String.valueOf(c_pass.getText());
                if(str.isEmpty() && str2.isEmpty() )
                {
                    Toast.makeText(getApplicationContext(),"INVALID CREDENTIAL",Toast.LENGTH_LONG).show();
                }
                else if(!str3.equals(str2))
                {
                    Toast.makeText(getApplicationContext(),"Unmatched  password",Toast.LENGTH_LONG).show();

                }
                else
                {

                    mAuth.createUserWithEmailAndPassword(str, str2).addOnCompleteListener(signup.this, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(signup.this.getApplicationContext(),
                                        "SignUp unsuccessful: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(),"signedUP",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(signup.this, login.class));
                            }
                        }
                    });

                }*/
                startActivity(new Intent(signup.this,login.class));
            }
        });
    }
}