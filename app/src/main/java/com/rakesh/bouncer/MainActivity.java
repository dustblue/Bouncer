package com.rakesh.bouncer;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.speech.RecognizerIntent;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Draw drawView;
    final int REQ_CODE_SPEECH_INPUT = 100;
    FloatingActionButton fab;
    int screenWidth, screenHeight, dir;
    Snackbar bar;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        drawView = new Draw(this);
        drawView.id=1;
        drawView.setLayoutParams(new AppBarLayout.LayoutParams(screenWidth,screenHeight));
        layout.setBackgroundColor(Color.parseColor("#44B3C2"));
        layout.addView(drawView);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick (View view) {
                        initiateVoiceRecog();
                    }
                }
        );
    }

    private void initiateVoiceRecog() {
        display("Initiating Speech Recognition");
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Change to, Move or Increase/Decrease Size");

        try {
            startActivityForResult(i, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            display("Speech Recognition not supported");
        }
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null!=data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    text = result.get(0);
                    display(text);
                    comprehend(text);
                }
            }
        }
    }

    public void comprehend (String text) {
        if (text.contains("change")) {
            drawView.invalidate();
            if(text.contains("square")) {
                drawView.id=1;
                drawView.invalidate();
            }
            if(text.contains("circle")){
                drawView.id=2;
                drawView.invalidate();
            }
            if(text.contains("rectangle")){
                drawView.id=3;
                drawView.invalidate();
            }
        }
        if (text.contains("move")) {
            if(text.contains("up")) dir=0;
            if(text.contains("right")) dir = 1;
            if(text.contains("down")) dir =2;
            if(text.contains("left")) dir= 3;
            if(drawView.id==1) moveRect(dir);
            if(drawView.id==2) moveCircle(dir);
            if(drawView.id==3) moveRect(dir);
        }
        if (text.contains("size")) {
            if (text.contains("increase")) resize(drawView.id, true);
            else if (text.contains("decrease")) resize(drawView.id, false);
        }
    }

    public void resize (int id, boolean res) {
        switch(id) {
            case 1 : {
                if (res) {
                    if (drawView.a-drawView.x <= screenWidth) {
                        drawView.x -= 10;
                        drawView.y -= 10;
                        drawView.a += 10;
                        drawView.b += 10;
                        drawView.invalidate();
                    }
                }
                else
                    if(drawView.a-drawView.x>=10) {
                        drawView.x += 10;
                        drawView.y += 10;
                        drawView.a -= 10;
                        drawView.b -= 10;
                        drawView.invalidate();
                    }
            }
            case 2 : {
                if (res) {
                    if (drawView.r <= (screenWidth / 2)) {
                        drawView.r += 10;
                        drawView.invalidate();
                    }
                }
                else
                    if(drawView.r>=10) {
                        drawView.r -= 10;
                        drawView.invalidate();
                    }
            }
            case 3 : {
                if (res) {
                    if (drawView.c-drawView.x <= screenWidth) {
                        drawView.x -= 10;
                        drawView.y -= 10;
                        drawView.c += 10;
                        drawView.d += 10;
                        drawView.invalidate();
                    }
                }
                else
                if(drawView.c-drawView.x>=10) {
                    drawView.x += 10;
                    drawView.y += 10;
                    drawView.c -= 10;
                    drawView.d -= 10;
                    drawView.invalidate();
                }
            }
        }
    }

    public void moveRect(int dir) {
        if (drawView.x>0 & drawView.y>0) {
            if (drawView.a<screenWidth & drawView.b<screenHeight) {
                switch(dir) {
                    case 0: {
                        drawView.y-=10;
                        if(drawView.id==1)drawView.b-=10;
                        if(drawView.id==3)drawView.d-=10;
                        drawView.invalidate();
                        break;
                    }
                    case 1: {
                        drawView.x+=10;
                        if(drawView.id==1)drawView.a+=10;
                        if(drawView.id==3)drawView.c+=10;
                        drawView.invalidate();
                        break;
                    }
                    case 2: {
                        drawView.y+=10;
                        if(drawView.id==1)drawView.b+=10;
                        if(drawView.id==3)drawView.d+=10;
                        drawView.invalidate();
                        break;
                    }
                    case 3: {
                        drawView.x-=10;
                        if(drawView.id==1)drawView.a-=10;
                        if(drawView.id==3)drawView.c-=10;
                        drawView.invalidate();
                        break;
                    }
                    default: {drawView.invalidate(); break;}
                }
            }
        }
    }

    public void moveCircle(int dir) {
        if (drawView.m-drawView.r>0 & drawView.n-drawView.r>0) {
            if (drawView.m<screenWidth& drawView.n<screenHeight) {
                switch(dir) {
                    case 0: {
                        drawView.n-=10;
                        drawView.invalidate();
                        break;
                    }
                    case 1: {
                        drawView.m+=10;
                        drawView.invalidate();
                        break;
                    }
                    case 2: {
                        drawView.n+=10;
                        drawView.invalidate();
                        break;
                    }
                    case 3: {
                        drawView.m-=10;
                        drawView.invalidate();
                        break;
                    }
                    default: {drawView.invalidate(); break;}
                }
            }
        }
    }

    public void display (String txt) {
        View view = findViewById(R.id.layout);
        bar = Snackbar.make(view, txt, Snackbar.LENGTH_SHORT);
        bar.show();
    }

    public class Draw extends View {
        Paint paint = new Paint();
        int id=0;
        int x =100, y=100, m=100, n= 100, a=180, b=180,c=140, d=180, r=40;
        public Draw(Context context) {
            super(context);
        }

        public void onDraw(Canvas canvas) {
            paint.setColor(Color.BLACK);
            switch(id) {
                case 1:{canvas.drawRect(x,y,a,b,paint);break;}
                case 2:{canvas.drawCircle(m,n,r,paint);break;}
                case 3:{canvas.drawRect(x,y,c,d,paint);break;}
                default: {canvas.drawRect(x,y,a,b,paint);break;}
            }
        }
    }
}


