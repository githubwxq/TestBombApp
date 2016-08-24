package com.hdf.easytools.view.section;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Scroller;

import com.nineoldandroids.animation.ValueAnimator;

public class DragGridView extends ViewGroup {
	private static final int DEFAULT_ROW_COUNT = 99;
	private static final int DEFAULT_COL_COUNT = 4;
	protected final String TAG = "DesktopView";
	protected final static int TOUCH_STATE_REST = 0;
	protected final static int TOUCH_STATE_SCROLLING = 1;
	protected int pageRowCount = DEFAULT_ROW_COUNT, pageColCount = DEFAULT_COL_COUNT;
	protected int horizontalSpacing = 0, verticalSpacing = 0;
	protected int currentPage;
	protected int pageCount;
	protected List<Rect> positionList = new ArrayList<Rect>();
	protected List<ItemMeta> metaList = new ArrayList<ItemMeta>();
	protected BaseAdapter adapter;
	protected ItemMeta selectedMeta;
	protected Scroller scroller;
	protected boolean firstLayout = false;
	protected int touchState = TOUCH_STATE_REST;
	protected int touchSlop;
	protected VelocityTracker velocityTracker;
	protected GestureDetector gestureDetector;
	protected ImageView selectedBorderView;
	protected ItemMeta clickMeta;
	protected int screenWidth;
	protected boolean supportPage;
	protected DragGridViewListener listener;

//	public int selectedBorderResId = R.drawable.service_item_bg_p;
	public int selectedColor = Color.TRANSPARENT;
	protected int innerTopPadding = 0;

