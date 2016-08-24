package com.hdf.easytools.view.listview;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.widget.ListView;

public class ListViewNoScroll extends ListView {
 
    private int mPosition;
 
    public ListViewNoScroll(Context context) {
        super(context);
    }
 
    public ListViewNoScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    public ListViewNoScroll(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
	/**
	 * 重写该方法，达到使ListView适应ScrollView的效果
	 */
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
 
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int actionMasked = ev.getActionMasked() & MotionEvent.ACTION_MASK;
 
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            mPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
            return super.dispatchTouchEvent(ev);
        }
 
        if (actionMasked == MotionEvent.ACTION_MOVE) {
            return true;
        }
 
        if (actionMasked == MotionEvent.ACTION_UP
        		|| actionMasked == MotionEvent.ACTION_CANCEL) {
            if (pointToPosition((int) ev.getX(), (int) ev.getY()) == mPosition) {
                super.dispatchTouchEvent(ev);
            } else {
                setPressed(false);
                invalidate();
                return true;
            }
        }
 
        return super.dispatchTouchEvent(ev);
    }
}