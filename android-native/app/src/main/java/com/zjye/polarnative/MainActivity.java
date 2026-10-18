package com.zjye.polarnative;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, pad * 2, pad, pad);

        TextView title = new TextView(this);
        title.setTextSize(18);
        title.setText("正三角形 ABC 边长 2，P 在三角形内或边上。拖动 P，求 PA·(PB+PC) 的最小值。");
        root.addView(title);

        final TextView info = new TextView(this);
        info.setTextSize(15);
        info.setPadding(0, pad / 2, 0, 0);
        root.addView(info);

        final TriangleView view = new TriangleView(this);
        view.setListener(new TriangleView.Listener() {
            @Override
            public void onChanged(double dot, double formula, double dist) {
                info.setText(String.format(
                    "PA·(PB+PC) = %.3f\n2|PN|² − 3/2 = %.3f\n|PN| = %.3f\n最小值 −3/2，当 P 与 N（AM 中点）重合",
                    dot, formula, dist));
            }
        });
        root.addView(view, new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        Button btn = new Button(this);
        btn.setText("跳到最小点 N");
        btn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) { view.jumpToN(); }
        });
        root.addView(btn);

        setContentView(root);
    }
}
