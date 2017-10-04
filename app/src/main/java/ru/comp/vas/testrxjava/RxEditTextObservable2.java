package ru.comp.vas.testrxjava;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;

public class RxEditTextObservable2 {

    public static Observable<String> getRxEditTextObservableFrom(@NonNull final EditText editText) {
        return Observable.fromPublisher(p -> {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    p.onNext(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        });
    }

}
