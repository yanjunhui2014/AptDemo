package com.milo.aptdemo.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.milo.annotation.BindView;
import com.milo.aptdemo.R;
import com.milo.aptlib.BindTools;

import java.util.Random;

/**
 * 标题：
 * 功能：
 * 备注：
 * <p>
 * Created by Milo  2020/3/10
 * E-Mail : 303767416@qq.com
 */
public class DemoFragment extends Fragment {

    @BindView(R.id.mImgHolder)
    ImageView mImgHolder;

    @BindView(R.id.mBtnChange)
    Button mBtnChange;

    public static Fragment newInstance() {
        return new DemoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demo, container, false);
        BindTools.bind(this, view);

        mBtnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImgHolder.setBackgroundColor(getRandomColor());
            }
        });

        return view;
    }

    private int getRandomColor() {
        int colors[] = new int[]{Color.BLACK, Color.BLUE, Color.DKGRAY, Color.GREEN, Color.YELLOW, Color.RED};
        return colors[new Random().nextInt(colors.length)];
    }

}
