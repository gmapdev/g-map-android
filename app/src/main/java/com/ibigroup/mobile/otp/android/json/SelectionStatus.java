package com.ibigroup.mobile.otp.android.json;

import java.io.Serializable;

public class SelectionStatus implements Serializable {

    private Long id;

    private String name;

    private boolean selected = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
