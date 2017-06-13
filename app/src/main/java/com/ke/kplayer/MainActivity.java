package com.ke.kplayer;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.lv_video);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = arrayAdapter.getItem(position);
//                VideoActivity.intentTo(VideoListActivity.this,path,path);
                VideoActivity.toActivity(MainActivity.this,path);
            }
        });

        mSubscription = Observable.create(new Observable.OnSubscribe<String>() {

            private void getVideoFile(Subscriber<? super String> subscriber, File file) {
                if (file.isDirectory()) {
                    for (File f : file.listFiles()) {
                        getVideoFile(subscriber, f);
                    }
                } else {
                    if (file.getAbsolutePath().endsWith(".mp4")) {
                        subscriber.onNext(file.getAbsolutePath());
                    }
                }
            }


            @Override
            public void call(Subscriber<? super String> subscriber) {
                getVideoFile(subscriber, Environment.getExternalStorageDirectory());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(getApplicationContext(), "scan video done", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        arrayAdapter.add(s);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscription.unsubscribe();
    }
}
