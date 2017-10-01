package ru.comp.vas.testrxjava;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v4.util.TimeUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    public static final String BUNDLE_TEXT = "text_bundle";

    @BindView(R.id.et_name)
    EditText mEtName;

    @BindView(R.id.et_first_pass)
    EditText mEtFPass;


    @BindView(R.id.et_second_pass)
    EditText mEtSPass;

    @BindView(R.id.text_view)
    TextView mTextView;

    @BindView(R.id.btn)
    Button mButton;

    @BindView(R.id.tv_pass_not_equals)
    TextView mTextViewPassNotEquals;

    private List<Disposable> mDisposableList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            mTextView.setText(savedInstanceState.getString(BUNDLE_TEXT));
        }

//        Observable<String> startObs = RxTextView.textChanges(mEtStart)
//                .debounce(500, TimeUnit.MILLISECONDS)
//                .map(s -> s.toString().replace(" ", ""));
//        Observable<String> endObs = RxTextView.textChanges(mEtEnd)
//                .debounce(500, TimeUnit.MILLISECONDS)
//                .map(s -> s.toString().replace(" ", ""));
//
//        Observable.combineLatest(
//                startObs,
//                endObs,
//                (s1, s2) -> s1 + ":" + s2)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(s -> mTextView.setText(s));
//
//        Observable.combineLatest(
//                startObs,
//                endObs,
//                (s1, s2) -> notEmpty(s1, s2))
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(b -> mButton.setEnabled(b));

        Observable<String> nameObs = RxTextView.textChanges(mEtName)
                .map(String::valueOf)
                .map(String::trim);

        mDisposableList.add(
                nameObs
                        .map((s) -> {
                            if (!TextUtils.isEmpty(s)) return ", " + s;
                            else return s;
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((s) -> mTextView.setText("Sing in" + s))
        );


        Observable<Boolean> duplicatesCheckObs = nameObs
                .debounce(500, TimeUnit.MILLISECONDS)
                .map(this::requestName)
                .map(s -> !checkResponseForDuplicates(s))
                .onErrorReturn(throwable -> false);

        mDisposableList.add(
                duplicatesCheckObs
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::setUniqName)
        );

        Observable<Boolean> equalsPass = Observable.combineLatest(
                RxTextView.textChanges(mEtFPass).map(s -> s.toString().trim()).filter(s -> !s.isEmpty()),
                RxTextView.textChanges(mEtSPass).map(s -> s.toString().trim()).filter(s -> !s.isEmpty()),
                String::equals
        );

        mDisposableList.add(
                equalsPass
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(isEquals -> {
                            if (isEquals) mTextViewPassNotEquals.setVisibility(View.INVISIBLE);
                            else mTextViewPassNotEquals.setVisibility(View.VISIBLE);
                        })
        );


        mDisposableList.add(
                Observable.combineLatest(
                        duplicatesCheckObs,
                        equalsPass,
                        (isUniq, isPassEquals) -> isUniq && isPassEquals)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(enable -> mButton.setEnabled(enable))
        );

        RxView.clicks(mButton)
                .subscribe(view -> onClickk());
    }

//    private boolean notEmpty(String... strings) {
//        for(String s : strings) {
//            if (s.isEmpty()) {
//                return false;
//            }
//        }
//        return true;
//    }

    private String requestName(String name) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return name;
    }

    private boolean checkResponseForDuplicates(String response) {
        if (response.equals("Жора")
                || response.equals("Вася")
                || response.equals("Паша")) {
            return true;
        } else {
            return false;
        }
    }

    private void setUniqName(boolean uniq) {
        if (uniq) {
            mTextView.setTextColor(getResources().getColor(R.color.greenColor));
        } else {
            mTextView.setTextColor(getResources().getColor(R.color.redColor));
        }
    }

    private void onClickk() {
        Toast.makeText(this, "Click!!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_TEXT, mTextView.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposableList.forEach(Disposable::dispose);
    }
}
