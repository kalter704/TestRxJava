package ru.comp.vas.testrxjava;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Main2Activity extends AppCompatActivity {

    private final String TAG = "main2_activity";

    private static int count = 0;

    @BindView(R.id.tv_count)
    TextView mCountTextView;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ButterKnife.bind(this);

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


        Log.i(TAG, "Start Main2Activity");

        writeLine();

        mCompositeDisposable.add(
                getNumbers()
                        .map(s -> s + " - Sleep 6 sec!")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                s -> Log.i(TAG, s),
                                Throwable::printStackTrace,
                                this::writeLine
                        )
        );

        mCompositeDisposable.add(
                Observable.just("Hello World!")
                        .map(s -> s + " - Vas")
                        .subscribe(
                                s -> Log.i(TAG, s),
                                Throwable::printStackTrace,
                                this::writeLine)
        );

        writeLine();

        mCompositeDisposable.add(
                getUrls()
                        .flatMap(urls -> Observable.fromIterable(urls))
                        .flatMap(url -> getUrlTitle(url))
                        .filter(str -> !str.equals("null"))
                        .take(3)
                        .subscribe(
                                title -> Log.i(TAG, title),
                                Throwable::printStackTrace,
                                this::writeLine)
        );

        mCompositeDisposable.add(
                Observable.just("qwe", "sdfas", "sdvb")
                        .map(s -> s += " - 123")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                s -> Log.i(TAG, s),
                                Throwable::printStackTrace,
                                this::writeLine)
        );

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

    private Observable<String> getNumbers() {
        return Observable.defer(() -> executeSlowBlockingMethod());
    }

    private Observable<String> executeSlowBlockingMethod() {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Observable.just("one", "two", "three", "four", "five");
    }

    private void writeLine() {
        String temp = "";
        for (int i = 0; i < 50; ++i) {
            temp += "-";
        }
        Log.i(TAG, temp);
    }

    @OnClick(R.id.btn_click2)
    void clickkk2() {
        mCountTextView.setText("Click " + String.valueOf(count++));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
        writeLine();
        Log.i(TAG, "Dispose all");
        writeLine();
    }
}
