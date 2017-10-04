package ru.comp.vas.testrxjava;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class Main2Activity extends AppCompatActivity {

    private final String TAG = "main2_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Observable.just("Hello World!")
                .map(s -> s + " - Vas")
                .subscribe(s -> Log.i(TAG, s));

        writeLine();

        getUrls()
                .flatMap(urls -> Observable.fromIterable(urls))
                .flatMap(url -> getUrlTitle(url))
                .filter(str -> !str.equals("null"))
                .take(3)
                .subscribe(title -> Log.i(TAG, title));

        writeLine();


    }


    private Observable<List<String>> getUrls() {
        List<String> l = new ArrayList();
        l.add("url1");
        l.add("url2");
        l.add("url3");
        l.add("url4");
        l.add("url5");
        return Observable.just(l);
    }

    public Observable<String> getUrlTitle(String s) {
        String tmp = s;
//        int i = Integer.valueOf(tmp.substring(3));
        if (tmp.equals("url2")) {
            return Observable.just("null");
        }
        tmp += " - Vas" + tmp.substring(3);
        return Observable.just(tmp);
    }

    private void writeLine() {
        String temp = "";
        for (int i = 0; i < 50; ++i) {
            temp += "-";
        }
        Log.i(TAG, temp);
    }

}
