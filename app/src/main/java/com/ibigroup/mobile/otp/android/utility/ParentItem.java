package com.ibigroup.mobile.otp.android.utility;

import java.util.List;

public class ParentItem {
    private final String name;
    private final int iconResId;
    private boolean expanded;

    private boolean isCheck = false;
    private boolean checkedTemp = false;

    private List<ChildItem> children;

    public ParentItem(String name, int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
        this.expanded = false; // Initially, the parent item is not expanded
    }

    public ParentItem(String name, List<ChildItem> children, int iconId) {
        this.name = name;
        this.children = children;
        iconResId = iconId;
        this.expanded = false;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isCheckedTemp() {
        return checkedTemp;
    }

    public void setCheckedTemp(boolean checkedTemp) {
        this.checkedTemp = checkedTemp;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public List<ChildItem> getChildren() {
        return children;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    private boolean toggleChecked = false; // Default is unchecked

    public boolean isToggleChecked() {
        return toggleChecked;
    }

    public void setToggleChecked(boolean toggleChecked) {
        this.toggleChecked = toggleChecked;
    }
}
