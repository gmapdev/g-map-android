package com.ibigroup.mobile.otp.android.customize;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;

public class DynamicHeightExpandableListView extends ExpandableListView {

    public DynamicHeightExpandableListView(Context context) {
        super(context);
    }

    public DynamicHeightExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicHeightExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Capture the original heightMeasureSpec
        int initialHeightMeasureSpec = heightMeasureSpec;

        // Expand the heightMeasureSpec to allow the ExpandableListView to expand
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);

        // Measure the ExpandableListView with the expanded heightMeasureSpec
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // If the ExpandableListView is expanded, calculate the total height of its child items
        int totalHeight = 0;
        for (int i = 0; i < getExpandableListAdapter().getGroupCount(); i++) {
            View groupView = getExpandableListAdapter().getGroupView(i, isGroupExpanded(i), null, this);
            groupView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            totalHeight += groupView.getMeasuredHeight();

            if (isGroupExpanded(i)) {
                for (int j = 0; j < getExpandableListAdapter().getChildrenCount(i); j++) {
                    View childView = getExpandableListAdapter().getChildView(i, j, false, null, this);
                    childView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                    totalHeight += childView.getMeasuredHeight();
                }
            }
        }

        // Set the measured height of the ExpandableListView
        setMeasuredDimension(getMeasuredWidth(), totalHeight);
    }
}
