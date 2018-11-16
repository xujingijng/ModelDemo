package com.xjj.freight.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.TouchDelegate;
import android.view.View;

/**
 * 扩大view的点击区域
 */
public class TouchUtil {
	private static final int MINSIZE = 72;
	private static DisplayMetrics dm = null;

    /**
     * Set delegate.
     *
     * @param activity the activity
     * @param view     the view
     */
    public static void setDelegate(final Activity activity, final View view){
		view.post(new Runnable() {

			@Override
			public void run() {
				Rect bounds = new Rect();
				view.getHitRect(bounds);

				if(activity != null){
					dm = new DisplayMetrics();
					activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
				}
				int minsize = MINSIZE;
				if (dm != null && dm.density > 1) {
                    minsize *= dm.density;
                }

				boolean changed = false;
				if (bounds.width() < minsize) {
					int cx = bounds.centerX();
					bounds.left = cx - minsize / 2;
					bounds.right = cx + minsize / 2;
					changed = true;
				}
				if (bounds.height() < minsize) {
					int cy = bounds.centerY();
					bounds.top = cy - minsize / 2;
					bounds.bottom = cy + minsize / 2;
					changed = true;
				}
				if (changed) {
					TouchDelegate touchDelegate = new TouchDelegate(bounds,
							view);

					if (View.class.isInstance(view.getParent())) {
						((View) view.getParent())
								.setTouchDelegate(touchDelegate);
					}
				}
			}
		});
	}
}
