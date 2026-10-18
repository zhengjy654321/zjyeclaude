package com.zjye.polarnative;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

public class TriangleView extends View {
    public interface Listener { void onChanged(double dot, double formula, double dist); }

    private static final double S3 = Math.sqrt(3);
    // 坐标系：B(-1,0) C(1,0) A(0,√3)，M 为 BC 中点，N 为 AM 中点
    private final double[] A = {0, S3}, B = {-1, 0}, C = {1, 0}, M = {0, 0}, N = {0, S3 / 2};
    private double[] P = {0.5, 0.3};
    private Listener listener;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public TriangleView(Context c) { super(c); }

    public void setListener(Listener l) { listener = l; notifyChanged(); }

    public void jumpToN() { P = new double[]{N[0], N[1]}; notifyChanged(); invalidate(); }

    private float sc() { return Math.min(getWidth() / 2.6f, getHeight() / 2.2f); }
    private float px(double x) { return (float) (getWidth() / 2f + x * sc()); }
    private float py(double y) { return (float) (getHeight() - sc() * 0.25f - y * sc()); }

    private static double dot(double ax, double ay, double bx, double by) { return ax * bx + ay * by; }

    private static boolean inside(double[] p) {
        return p[1] >= 0 && p[1] <= S3 * (1 - Math.abs(p[0])) + 1e-9;
    }

    private double[] clamp(double[] p) {
        if (inside(p)) return p;
        double[][][] edges = {{A, B}, {B, C}, {C, A}};
        double[] best = null;
        double bd = 1e18;
        for (double[][] e : edges) {
            double dx = e[1][0] - e[0][0], dy = e[1][1] - e[0][1];
            double t = Math.max(0, Math.min(1, dot(p[0] - e[0][0], p[1] - e[0][1], dx, dy) / dot(dx, dy, dx, dy)));
            double qx = e[0][0] + dx * t, qy = e[0][1] + dy * t;
            double d = Math.hypot(p[0] - qx, p[1] - qy);
            if (d < bd) { bd = d; best = new double[]{qx, qy}; }
        }
        return best;
    }

    private void notifyChanged() {
        if (listener == null) return;
        double sx = (B[0] - P[0]) + (C[0] - P[0]), sy = (B[1] - P[1]) + (C[1] - P[1]);
        double d = dot(A[0] - P[0], A[1] - P[1], sx, sy);
        double dist = Math.hypot(P[0] - N[0], P[1] - N[1]);
        listener.onChanged(d, 2 * dist * dist - 1.5, dist);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float s = sc();
        if (s <= 0) return true;
        double x = (e.getX() - getWidth() / 2f) / s;
        double y = (getHeight() - s * 0.25f - e.getY()) / s;
        P = clamp(new double[]{x, y});
        notifyChanged();
        invalidate();
        return true;
    }

    private void arrow(Canvas c, double[] from, double tx, double ty, int color) {
        paint.setColor(color);
        paint.setStrokeWidth(5f);
        paint.setStyle(Paint.Style.STROKE);
        float x1 = px(from[0]), y1 = py(from[1]), x2 = px(tx), y2 = py(ty);
        c.drawLine(x1, y1, x2, y2, paint);
        double t = Math.atan2(y2 - y1, x2 - x1);
        Path h = new Path();
        h.moveTo(x2, y2);
        h.lineTo((float) (x2 - 28 * Math.cos(t - 0.4)), (float) (y2 - 28 * Math.sin(t - 0.4)));
        h.lineTo((float) (x2 - 28 * Math.cos(t + 0.4)), (float) (y2 - 28 * Math.sin(t + 0.4)));
        h.close();
        paint.setStyle(Paint.Style.FILL);
        c.drawPath(h, paint);
    }

    private void point(Canvas c, double[] p, int color, float r, String label) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        c.drawCircle(px(p[0]), py(p[1]), r, paint);
        paint.setColor(Color.BLACK);
        paint.setTextSize(40f);
        c.drawText(label, px(p[0]) + 14, py(p[1]) - 14, paint);
    }

    @Override
    protected void onDraw(Canvas c) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4f);
        paint.setColor(Color.DKGRAY);
        Path tri = new Path();
        tri.moveTo(px(A[0]), py(A[1]));
        tri.lineTo(px(B[0]), py(B[1]));
        tri.lineTo(px(C[0]), py(C[1]));
        tri.close();
        c.drawPath(tri, paint);
        c.drawLine(px(A[0]), py(A[1]), px(M[0]), py(M[1]), paint);

        arrow(c, P, A[0], A[1], Color.rgb(31, 95, 214));
        arrow(c, P, B[0], B[1], Color.GRAY);
        arrow(c, P, C[0], C[1], Color.GRAY);
        arrow(c, P, P[0] + (B[0] - P[0]) + (C[0] - P[0]), P[1] + (B[1] - P[1]) + (C[1] - P[1]), Color.rgb(201, 64, 43));

        point(c, A, Color.BLACK, 8f, "A");
        point(c, B, Color.BLACK, 8f, "B");
        point(c, C, Color.BLACK, 8f, "C");
        point(c, M, Color.BLACK, 6f, "M");
        point(c, N, Color.rgb(29, 138, 90), 10f, "N");
        point(c, P, Color.rgb(201, 64, 43), 14f, "P");
    }
}
