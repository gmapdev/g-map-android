package com.ibigroup.mobile.otp.android.utility;

public class ChildItem {
    private String name;
    private boolean checked;
    private boolean checkedTemp;
    private int iconID;

    public ChildItem(String name) {
        this.name = name;
        this.checked = false; // Initially, the child item is unchecked
    }

    public ChildItem(String name, boolean isCheck) {
        this.name = name;
        this.checked = isCheck;
    }

    public ChildItem(String name, boolean isCheck, int iconID) {
        this.name = name;
        this.checked = isCheck;
        this.iconID = iconID;
    }

    public int getIconID() {
        return iconID;
    }

    public String getName() {
        return name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCheckedTemp() {
        return checkedTemp;
    }

    public void setCheckedTemp(boolean checkedTemp) {
        this.checkedTemp = checkedTemp;
    }
}
