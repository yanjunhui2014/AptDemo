package com.milo.aptdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.milo.annotation.BindView;
import com.milo.aptdemo.ui.DemoFragment;
import com.milo.aptlib.BindTools;

import java.lang.annotation.Annotation;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mTvHelloWorld)
    TextView mTvHelloWorld;

    @BindView(R.id.mContainer)
    FrameLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BindTools.bind(this);
        if (mTvHelloWorld == null) {
            BindTools.bindStrMode(this);
            mTvHelloWorld.setText("APT-StringCreaterProxy .. process .. ");
        }
        mTvHelloWorld.setText("PT-PoetCreaterProxy .. process .. ");


        Class clazz = BindView.class;

        StringBuilder builder = new StringBuilder();
        builder.append("\n")
                .append(String.format("getCanonicalName == %s", clazz.getCanonicalName()))
                .append("\n")
                .append(String.format("getName == %s", clazz.getName()))
                .append("\n")
                .append(String.format("getSimpleName == %s", clazz.getSimpleName()));

        ((TextView) findViewById(R.id.mTvInfo)).setText(builder.toString());

        getSupportFragmentManager().beginTransaction().replace(mContainer.getId(), DemoFragment.newInstance())
                .commit();
    }

}
