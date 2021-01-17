package com.zeyuyang.eerovercontrol;

import java.io.IOException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


public class MainActivity extends Activity implements OnClickListener {

    Button get, forward, backward, left, right, check, clear, neckUp, neckDown, liftUp, liftDown;
    TextView drive;

    private WebView webview;
    static final ArrayList data = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        webview = (WebView) findViewById(R.id.webview);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl("http://192.168.0.6/5");

        try {
            setw();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setw() throws IOException {

        drive = (TextView) findViewById(R.id.drive);

        get = (Button) findViewById(R.id.get);
        forward = (Button) findViewById(R.id.forward);
        backward = (Button) findViewById(R.id.backward);
        left = (Button) findViewById(R.id.left);
        right = (Button) findViewById(R.id.right);
        check = (Button) findViewById(R.id.check);
        clear = (Button) findViewById(R.id.clear);
        neckUp = (Button) findViewById(R.id.neckUp);
        neckDown = (Button) findViewById(R.id.neckDown);

        final int[] dis = {0, 0};
        final String address = "http://192.168.0.6/";

//        drive.setText(direction(dis[0], dis[1]) + "");


        forward.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            dis[0] -= 1;
                            webview.loadUrl(address + direction(dis[0], dis[1]));
                        }
//                        drive.setText(direction(dis[0], dis[1]) + "");

                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            dis[0] += 1;
                            webview.loadUrl(address + direction(dis[0], dis[1]));
                        }
//                        drive.setText(direction(dis[0], dis[1]) + "");
                        return true;
                    }
                }
        );


        backward.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            dis[0] += 1;
                            webview.loadUrl(address + direction(dis[0], dis[1]));
                        }
//                        drive.setText(direction(dis[0], dis[1]) + "");
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            dis[0] -= 1;
                            webview.loadUrl(address + direction(dis[0], dis[1]));
                        }
//                        drive.setText(direction(dis[0], dis[1]) + "");
                        return true;
                    }
                }
        );


        left.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            dis[1] -= 1;
                            webview.loadUrl(address + direction(dis[0], dis[1]));
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            dis[1] += 1;
                            webview.loadUrl(address + direction(dis[0], dis[1]));
                        }
                        return true;
                    }
                }
        );


        right.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            dis[1] += 1;
                            webview.loadUrl(address + direction(dis[0], dis[1]));
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            dis[1] -= 1;
                            webview.loadUrl(address + direction(dis[0], dis[1]));
                        }
                        return true;
                    }
                }
        );


        neckUp.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            webview.loadUrl(address + "U");
                        }

                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            webview.loadUrl(address + "X");
                        }
                        return true;
                    }
                }
        );


        neckDown.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            webview.loadUrl(address + "D");
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            webview.loadUrl(address + "X");
                        }
                        return true;
                    }
                }
        );



        get.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {

                            webview.loadUrl(address + "G");

                            Utils.delay(2, new Utils.DelayCallback() {
                                @Override
                                public void afterDelay() {
                                    webview.loadUrl(address + "5");
                                    String title = webview.getTitle();
                                    drive.setText(title);
                                    data.add(title);
                                }
                            });
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {

                        }
                        return true;
                    }
                }
        );

        check.setOnLongClickListener(
                new Button.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        openDialog("LIZARDS", data);
                        return true;
                    }
                }
        );


        clear.setOnLongClickListener(
                new Button.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                        builder.setTitle("Sure you want to clear all data?")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                data.clear();
                                                openDialog("Lizard Data Cleared", data);
                                            }
                                        }
                                )
                                .setNegativeButton("Cancel", null);

                        AlertDialog alert = builder.create();
                        alert.show();
                        return true;
                    }
                }
        );

    }

    @Override
    public void onClick (View v) {
        try {

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public int direction (int a, int b) {
        int count = 0;
        int out = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                count++;
                if (i == a & j == b) {
                  out = count;
                }
            }
        }
        return out;
    }

    public void openDialog (String title, ArrayList list) {
        String out = "";
        for (int i = 0; i < list.size(); i++) {
            out += list.get(i) + "\n";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(title)
                .setMessage(out)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }
         );

        AlertDialog alert = builder.create();
        alert.show();
    }

}