	protected static final Interpolator tnterpolator = new Interpolator() {
		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t * t * t + 1.0f;
		}
	};

	protected boolean isWaitSwitch = false;
	protected int switchSlop = dip2px(getContext(), 35);
	protected Runnable switchRunnable;

	public DragGridView(Context context) {
		super(context);
		initialize();
	}

	public DragGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public DragGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	protected void initialize() {
		supportPage = true;
		scroller = new Scroller(getContext(), tnterpolator);
		ViewConfiguration configuration = ViewConfiguration.get(getContext());
		touchSlop = configuration.getScaledTouchSlop() * 5;
		gestureDetector = new GestureDetector(getContext(), new SimpleOnGestureListener() {
			@Override
			public void onLongPress(MotionEvent e) {
				if (clickMeta == null)
					return;
				if (listener != null && listener.ignoreItem(clickMeta))
					return;
				selectedMeta = clickMeta;
				showSelectedBorder(selectedMeta.index);
				selectedMeta.itemView.bringToFront();
				if (listener != null) {
					listener.dragBegin(selectedMeta);
				}
				super.onLongPress(e);
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				if (listener != null) {
					listener.click(clickMeta);
				}
				return super.onSingleTapUp(e);
			}
		});

		DisplayMetrics dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int availWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
		int maxChildSize = (availWidth - 5 * horizontalSpacing) / pageColCount;
		int childHeight = 0, childWidth = 0;
		for (int i = 0, len = getChildCount(); i < len; ++i) {
			View child = getChildAt(i);
			LayoutParams lp = child.getLayoutParams();
			int widthMode = MeasureSpec.UNSPECIFIED;
			int heightMode = MeasureSpec.UNSPECIFIED;
			int widthSize = 0;
			int heightSize = 0;
			if (lp.width != LayoutParams.WRAP_CONTENT) {
				widthMode = MeasureSpec.EXACTLY;
				widthSize = maxChildSize;
				if (lp.width != LayoutParams.MATCH_PARENT) {
					widthSize = lp.width;
				}
			}
			if (lp.height != LayoutParams.WRAP_CONTENT) {
				heightMode = MeasureSpec.EXACTLY;
				heightSize = maxChildSize;
				if (lp.height != LayoutParams.MATCH_PARENT) {
					heightSize = lp.height;
				}
			}
			final int widthSpec = MeasureSpec.makeMeasureSpec(widthSize, widthMode);
			final int heightSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
			child.measure(widthSpec, heightSpec);
			childHeight = child.getMeasuredHeight();
			childWidth = child.getMeasuredWidth();

			if (childWidth > maxChildSize) {
				MeasureSpec.makeMeasureSpec(maxChildSize, MeasureSpec.EXACTLY);
				child.measure(widthSpec, heightSpec);
			}
		}

		int rowCount = (int) Math.ceil(getChildCount() * 1.0 / pageColCount);
		if (rowCount > pageRowCount) {
			rowCount = pageRowCount;
		}
		int height = innerTopPadding + rowCount * childHeight;
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), resolveSize(height, heightMeasureSpec));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int availWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
		int defaultChildSize = (availWidth - 5 * horizontalSpacing) / pageColCount;

		int width = r - l;
		int height = b - t;
		int paddingLeft = getPaddingLeft();
		int paddingTop = getPaddingTop();
		int paddingRight = getPaddingRight();
		int paddingBottom = getPaddingBottom();
		int pageSize = pageRowCount * pageColCount;
		int pageIndex = 0;

		int hs = horizontalSpacing;

		if (firstLayout) {
			int maxChildWidth = 0;
			for (int i = 0, len = getChildCount(); i < len; ++i) {
				View child = getChildAt(i);
				maxChildWidth = Math.max(maxChildWidth, child.getMeasuredWidth());
			}

			if (maxChildWidth > 0 && maxChildWidth < defaultChildSize) {
				hs = (availWidth - maxChildWidth * pageColCount) / 5;
			}

			int childLeft = paddingLeft + hs;
			int childTop = innerTopPadding + verticalSpacing;

			for (int i = 0, len = getChildCount(); i < len; ++i) {
				View child = getChildAt(i);
				int childWidth = child.getMeasuredWidth();
				int childHeight = child.getMeasuredHeight();
				if (i != 0 && (i % pageSize == 0)) {
					pageIndex = i / pageSize;
				}
				if (i != 0 && (i % pageColCount == 0)) {
					childLeft = paddingLeft + hs + pageIndex * width;
					childTop += childHeight + verticalSpacing;
				}
				if (i != 0 && (i % pageSize == 0)) {
					childTop = innerTopPadding + verticalSpacing;
				}
				child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
				if (firstLayout) {
					Rect rect = new Rect(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
					positionList.add(rect);
					ItemMeta meta = metaList.get(i);
					meta.rect = rect;
				}
				childLeft += childWidth + hs;
			}
			firstLayout = false;
		}
	}

	float lastMotionX, lastMotionY;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		float x = ev.getX();
		float y = ev.getY();
		switch ((ev.getAction())) {
			case MotionEvent.ACTION_DOWN:
				if (!scroller.isFinished()) {
					scroller.abortAnimation();
				}
				touchState = scroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
				lastMotionX = x;
				lastMotionY = y;
				return false;
			case MotionEvent.ACTION_MOVE:
				if (touchState == TOUCH_STATE_REST) {
					int xDiff = (int) Math.abs(x - lastMotionX);
					if (xDiff > touchSlop) {
						touchState = TOUCH_STATE_SCROLLING;
						return true;
					}
				}
			case MotionEvent.ACTION_UP:
				return false;
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!supportPage)
			return false;
		float x = ev.getX();
		float y = ev.getY();
		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}
		velocityTracker.addMovement(ev);
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_MOVE:
				if (touchState == TOUCH_STATE_REST) {
					int xDiff = (int) Math.abs(x - lastMotionX);
					if (xDiff > touchSlop) {
						touchState = TOUCH_STATE_SCROLLING;
						return true;
					}
				}
				if (touchState == TOUCH_STATE_SCROLLING) {
					int delta = (int) (lastMotionX - x);
					lastMotionX = x;
					lastMotionY = y;

					final int scrollX = getScrollX();
					if (delta < 0) {
						if (scrollX > 0) {
							scrollBy(delta, 0);
						} else {
							scrollBy(delta / 3, 0);
						}
					} else if (delta > 0) {
						if ((pageCount * getWidth()) - scrollX - getWidth() > 0) {
							scrollBy(delta, 0);
						} else {
							scrollBy(delta / 3, 0);
						}
					}
					return true;
				}
				break;
			case MotionEvent.ACTION_UP:
				if (touchState == TOUCH_STATE_SCROLLING) {
					velocityTracker.computeCurrentVelocity(1000);
					int velocityX = (int) velocityTracker.getXVelocity();
					int newIndex = currentPage;
					if (velocityX > 1000) {
						newIndex--;
					} else if (velocityX < -1000) {
						newIndex++;
					} else {
						newIndex = (int) Math.round(getScrollX() * 1.0 / getWidth());
					}
					snapToPage(newIndex);

					if (velocityTracker != null) {
						velocityTracker.recycle();
						velocityTracker = null;
					}
					touchState = TOUCH_STATE_REST;
				}
				break;
		}
		return true;
	}

	public void setSupportPage(boolean supportPage) {
		this.supportPage = supportPage;
	}

	protected void snapToPage(int pageIndex) {
		if (pageIndex < 0) {
			pageIndex = 0;
		} else if (pageIndex >= pageCount) {
			pageIndex = pageCount > 0 ? pageCount - 1 : 0;
		}
		int oldIndex = currentPage;
		int newIndex = pageIndex;
		currentPage = pageIndex;
		if (listener != null) {
			listener.pageChanged(oldIndex, newIndex);
		}
		int newX = currentPage * getWidth();
		int delta = newX - getScrollX();
		int dura = Math.abs(delta) * 2;
		dura = dura > 1000 ? 1000 : dura;
		scroller.startScroll(getScrollX(), getScrollY(), delta, getScrollY(), dura);
		invalidate();
	}

	public List<ItemMeta> getMetaList() {
		return metaList;
	}

	public int getPageCount() {
		return pageCount;
	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			if (selectedMeta != null) {
				View selectedView = selectedMeta.itemView;
				int newX = selectedView.getLeft() - (getScrollX() - scroller.getCurrX());
				int newY = selectedView.getTop() - (getScrollY() - scroller.getCurrY());
				selectedView.layout(newX, newY, newX + selectedView.getWidth(), newY + selectedView.getHeight());
			}
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			postInvalidate();
		}
	}

	public void setInnerTopPadding(int top) {
		innerTopPadding = top;
	}

	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
		removeAllViews();
		firstLayout = true;
		positionList.clear();
		metaList.clear();
		for (int i = 0, len = adapter.getCount(); i < len; i++) {
			final View child = adapter.getView(i, null, this);
			child.setOnTouchListener(new OnTouchListener() {
				float offsetX;
				float offsetY;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					float x = event.getX() + v.getLeft();
					float y = event.getY() + v.getTop();
					gestureDetector.onTouchEvent(event);

					if (listener != null) {
						listener.onItemTouch(v, event);
					}

					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							for (int i = 0, len = metaList.size(); i < len; i++) {
								ItemMeta meta = metaList.get(i);
								if (meta.itemView.equals(child)) {
									clickMeta = meta;
									Rect rect = meta.rect;
									offsetX = rect.left - x;
									offsetY = rect.top - y;
								}
							}
							return true;
						case MotionEvent.ACTION_MOVE:
							if (selectedMeta != null) {
								DragGridView.this.requestDisallowInterceptTouchEvent(true);
								View selectedView = selectedMeta.itemView;
								int newX = (int) (offsetX + x);
								int newY = (int) (offsetY + y);
								selectedView.layout(newX, newY, newX + selectedView.getWidth(), newY + selectedView.getHeight());

								int insertIndex = -1;
								for (int i = 0, len = metaList.size(); i < len; i++) {
									ItemMeta meta = metaList.get(i);
									if (meta.rect.contains((int) x, (int) y)) {
										if (listener != null && listener.ignoreItem(meta)) {
											continue;
										}
										insertIndex = meta.index;
										break;
									}
								}
								if (insertIndex == -1) {
									int size = 0;
									for (ItemMeta meta : metaList) {
										if (meta.itemView.getVisibility() == View.VISIBLE)
											size++;
									}
									insertIndex = size - 1;
								}
								if (insertIndex != selectedMeta.index) {
									int oldIndex = selectedMeta.index;
									int newMoveIndex = insertIndex;
									selectedMeta.changeIndex(newMoveIndex);
									showSelectedBorder(newMoveIndex);
									for (int iMove = Math.min(oldIndex, newMoveIndex); iMove <= Math.max(oldIndex, newMoveIndex); iMove++) {
										final ItemMeta moveMeta = metaList.get(iMove);
										final View moveView = moveMeta.itemView;
										if (moveView.equals(selectedView))
											continue;
										if (oldIndex < newMoveIndex) {
											moveMeta.changeIndex(--moveMeta.index);
										} else {
											moveMeta.changeIndex(++moveMeta.index);
										}
										moveMeta.resetPosition();
									}
								}

								final int rawX = (int) event.getRawX();
								boolean needSwitch = rawX - switchSlop < 0 || rawX + switchSlop > screenWidth;
								if (needSwitch && !isWaitSwitch) {
									isWaitSwitch = true;
									switchRunnable = new Runnable() {
										@Override
										public void run() {
											if (rawX - switchSlop < 0) {
												snapToPage(currentPage - 1);
											} else {
												snapToPage(currentPage + 1);
											}
											postDelayed(switchRunnable, 2000);
										}
									};
									postDelayed(switchRunnable, 1000);
								} else if (!needSwitch && isWaitSwitch) {
									isWaitSwitch = false;
									removeCallbacks(switchRunnable);
								}
								if (listener != null) {
									listener.drag(selectedMeta, event);
								}
							}
							return true;
						case MotionEvent.ACTION_UP:
						case MotionEvent.ACTION_CANCEL:
							if (selectedMeta != null) {
								// final View selectedView =
								// selectedMeta.itemView;
								// final Rect rect = selectedMeta.rect;
								// final int fromLeft = selectedView.getLeft();
								// final int fromTop = selectedView.getTop();
								// ValueAnimator animator =
								// ValueAnimator.ofFloat(0, 1).setDuration(500);
								// animator.addUpdateListener(new
								// ValueAnimator.AnimatorUpdateListener() {
								// @Override
								// public void onAnimationUpdate(ValueAnimator
								// valueAnimator) {
								// float ratio = (Float)
								// valueAnimator.getAnimatedValue();
								// int x = (int) (fromLeft + ratio * (rect.left
								// - fromLeft));
								// int y = (int) (fromTop + ratio * (rect.top -
								// fromTop));
								// selectedView.layout(x, y, x +
								// selectedView.getWidth(), y +
								// selectedView.getHeight());
								// }
								// });
								// if (selectedMeta.animator != null)
								// selectedMeta.animator.cancel();
								// selectedMeta.animator = animator;
								// animator.start();
								if (isWaitSwitch) {
									removeCallbacks(switchRunnable);
								}
								if (listener == null || false == listener.dragEnd(selectedMeta))
									selectedMeta.resetPosition();
								selectedMeta = null;
								hideSelectedBorder();
							}
							clickMeta = null;
					}
					return false;
				}
			});
			addView(child);
			ItemMeta meta = new ItemMeta();
			meta.index = i;
			meta.itemView = child;
			meta.data = adapter.getItem(i);
			metaList.add(meta);
		}
		pageCount = (int) Math.ceil(getChildCount() * 1.0 / (pageRowCount * pageColCount));
	}

	protected void showSelectedBorder(int index) {
		if (selectedBorderView == null) {
			selectedBorderView = new ImageView(getContext());
			int padding = (int) dip2px(getContext(), 5);
			selectedBorderView.setPadding(padding, padding, padding, padding);
//			selectedBorderView.setBackgroundResource(selectedBorderResId);
			selectedBorderView.setBackgroundColor(selectedColor);
			addView(selectedBorderView, 0);
		}
		if (selectedBorderView.getParent() == null) {
			addView(selectedBorderView, 0);
		}
		Rect rect = positionList.get(index);
		selectedBorderView.setVisibility(View.VISIBLE);
		selectedBorderView.layout(rect.left, rect.top, rect.right, rect.bottom);
	}

	public int getCurrentPage() {
		return currentPage;
	}

	protected void hideSelectedBorder() {
		selectedBorderView.setVisibility(View.INVISIBLE);
	}

	public int getPageRowCount() {
		return pageRowCount;
	}

	public void setPageRowCount(int pageRowCount) {
		this.pageRowCount = pageRowCount;
		firstLayout = true;
		requestLayout();
	}

	public int getPageColCount() {
		return pageColCount;
	}

	public void setPageColCount(int pageColCount) {
		this.pageColCount = pageColCount;
		firstLayout = true;
		requestLayout();
	}

	public int getHorizontalSpacing() {
		return horizontalSpacing;
	}

	public void setHorizontalSpacing(int horizontalSpacing) {
		this.horizontalSpacing = horizontalSpacing;
		requestLayout();
	}

	public int getVerticalSpacing() {
		return verticalSpacing;
	}

	public void setVerticalSpacing(int verticalSpacing) {
		this.verticalSpacing = verticalSpacing;
		requestLayout();
	}

	public void setOnDragGridViewListener(DragGridViewListener listener) {
		this.listener = listener;
	}

	public ItemMeta getMetaByData(Object data) {
		for (ItemMeta meta : metaList) {
			if (data.equals(meta.data))
				return meta;
		}
		return null;
	}

	public class ItemMeta {
		public int index;
		public View itemView;
		public Rect rect;
		public ValueAnimator animator;
		public Object data;

		public void changeIndex(int newIndex) {
			index = newIndex;
			rect = positionList.get(newIndex);
			metaList.remove(this);
			metaList.add(newIndex, this);
		}

		public void resetPosition() {
			final Rect tarRect = rect;
			final int fromLeft = itemView.getLeft();
			final int fromTop = itemView.getTop();

			ValueAnimator animator = ValueAnimator.ofFloat(0, 1).setDuration(500);
			animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator valueAnimator) {
					float ratio = (Float) valueAnimator.getAnimatedValue();
					int x = (int) (fromLeft + ratio * (tarRect.left - fromLeft));
					int y = (int) (fromTop + ratio * (tarRect.top - fromTop));
					itemView.layout(x, y, x + itemView.getWidth(), y + itemView.getHeight());
				}
			});
			if (this.animator != null)
				this.animator.cancel();
			this.animator = animator;
			animator.start();
		}
	}

	public static class DragGridViewListener {
		protected boolean ignoreItem(ItemMeta itemMeta) {
			return false;
		}

		protected void pageChanged(int oldIndex, int newIndex) {

		}

		protected void dragBegin(ItemMeta selectedMeta) {

		}

		protected void drag(ItemMeta selectedMeta, MotionEvent event) {

		}

		protected boolean dragEnd(ItemMeta selectedMeta) {
			return false;
		}

		protected void click(ItemMeta clickMeta) {

		}

		protected void onItemTouch(View v, MotionEvent event) {

		}
	}
	
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
}
