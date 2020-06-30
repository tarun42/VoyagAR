package com.xlsoft.kudanar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class login extends AppCompatActivity {
    EditText email,pass;
    Button login_btn,sign_up_page;
    //private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
      //  mAuth = FirebaseAuth.getInstance();
        email=findViewById(R.id.editText);
        pass=findViewById(R.id.editText2);
        login_btn=findViewById(R.id.button3);
        sign_up_page=findViewById(R.id.button4);
        sign_up_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this,signup.class));
            }
        });
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* String str= String.valueOf(email.getText());
                String str2= String.valueOf(pass.getText());
                if(str.isEmpty() && str2.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"INVALID CREDENTIAL",Toast.LENGTH_LONG).show();
                }
                else
                {

                    mAuth.createUserWithEmailAndPassword(str, str2).addOnCompleteListener(login.this, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(login.this.getApplicationContext(),
                                        "SignUp unsuccessful: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                       Toast.makeText(getApplicationContext(),"signedIN",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(login.this, MainActivity.class));
                            }
                        }


                    });

                }*/
                startActivity(new Intent(login.this,threeDmaps.class));
                finish();
            }
        });



    }
    @Override
    protected void onStart() {
        super.onStart();

        //FirebaseUser currentUser = mAuth.getCurrentUser();


    }

}
