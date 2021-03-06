package com.novas.nongjiuser.uimodel;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.math.BigInteger;

public class novasLayout extends ViewGroup {


    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    public static final int SHOW_DIVIDER_NONE = 0;
    /**
     * Show a divider at the beginning of the group.
     */
    public static final int SHOW_DIVIDER_BEGINNING = 1;
    /**
     * Show dividers between each item in the group.
     */
    public static final int SHOW_DIVIDER_MIDDLE = 2;
    /**
     * Show a divider at the end of the group.
     */
    public static final int SHOW_DIVIDER_END = 4;

    /**
     * Whether the children of this layout are baseline aligned.  Only applicable
     * if {@link #mOrientation} is horizontal.
     */
    @ViewDebug.ExportedProperty(category = "layout")
    private boolean mBaselineAligned = true;

    /**
     * If this layout is part of another layout that is baseline aligned,
     * use the child at this index as the baseline.
     *
     * Note: this is orthogonal to {@link #mBaselineAligned}, which is concerned
     * with whether the children of this layout are baseline aligned.
     */
    @ViewDebug.ExportedProperty(category = "layout")
    private int mBaselineAlignedChildIndex = -1;

    /**
     * The additional offset to the child's baseline.
     * We'll calculate the baseline of this layout as we measure vertically; for
     * horizontal linear layouts, the offset of 0 is appropriate.
     */
    @ViewDebug.ExportedProperty(category = "measurement")
    private int mBaselineChildTop = 0;

    @ViewDebug.ExportedProperty(category = "measurement")
    private int mOrientation;

    @ViewDebug.ExportedProperty(category = "measurement", flagMapping = {
            @ViewDebug.FlagToString(mask = -1,
                    equals = -1, name = "NONE"),
            @ViewDebug.FlagToString(mask = Gravity.NO_GRAVITY,
                    equals = Gravity.NO_GRAVITY,name = "NONE"),
            @ViewDebug.FlagToString(mask = Gravity.TOP,
                    equals = Gravity.TOP, name = "TOP"),
            @ViewDebug.FlagToString(mask = Gravity.BOTTOM,
                    equals = Gravity.BOTTOM, name = "BOTTOM"),
            @ViewDebug.FlagToString(mask = Gravity.LEFT,
                    equals = Gravity.LEFT, name = "LEFT"),
            @ViewDebug.FlagToString(mask = Gravity.RIGHT,
                    equals = Gravity.RIGHT, name = "RIGHT"),
            @ViewDebug.FlagToString(mask = Gravity.START,
                    equals = Gravity.START, name = "START"),
            @ViewDebug.FlagToString(mask = Gravity.END,
                    equals = Gravity.END, name = "END"),
            @ViewDebug.FlagToString(mask = Gravity.CENTER_VERTICAL,
                    equals = Gravity.CENTER_VERTICAL, name = "CENTER_VERTICAL"),
            @ViewDebug.FlagToString(mask = Gravity.FILL_VERTICAL,
                    equals = Gravity.FILL_VERTICAL, name = "FILL_VERTICAL"),
            @ViewDebug.FlagToString(mask = Gravity.CENTER_HORIZONTAL,
                    equals = Gravity.CENTER_HORIZONTAL, name = "CENTER_HORIZONTAL"),
            @ViewDebug.FlagToString(mask = Gravity.FILL_HORIZONTAL,
                    equals = Gravity.FILL_HORIZONTAL, name = "FILL_HORIZONTAL"),
            @ViewDebug.FlagToString(mask = Gravity.CENTER,
                    equals = Gravity.CENTER, name = "CENTER"),
            @ViewDebug.FlagToString(mask = Gravity.FILL,
                    equals = Gravity.FILL, name = "FILL"),
            @ViewDebug.FlagToString(mask = Gravity.RELATIVE_LAYOUT_DIRECTION,
                    equals = Gravity.RELATIVE_LAYOUT_DIRECTION, name = "RELATIVE")
    }, formatToHexString = true)
    private int mGravity = Gravity.START | Gravity.TOP;

    @ViewDebug.ExportedProperty(category = "measurement")
    private int mTotalLength;

    @ViewDebug.ExportedProperty(category = "layout")
    private float mWeightSum;

    @ViewDebug.ExportedProperty(category = "layout")
    private boolean mUseLargestChild;

    private int[] mMaxAscent;
    private int[] mMaxDescent;

    private static final int VERTICAL_GRAVITY_COUNT = 4;

    private static final int INDEX_CENTER_VERTICAL = 0;
    private static final int INDEX_TOP = 1;
    private static final int INDEX_BOTTOM = 2;
    private static final int INDEX_FILL = 3;

    private Drawable mDivider;
    private int mDividerWidth;
    private int mDividerHeight;
    private int mShowDividers;
    private int mDividerPadding;

    public novasLayout(Context context) {
        this(context, null);
    }

    public novasLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public novasLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int index = 1;
        if (index >= 0) {
            setOrientation(index);
        }

        if (index >= 0) {
            setGravity(index);
        }

        boolean baselineAligned = true;
        if (!baselineAligned) {
            setBaselineAligned(baselineAligned);
        }

        mWeightSum = 0.0f;

        mBaselineAlignedChildIndex =-1;

        mUseLargestChild = false;

