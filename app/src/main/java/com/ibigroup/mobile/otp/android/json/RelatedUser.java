package com.ibigroup.mobile.otp.android.json;

import java.io.Serializable;

public class RelatedUser implements Serializable {
    private String email;

    //CONFIRMED -> Verified, PENDING->Pending
    private String status;
    private String acceptKey;
    private String nickname;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAcceptKey() {
        return acceptKey;
    }

    public void setAcceptKey(String acceptKey) {
        this.acceptKey = acceptKey;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