     //   setDividerDrawable(a.getDrawable(R.styleable.LinearLayout_divider));
        mShowDividers = 0;
        mDividerPadding = 0;
        System.out.println("初始化");
    }

    /**
     * Set how dividers should be shown between items in this layout
     *
     * @param showDividers One or more of {@link #SHOW_DIVIDER_BEGINNING},
     *                     {@link #SHOW_DIVIDER_MIDDLE}, or {@link #SHOW_DIVIDER_END},
     *                     or {@link #SHOW_DIVIDER_NONE} to show no dividers.
     */
    public void setShowDividers(int showDividers) {
        if (showDividers != mShowDividers) {
            requestLayout();
        }
        mShowDividers = showDividers;
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    /**
     * @return A flag set indicating how dividers should be shown around items.
     * @see #setShowDividers(int)
     */
    public int getShowDividers() {
        return mShowDividers;
    }

    /**
     * @return the divider Drawable that will divide each item.
     *
     * @see #setDividerDrawable(Drawable)
     *
     * @attr ref android.R.styleable#LinearLayout_divider
     */
    public Drawable getDividerDrawable() {
        return mDivider;
    }

    /**
     * Set a drawable to be used as a divider between items.
     *
     * @param divider Drawable that will divide each item.
     *
     * @see #setShowDividers(int)
     *
     * @attr ref android.R.styleable#LinearLayout_divider
     */
    public void setDividerDrawable(Drawable divider) {
        if (divider == mDivider) {
            return;
        }
        mDivider = divider;
        if (divider != null) {
            mDividerWidth = divider.getIntrinsicWidth();
            mDividerHeight = divider.getIntrinsicHeight();
        } else {
            mDividerWidth = 0;
            mDividerHeight = 0;
        }
        setWillNotDraw(divider == null);
        requestLayout();
    }

    /**
     * Set padding displayed on both ends of dividers.
     *
     * @param padding Padding value in pixels that will be applied to each end
     *
     * @see #setShowDividers(int)
     * @see #setDividerDrawable(Drawable)
     * @see #getDividerPadding()
     */
    public void setDividerPadding(int padding) {
        mDividerPadding = padding;
    }

    /**
     * Get the padding size used to inset dividers in pixels
     *
     * @see #setShowDividers(int)
     * @see #setDividerDrawable(Drawable)
     * @see #setDividerPadding(int)
     */
    public int getDividerPadding() {
        return mDividerPadding;
    }

    /**
     * Get the width of the current divider drawable.
     *
     * @hide Used internally by framework.
     */
    public int getDividerWidth() {
        return mDividerWidth;
    }
 /*
    @Override
    protected void onDraw(Canvas canvas) {
        System.out.println("in ondraw");
        if (mDivider == null) {
            return;
        }

        if (mOrientation == VERTICAL) {
            drawDividersVertical(canvas);
        } else {
            drawDividersHorizontal(canvas);
        }
    }
*/
    void drawDividersVertical(Canvas canvas) {
        final int count = getVirtualChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getVirtualChildAt(i);

            if (child != null && child.getVisibility() != GONE) {
                if (hasDividerBeforeChildAt(i)) {
                    final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    final int top = child.getTop() - lp.topMargin - mDividerHeight;
                    drawHorizontalDivider(canvas, top);
                }
            }
        }

        if (hasDividerBeforeChildAt(count)) {
            final View child = getVirtualChildAt(count - 1);
            int bottom = 0;
            if (child == null) {
                bottom = getHeight() - getPaddingBottom() - mDividerHeight;
            } else {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                bottom = child.getBottom() + lp.bottomMargin;
            }
            drawHorizontalDivider(canvas, bottom);
        }
    }

    void drawDividersHorizontal(Canvas canvas) {
        final int count = getVirtualChildCount();
        final boolean isLayoutRtl = true;
        for (int i = 0; i < count; i++) {
            final View child = getVirtualChildAt(i);

            if (child != null && child.getVisibility() != GONE) {
                if (hasDividerBeforeChildAt(i)) {
                    final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    final int position;
                    if (isLayoutRtl) {
                        position = child.getRight() + lp.rightMargin;
                    } else {
                        position = child.getLeft() - lp.leftMargin - mDividerWidth;
                    }
                    drawVerticalDivider(canvas, position);
                }
            }
        }

        if (hasDividerBeforeChildAt(count)) {
            final View child = getVirtualChildAt(count - 1);
            int position;
            if (child == null) {
                if (isLayoutRtl) {
                    position = getPaddingLeft();
                } else {
                    position = getWidth() - getPaddingRight() - mDividerWidth;
                }
            } else {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (isLayoutRtl) {
                    position = child.getLeft() - lp.leftMargin - mDividerWidth;
                } else {
                    position = child.getRight() + lp.rightMargin;
                }
            }
            drawVerticalDivider(canvas, position);
        }
    }

    void drawHorizontalDivider(Canvas canvas, int top) {
        mDivider.setBounds(getPaddingLeft() + mDividerPadding, top,
                getWidth() - getPaddingRight() - mDividerPadding, top + mDividerHeight);
        mDivider.draw(canvas);
    }

    void drawVerticalDivider(Canvas canvas, int left) {
        mDivider.setBounds(left, getPaddingTop() + mDividerPadding,
                left + mDividerWidth, getHeight() - getPaddingBottom() - mDividerPadding);
        mDivider.draw(canvas);
    }

    /**
     * <p>Indicates whether widgets contained within this layout are aligned
     * on their baseline or not.</p>
     *
     * @return true when widgets are baseline-aligned, false otherwise
     */
    public boolean isBaselineAligned() {
        return mBaselineAligned;
    }

    /**
     * <p>Defines whether widgets contained in this layout are
     * baseline-aligned or not.</p>
     *
     * @param baselineAligned true to align widgets on their baseline,
     *         false otherwise
     *
     * @attr ref android.R.styleable#LinearLayout_baselineAligned
     */
    public void setBaselineAligned(boolean baselineAligned) {
        mBaselineAligned = baselineAligned;
    }

    /**
     * When true, all children with a weight will be considered having
     * the minimum size of the largest child. If false, all children are
     * measured normally.
     *
     * @return True to measure children with a weight using the minimum
     *         size of the largest child, false otherwise.
     *
     * @attr ref android.R.styleable#LinearLayout_measureWithLargestChild
     */
    public boolean isMeasureWithLargestChildEnabled() {
        return mUseLargestChild;
    }

    /**
     * When set to true, all children with a weight will be considered having
     * the minimum size of the largest child. If false, all children are
     * measured normally.
     *
     * Disabled by default.
     *
     * @param enabled True to measure children with a weight using the
     *        minimum size of the largest child, false otherwise.
     *
     * @attr ref android.R.styleable#LinearLayout_measureWithLargestChild
     */
    public void setMeasureWithLargestChildEnabled(boolean enabled) {
        mUseLargestChild = enabled;
    }

    @Override
    public int getBaseline() {
        if (mBaselineAlignedChildIndex < 0) {
            return super.getBaseline();
        }

        if (getChildCount() <= mBaselineAlignedChildIndex) {
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout "
                    + "set to an index that is out of bounds.");
        }

        final View child = getChildAt(mBaselineAlignedChildIndex);
        final int childBaseline = child.getBaseline();

        if (childBaseline == -1) {
            if (mBaselineAlignedChildIndex == 0) {
                // this is just the default case, safe to return -1
                return -1;
            }
            // the user picked an index that points to something that doesn't
            // know how to calculate its baseline.
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout "
                    + "points to a View that doesn't know how to get its baseline.");
        }

        // TODO: This should try to take into account the virtual offsets
        // (See getNextLocationOffset and getLocationOffset)
        // We should add to childTop:
        // sum([getNextLocationOffset(getChildAt(i)) / i < mBaselineAlignedChildIndex])
        // and also add:
        // getLocationOffset(child)
        int childTop = mBaselineChildTop;

        if (mOrientation == VERTICAL) {
            final int majorGravity = mGravity & Gravity.VERTICAL_GRAVITY_MASK;
            if (majorGravity != Gravity.TOP) {
                switch (majorGravity) {
                    case Gravity.BOTTOM:
                        childTop = this.getBottom() - this.getTop() - this.getPaddingBottom() - mTotalLength;
                        break;

                    case Gravity.CENTER_VERTICAL:
                        childTop += ((this.getBottom() - this.getTop() - this.getPaddingTop() - this.getPaddingBottom()) -
                                mTotalLength) / 2;
                        break;
                }
            }
        }

        novasLayout.LayoutParams lp = (novasLayout.LayoutParams) child.getLayoutParams();
        return childTop + lp.topMargin + childBaseline;
    }

    /**
     * @return The index of the child that will be used if this layout is
     *   part of a larger layout that is baseline aligned, or -1 if none has
     *   been set.
     */
    public int getBaselineAlignedChildIndex() {
        return mBaselineAlignedChildIndex;
    }

    /**
     * @param i The index of the child that will be used if this layout is
     *          part of a larger layout that is baseline aligned.
     *
     * @attr ref android.R.styleable#LinearLayout_baselineAlignedChildIndex
     */
    public void setBaselineAlignedChildIndex(int i) {
        if ((i < 0) || (i >= getChildCount())) {
            throw new IllegalArgumentException("base aligned child index out "
                    + "of range (0, " + getChildCount() + ")");
        }
        mBaselineAlignedChildIndex = i;
    }

    /**
     * <p>Returns the view at the specified index. This method can be overriden
     * to take into account virtual children. Refer to
     * {@link android.widget.TableLayout} and {@link android.widget.TableRow}
     * for an example.</p>
     *
     * @param index the child's index
     * @return the child at the specified index
     */
    View getVirtualChildAt(int index) {
        return getChildAt(index);
    }

    /**
     * <p>Returns the virtual number of children. This number might be different
     * than the actual number of children if the layout can hold virtual
     * children. Refer to
     * {@link android.widget.TableLayout} and {@link android.widget.TableRow}
     * for an example.</p>
     *
     * @return the virtual number of children
     */
    int getVirtualChildCount() {
        return getChildCount();
    }

    /**
     * Returns the desired weights sum.
     *
     * @return A number greater than 0.0f if the weight sum is defined, or
     *         a number lower than or equals to 0.0f if not weight sum is
     *         to be used.
     */
    public float getWeightSum() {
        return mWeightSum;
    }

    /**
     * Defines the desired weights sum. If unspecified the weights sum is computed
     * at layout time by adding the layout_weight of each child.
     *
     * This can be used for instance to give a single child 50% of the total
     * available space by giving it a layout_weight of 0.5 and setting the
     * weightSum to 1.0.
     *
     * @param weightSum a number greater than 0.0f, or a number lower than or equals
     *        to 0.0f if the weight sum should be computed from the children's
     *        layout_weight
     */
    public void setWeightSum(float weightSum) {
        mWeightSum = Math.max(0.0f, weightSum);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        System.out.println("in onmeasure");
        System.out.println("orientation="+mOrientation);
        mOrientation=1;
        if (mOrientation == VERTICAL) {
            measureVertical(widthMeasureSpec, heightMeasureSpec);
        } else {
        }
    }

    /**
     * Determines where to position dividers between children.
     *
     * @param childIndex Index of child to check for preceding divider
     * @return true if there should be a divider before the child at childIndex
     * @hide Pending API consideration. Currently only used internally by the system.
     */
    protected boolean hasDividerBeforeChildAt(int childIndex) {
        if (childIndex == 0) {
            return (mShowDividers & SHOW_DIVIDER_BEGINNING) != 0;
        } else if (childIndex == getChildCount()) {
            return (mShowDividers & SHOW_DIVIDER_END) != 0;
        } else if ((mShowDividers & SHOW_DIVIDER_MIDDLE) != 0) {
            boolean hasVisibleViewBefore = false;
            for (int i = childIndex - 1; i >= 0; i--) {
                if (getChildAt(i).getVisibility() != GONE) {
                    hasVisibleViewBefore = true;
                    break;
                }
            }
            return hasVisibleViewBefore;
        }
        return false;
    }

    /**
     * Measures the children when the orientation of this LinearLayout is set
     * to {@link #VERTICAL}.
     *
     *
     * @see #getOrientation()
     * @see #setOrientation(int)
     * @see #onMeasure(int, int)
     */
    public  int resolveSizeAndStatea(int size, int measureSpec, int childMeasuredState) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize =  MeasureSpec.getSize(measureSpec);
        System.out.println("specsize="+specSize+"   "+size+"   "+childMeasuredState);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                System.out.println("            case MeasureSpec.UNSPECIFIED:\n");
                result = size;
                break;
            case MeasureSpec.AT_MOST:
                System.out.println("            case MeasureSpec.AT_MOST:\n");
                if (specSize < size) {
                    result = specSize | MEASURED_STATE_TOO_SMALL;
                } else {
                    result = size;
                }
                break;
            case MeasureSpec.EXACTLY:
                System.out.println("            case MeasureSpec.EXACTLY:\n");
                result = specSize;
                break;
        }
        return result | (childMeasuredState&MEASURED_STATE_MASK);
    }
    void measureVertical(int widthMeasureSpec, int heightMeasureSpec) {
        System.out.println("  fasdfdsaf   "+widthMeasureSpec+"   "+heightMeasureSpec);
        mTotalLength = 0;
        int maxWidth = 0;
        int childState = 0;
        int alternativeMaxWidth = 0;
        int weightedMaxWidth = 0;
        boolean allFillParent = true;
        float totalWeight = 0;

        final int count = getVirtualChildCount();

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        boolean matchWidth = false;
        boolean skippedMeasure = false;

        final int baselineChildIndex = mBaselineAlignedChildIndex;
        final boolean useLargestChild = mUseLargestChild;
        System.out.println("  "+count+"   "+widthMode+"   "+heightMode+"  "+baselineChildIndex+"   "+useLargestChild );
        int largestChildHeight = Integer.MIN_VALUE;

        // See how tall everyone is. Also remember max width.

        for (int i = 0; i < count; ++i)
        {
            final View child = getVirtualChildAt(i);

            if (child == null) {
                mTotalLength += measureNullChild(i);
                continue;
            }

            if (child.getVisibility() == View.GONE) {
              //  i += getChildrenSkipCount(child, i);
                continue;
            }

            if (hasDividerBeforeChildAt(i)) {
                mTotalLength += mDividerHeight;
            }

            ViewGroup.LayoutParams params=child.getLayoutParams();
            novasLayout.LayoutParams lp =(novasLayout.LayoutParams) params;

            totalWeight += lp.weight;
            System.out.println("weight="+totalWeight+" totallength=  "+mTotalLength);
            System.out.println("heightMode == MeasureSpec.EXACTLY="+(heightMode == MeasureSpec.EXACTLY));
            /*
            这个是根据父亲容器来确定的，而不是根据child来确定的

             */
            if (heightMode == MeasureSpec.EXACTLY && lp.height == 0 && lp.weight > 0) {
                System.out.println("exactly");
                // Optimization: don't bother measuring children who are going to use
                // leftover space. These views will get measured again down below if
                // there is any leftover space.
                final int totalLength = mTotalLength;
                mTotalLength = Math.max(totalLength, totalLength + lp.topMargin + lp.bottomMargin);
                skippedMeasure = true;
            } else {
                System.out.println("measure");

                int oldHeight = Integer.MIN_VALUE;

                if (lp.height == 0 && lp.weight > 0) {
                    // heightMode is either UNSPECIFIED or AT_MOST, and this
                    // child wanted to stretch to fill available space.
                    // Translate that to WRAP_CONTENT so that it does not end up
                    // with a height of 0
                    oldHeight = 0;
                    lp.height = LayoutParams.WRAP_CONTENT;
                }

                // Determine how big this child would like to be. If this or
                // previous children have given a weight, then we allow it to
                // use all available space (and we will shrink things later
                // if needed).
                measureChildBeforeLayout(
                        child, i, widthMeasureSpec, 0, heightMeasureSpec,
                        totalWeight == 0 ? mTotalLength : 0);

                if (oldHeight != Integer.MIN_VALUE) {
                    lp.height = oldHeight;
                }
                System.out.println("layoutparams="+lp.bottomMargin+"   "+lp.topMargin);
                final int childHeight = child.getMeasuredHeight();
                System.out.println("childrenheight="+childHeight);
                final int totalLength = mTotalLength;
                System.out.println("totallength="+mTotalLength);
                mTotalLength = Math.max(totalLength, totalLength + childHeight + lp.topMargin +
                        lp.bottomMargin );
                System.out.println("totallength="+mTotalLength+"   "+childHeight+"   "+lp.topMargin+"   "+lp.bottomMargin+"   ");

                if (useLargestChild) {
                    largestChildHeight = Math.max(childHeight, largestChildHeight);
                }
            }

            /**
             * If applicable, compute the additional offset to the child's baseline
             * we'll need later when asked {@link #getBaseline}.
             */
            if ((baselineChildIndex >= 0) && (baselineChildIndex == i + 1)) {
                mBaselineChildTop = mTotalLength;
            }

            // if we are trying to use a child index for our baseline, the above
            // book keeping only works if there are no children above it with
            // weight.  fail fast to aid the developer.
            if (i < baselineChildIndex && lp.weight > 0) {
                throw new RuntimeException("A child of LinearLayout with index "
                        + "less than mBaselineAlignedChildIndex has weight > 0, which "
                        + "won't work.  Either remove the weight, or don't set "
                        + "mBaselineAlignedChildIndex.");
            }

            boolean matchWidthLocally = false;
            if (widthMode != MeasureSpec.EXACTLY && lp.width == LayoutParams.MATCH_PARENT) {
                // The width of the linear layout will scale, and at least one
                // child said it wanted to match our width. Set a flag
                // indicating that we need to remeasure at least that view when
                // we know our width.
                System.out.println("widthmode=");
                matchWidth = true;
                matchWidthLocally = true;
            }

            final int margin = lp.leftMargin + lp.rightMargin;
            final int measuredWidth = child.getMeasuredWidth() + margin;
            maxWidth = Math.max(maxWidth, measuredWidth);
            System.out.println("maxwidth="+maxWidth);
            childState = combineMeasuredStates(childState, child.getMeasuredState());

            allFillParent = allFillParent && lp.width == LayoutParams.MATCH_PARENT;
            System.out.println("lp.weight="+lp.weight);
            if (lp.weight > 0) {
                /*
                 * Widths of weighted Views are bogus if we end up
                 * remeasuring, so keep them separate.
                 */
                weightedMaxWidth = Math.max(weightedMaxWidth,
                        matchWidthLocally ? margin : measuredWidth);
            } else {
                alternativeMaxWidth = Math.max(alternativeMaxWidth,
                        matchWidthLocally ? margin : measuredWidth);
            }

            i += getChildrenSkipCount(child, i);
        }

        if (mTotalLength > 0 && hasDividerBeforeChildAt(count)) {
            mTotalLength += mDividerHeight;
        }
        System.out.println("useLargestChild="+useLargestChild);
        if (useLargestChild &&
                (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED)) {
            mTotalLength = 0;

            for (int i = 0; i < count; ++i) {
                final View child = getVirtualChildAt(i);

                if (child == null) {
                    mTotalLength += measureNullChild(i);
                    continue;
                }

                if (child.getVisibility() == GONE) {
                    i += getChildrenSkipCount(child, i);
                    continue;
                }

                final novasLayout.LayoutParams lp = (novasLayout.LayoutParams)
                        child.getLayoutParams();
                // Account for negative margins
                final int totalLength = mTotalLength;
                mTotalLength = Math.max(totalLength, totalLength + largestChildHeight +
                        lp.topMargin + lp.bottomMargin);
            }
        }
        System.out.println("paddingTop="+this.getPaddingTop()+"   "+this.getPaddingBottom());
        // Add in our padding
        mTotalLength += this.getPaddingTop() + this.getPaddingBottom();

        int heightSize = mTotalLength;

        // Check against our minimum height
        heightSize = Math.max(heightSize, getSuggestedMinimumHeight());

        // Reconcile our calculated size with the heightMeasureSpec
        /*

        获取整个layout的大小
         */
        int heightSizeAndState = resolveSizeAndStatea(heightSize, heightMeasureSpec, 0);
        System.out.println("heightsize="+heightSize+"    "+" heightsizeand state=  "+heightSizeAndState);
        heightSize = heightSizeAndState & MEASURED_SIZE_MASK;
        // Either expand children with weight to take up available space or
        // shrink them if they extend beyond our current bounds. If we skipped
        // measurement on any children, we need to measure them now.
        int delta = heightSize - mTotalLength;
        System.out.println("heightsize="+heightSize+"   "+delta+"   "+skippedMeasure);
      //  totalWeight=0;
        if (skippedMeasure || delta != 0 && totalWeight > 0.0f) {
            System.out.println("in for"+totalWeight+"   "+mWeightSum);
            float weightSum = mWeightSum > 0.0f ? mWeightSum : totalWeight;

            mTotalLength = 0;

            for (int i = 0; i < count; ++i) {
                final View child = getVirtualChildAt(i);

                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                novasLayout.LayoutParams lp = (novasLayout.LayoutParams) child.getLayoutParams();

                float childExtra = lp.weight;
                System.out.println("childextra="+childExtra);
                if (childExtra > 0) {
                    // Child said it could absorb extra space -- give him his share
                    int share = (int) (childExtra * delta / weightSum);
                    weightSum -= childExtra;
                    delta -= share;
                    System.out.println("share="+share+"   "+weightSum+"   "+delta);
                    final int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                            this.getPaddingLeft() + this.getPaddingRight() +
                                    lp.leftMargin + lp.rightMargin, lp.width);

                    // TODO: Use a field like lp.isMeasured to figure out if this
                    // child has been previously measured
                    if ((lp.height != 0) || (heightMode != MeasureSpec.EXACTLY)) {
                        // child was measured once already above...
                        // base new measurement on stored values
                        int childHeight = child.getMeasuredHeight() + share;
                        if (childHeight < 0) {
                            childHeight = 0;
                        }
                        System.out.println("childheight="+childHeight);
                        child.measure(childWidthMeasureSpec,
                                MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY));
                    } else {
                        // child was skipped in the loop above.
                        // Measure for this first time here      
                        child.measure(childWidthMeasureSpec,
                                MeasureSpec.makeMeasureSpec(share > 0 ? share : 0,
                                        MeasureSpec.EXACTLY));
                    }

                    // Child may now not fit in vertical dimension.
                    childState = combineMeasuredStates(childState, child.getMeasuredState()
                            & (MEASURED_STATE_MASK>>MEASURED_HEIGHT_STATE_SHIFT));
                }

                final int margin =  lp.leftMargin + lp.rightMargin;
                final int measuredWidth = child.getMeasuredWidth() + margin;
                maxWidth = Math.max(maxWidth, measuredWidth);

                boolean matchWidthLocally = widthMode != MeasureSpec.EXACTLY &&
                        lp.width == LayoutParams.MATCH_PARENT;

                alternativeMaxWidth = Math.max(alternativeMaxWidth,
                        matchWidthLocally ? margin : measuredWidth);

                allFillParent = allFillParent && lp.width == LayoutParams.MATCH_PARENT;

                final int totalLength = mTotalLength;
                mTotalLength = Math.max(totalLength, totalLength + child.getMeasuredHeight() +
                        lp.topMargin + lp.bottomMargin);
            }

            // Add in our padding
            mTotalLength += this.getPaddingTop() + this.getPaddingBottom();
            // TODO: Should we recompute the heightSpec based on the new total length?
        } else {
            alternativeMaxWidth = Math.max(alternativeMaxWidth,
                    weightedMaxWidth);


            // We have no limit, so make all weighted views as tall as the largest child.
            // Children will have already been measured once.
            if (useLargestChild && heightMode != MeasureSpec.EXACTLY) {
                for (int i = 0; i < count; i++) {
                    final View child = getVirtualChildAt(i);

                    if (child == null || child.getVisibility() == View.GONE) {
                        continue;
                    }

                    final novasLayout.LayoutParams lp =
                            (novasLayout.LayoutParams) child.getLayoutParams();

                    float childExtra = lp.weight;
                    if (childExtra > 0) {
                        child.measure(
                                MeasureSpec.makeMeasureSpec(child.getMeasuredWidth(),
                                        MeasureSpec.EXACTLY),
                                MeasureSpec.makeMeasureSpec(largestChildHeight,
                                        MeasureSpec.EXACTLY));
                    }
                }
            }
        }

        if (!allFillParent && widthMode != MeasureSpec.EXACTLY) {
            maxWidth = alternativeMaxWidth;
        }

        maxWidth += this.getPaddingLeft() + this.getPaddingRight();

        // Check against our minimum width
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());
        System.out.println("setMeasuredDimension  "+heightSizeAndState);
        setMeasuredDimension(resolveSizeAndStatea(maxWidth, widthMeasureSpec, childState),
                heightSizeAndState);

        if (matchWidth) {
            forceUniformWidth(count, heightMeasureSpec);
        }
    }

    private void forceUniformWidth(int count, int heightMeasureSpec) {
        // Pretend that the linear layout has an exact size.
        int uniformMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(),
                MeasureSpec.EXACTLY);
        for (int i = 0; i< count; ++i) {
            final View child = getVirtualChildAt(i);
            if (child.getVisibility() != GONE) {
                novasLayout.LayoutParams lp = ((novasLayout.LayoutParams)child.getLayoutParams());

                if (lp.width == LayoutParams.MATCH_PARENT) {
                    // Temporarily force children to reuse their old measured height
                    // FIXME: this may not be right for something like wrapping text?
                    int oldHeight = lp.height;
                    lp.height = child.getMeasuredHeight();

                    // Remeasue with new dimensions
                    measureChildWithMargins(child, uniformMeasureSpec, 0, heightMeasureSpec, 0);
                    lp.height = oldHeight;
                }
            }
        }
    }

    /**
     * <p>Returns the number of children to skip after measuring/laying out
     * the specified child.</p>
     *
     * @param child the child after which we want to skip children
     * @param index the index of the child after which we want to skip children
     * @return the number of children to skip, 0 by default
     */
    int getChildrenSkipCount(View child, int index) {
        return 0;
    }

    /**
     * <p>Returns the size (width or height) that should be occupied by a null
     * child.</p>
     *
     * @param childIndex the index of the null child
     * @return the width or height of the child depending on the orientation
     */
    int measureNullChild(int childIndex) {
        return 0;
    }


    void measureChildBeforeLayout(View child, int childIndex,
                                  int widthMeasureSpec, int totalWidth, int heightMeasureSpec,
                                  int totalHeight) {
        System.out.println("measure children="+widthMeasureSpec+"   "+totalWidth+"  "+totalHeight+"   "+heightMeasureSpec);
        measureChildWithMargins(child, widthMeasureSpec, totalWidth,
                heightMeasureSpec, totalHeight);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        System.out.println("in onlayout"+"   "+l+"   "+t+"   "+r+"   "+b);

        if (mOrientation == VERTICAL) {
            layoutVertical(l, t, r, b);
        } else {
        }
    }

    /**
     * Position the children during a layout pass if the orientation of this
     * LinearLayout is set to {@link #VERTICAL}.
     *
     * @see #getOrientation()
     * @see #setOrientation(int)
     * @see #onLayout(boolean, int, int, int, int)
     * @param left
     * @param top
     * @param right
     * @param bottom
     * right表示view的width，bottom表示view的height
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    void layoutVertical(int left, int top, int right, int bottom) {
        final int paddingLeft = this.getPaddingLeft();

        int childTop;
        int childLeft;

        // Where right end of child should go
        final int width = right - left;
        /*

         childright表示的是父亲layout的右侧边界，而不layout里面包含的child边界
         */
        int parentRight = width - this.getPaddingRight();
        System.out.println("childright="+parentRight);
        // Space available for child
        int childSpace = width - paddingLeft - this.getPaddingRight();

        final int count = getVirtualChildCount();

        final int majorGravity = mGravity & Gravity.VERTICAL_GRAVITY_MASK;
        final int minorGravity = mGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;

        switch (majorGravity) {
            case Gravity.BOTTOM:
                System.out.println("            case Gravity.BOTTOM:\n");
                // mTotalLength contains the padding already
                childTop = this.getPaddingTop() + bottom - top - mTotalLength;
                break;

            // mTotalLength contains the padding already
            case Gravity.CENTER_VERTICAL:
                System.out.println("            case Gravity.CENTER_VERTICAL:\n");
                childTop = this.getPaddingTop() + (bottom - top - mTotalLength) / 2;
                break;

            case Gravity.TOP:
                System.out.println("            case Gravity.TOP:\n");
            default:
                System.out.println("            default:\n");
                childTop = this.getPaddingTop();
                break;
        }
        System.out.println("this.getPaddingTop="+this.getPaddingTop());
        for (int i = 0; i < count; i++) {
            final View child = getVirtualChildAt(i);
            if (child == null) {
                childTop += measureNullChild(i);
            } else if (child.getVisibility() != GONE) {
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();

                final novasLayout.LayoutParams lp =
                        (novasLayout.LayoutParams) child.getLayoutParams();

                int gravity = lp.gravity;
                System.out.println("gravity     child="+gravity);
                if (gravity < 0) {
                    gravity = minorGravity;
                }
                /*

                设备横屏竖屏的判断
                 */
                final int layoutDirection = getLayoutDirection();
                 int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
                /*
                我认为在竖屏的情况下absoluteGravity=gravity
                 */
                absoluteGravity=gravity;
                System.out.println("gravity="+(absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK)+"    "+absoluteGravity);
                System.out.println("Gravity_center"+Gravity.CENTER_HORIZONTAL+"   "+Gravity.CENTER+"   "+Gravity.TOP+"   "+Gravity.BOTTOM);
                System.out.println(Gravity.HORIZONTAL_GRAVITY_MASK);
                switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                    case Gravity.CENTER_HORIZONTAL:
                        System.out.println("                    case Gravity.CENTER_HORIZONTAL:\n"+"   "+Gravity.CENTER_HORIZONTAL);
                        childLeft = paddingLeft + ((childSpace - childWidth) / 2)
                                + lp.leftMargin - lp.rightMargin;
                        break;

                    case Gravity.RIGHT:
                        childLeft = parentRight - childWidth - lp.rightMargin;
                        break;

                    case Gravity.LEFT:
                    default:
                        childLeft = paddingLeft + lp.leftMargin;
                        break;
                }

                if (hasDividerBeforeChildAt(i)) {
                    childTop += mDividerHeight;
                }

                childTop += lp.topMargin;
                setChildFrame(child, childLeft, childTop,
                        childWidth, childHeight);
                childTop += childHeight + lp.bottomMargin;

                i += getChildrenSkipCount(child, i);
            }
        }
    }


    private void setChildFrame(View child, int left, int top, int width, int height) {
        System.out.println("setChildFrame "+left+"  "+top+"   "+width+"   "+height);
        child.layout(left, top, left + width, top + height);
    }

    /**
     * Should the layout be a column or a row.
     * @param orientation Pass {@link #HORIZONTAL} or {@link #VERTICAL}. Default
     * value is {@link #HORIZONTAL}.
     *
     * @attr ref android.R.styleable#LinearLayout_orientation
     */
    public void setOrientation(int orientation) {
        if (mOrientation != orientation) {
            mOrientation = orientation;
            requestLayout();
        }
    }

    /**
     * Returns the current orientation.
     *
     * @return either {@link #HORIZONTAL} or {@link #VERTICAL}
     */
    public int getOrientation() {
        return mOrientation;
    }

    /**
     * Describes how the child views are positioned. Defaults to GRAVITY_TOP. If
     * this layout has a VERTICAL orientation, this controls where all the child
     * views are placed if there is extra vertical space. If this layout has a
     * HORIZONTAL orientation, this controls the alignment of the children.
     *
     * @param gravity See {@link android.view.Gravity}
     *
     * @attr ref android.R.styleable#LinearLayout_gravity
     */
    public void setGravity(int gravity) {
        if (mGravity != gravity) {
            if ((gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
                gravity |= Gravity.START;
            }

            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == 0) {
                gravity |= Gravity.TOP;
            }

            mGravity = gravity;
            requestLayout();
        }
    }

    public void setHorizontalGravity(int horizontalGravity) {
        final int gravity = horizontalGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        if ((mGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) != gravity) {
            mGravity = (mGravity & ~Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) | gravity;
            requestLayout();
        }
    }

    public void setVerticalGravity(int verticalGravity) {
        final int gravity = verticalGravity & Gravity.VERTICAL_GRAVITY_MASK;
        if ((mGravity & Gravity.VERTICAL_GRAVITY_MASK) != gravity) {
            mGravity = (mGravity & ~Gravity.VERTICAL_GRAVITY_MASK) | gravity;
            requestLayout();
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        System.out.println("    public LayoutParams generateLayoutParams(AttributeSet attrs) {\n");
        return new novasLayout.LayoutParams(getContext(), attrs);
    }

    /**
     * Returns a set of layout parameters with a width of
     * {@link android.view.ViewGroup.LayoutParams#MATCH_PARENT}
     * and a height of {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}
     * when the layout's orientation is {@link #VERTICAL}. When the orientation is
     * {@link #HORIZONTAL}, the width is set to {@link LayoutParams#WRAP_CONTENT}
     * and the height to {@link LayoutParams#WRAP_CONTENT}.
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        System.out.println("    protected LayoutParams generateDefaultLayoutParams() {\n");
        if (mOrientation == HORIZONTAL) {
            return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        } else if (mOrientation == VERTICAL) {
            return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        return null;
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        System.out.println("       protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {\n");
        return new LayoutParams(p);
    }


    // Override to allow type-checking of LayoutParams.
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof novasLayout.LayoutParams;
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(novasLayout.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(novasLayout.class.getName());
    }

    /**
     * Per-child layout information associated with ViewLinearLayout.
     *
     * @attr ref android.R.styleable#LinearLayout_Layout_layout_weight
     * @attr ref android.R.styleable#LinearLayout_Layout_layout_gravity
     */
    public  class LayoutParams extends ViewGroup.MarginLayoutParams {
        /**
         * Indicates how much of the extra space in the LinearLayout will be
         * allocated to the view associated with these LayoutParams. Specify
         * 0 if the view should not be stretched. Otherwise the extra pixels
         * will be pro-rated among all views whose weight is greater than 0.
         */
        @ViewDebug.ExportedProperty(category = "layout")
        public float weight;

        /**
         * Gravity for the view associated with these LayoutParams.
         *
         * @see android.view.Gravity
         */
        @ViewDebug.ExportedProperty(category = "layout", mapping = {
                @ViewDebug.IntToString(from =  -1,                       to = "NONE"),
                @ViewDebug.IntToString(from = Gravity.NO_GRAVITY,        to = "NONE"),
                @ViewDebug.IntToString(from = Gravity.TOP,               to = "TOP"),
                @ViewDebug.IntToString(from = Gravity.BOTTOM,            to = "BOTTOM"),
                @ViewDebug.IntToString(from = Gravity.LEFT,              to = "LEFT"),
                @ViewDebug.IntToString(from = Gravity.RIGHT,             to = "RIGHT"),
                @ViewDebug.IntToString(from = Gravity.START,            to = "START"),
                @ViewDebug.IntToString(from = Gravity.END,             to = "END"),
                @ViewDebug.IntToString(from = Gravity.CENTER_VERTICAL,   to = "CENTER_VERTICAL"),
                @ViewDebug.IntToString(from = Gravity.FILL_VERTICAL,     to = "FILL_VERTICAL"),
                @ViewDebug.IntToString(from = Gravity.CENTER_HORIZONTAL, to = "CENTER_HORIZONTAL"),
                @ViewDebug.IntToString(from = Gravity.FILL_HORIZONTAL,   to = "FILL_HORIZONTAL"),
                @ViewDebug.IntToString(from = Gravity.CENTER,            to = "CENTER"),
                @ViewDebug.IntToString(from = Gravity.FILL,              to = "FILL")
        })
        public int gravity = -1;

        /**
         * {@inheritDoc}
         */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            System.out.println(" public LayoutParams(Context c, AttributeSet attrs)     " + attrs.getAttributeName(2));
            weight = 0;
            /*
            这段代码可以获取attr的所有属性值
            <com.novas.nongjiuser.uimodel.novasLayout xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:tools="http://schemas.android.com/tools" android:layout_width="fill_parent"
             android:layout_height="wrap_content" android:paddingLeft="@dimen/activity_horizontal_margin"
             android:paddingRight="@dimen/activity_horizontal_margin"
               android:paddingTop="@dimen/activity_vertical_margin"
               android:layout_gravity="center_vertical"
             android:orientation="vertical"
             android:background="@color/joinme_common_shadow"
           android:paddingBottom="@dimen/activity_vertical_margin" tools:context=".MainActivity">

           <com.novas.nongjiuser.uimodel.MyButton android:text="@string/hello_world" android:layout_width="wrap_content"
           android:layout_marginTop="10dp"
           android:layout_marginBottom="10dp"
           android:layout_gravity="right"
           android:layout_height="wrap_content" />
            <TextView android:text="@string/hello_world" android:layout_width="wrap_content"
            android:layout_height="wrap_content" />

             </com.novas.nongjiuser.uimodel.novasLayout>


           以上面这个xml为例，xmlns:android="http://schemas.android.com/apk/res/android" 这个叫做命名空间，如果自定义属性，必须指定；
           layout_width这种东西叫做数值，这些都可以通过AttributeSet获取到。下面这段代码可以获取layout文件中，定义的所有属性值


            int count=attrs.getAttributeCount();
            for(int i=0;i<attrs.getAttributeCount();i++)
            {
                String key=attrs.getAttributeName(i);
                String value=attrs.getAttributeValue(i);
                if(key.equals("layout_gravity"))
                {
                    gravity=Integer.parseInt(value.substring(2));
                }
                System.out.println(key+"   "+value);
            }
            */
            /*
            2是表示当没有定义layout_gravity属性的时候，默认是2
             */
            gravity=attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "layout_gravity", 2);
           System.out.println("属性值:"+ attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "layout_gravity", 2));
          //  System.out.println(attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_width"));

        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(int width, int height) {
            super(width, height);
            System.out.println(" public LayoutParams(int width, int height) {");

            weight = 0;
        }

        /**
         * Creates a new set of layout parameters with the specified width, height
         * and weight.
         *
         * @param width the width, either {@link #MATCH_PARENT},
         *        {@link #WRAP_CONTENT} or a fixed size in pixels
         * @param height the height, either {@link #MATCH_PARENT},
         *        {@link #WRAP_CONTENT} or a fixed size in pixels
         * @param weight the weight
         */
        public LayoutParams(int width, int height, float weight) {
            super(width, height);
            this.weight = weight;
            System.out.println("        public LayoutParams(int width, int height, float weight) {\n");
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
            System.out.println("  public LayoutParams(ViewGroup.LayoutParams p) {");
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
            System.out.println(" public LayoutParams(ViewGroup.MarginLayoutParams source)");
        }

        /**
         * Copy constructor. Clones the width, height, margin values, weight,
         * and gravity of the source.
         *
         * @param source The layout params to copy from.
         */
        public LayoutParams(LayoutParams source) {
            super(source);
            System.out.println("public LayoutParams(LayoutParams source)");
            this.weight = source.weight;
            this.gravity = source.gravity;
        }


    }
}
