package com.bod.bod.admin.dto;

import com.bod.bod.verification.entity.Verification;
import lombok.Getter;

@Getter
public class AdminVerificationGetResponse {

    private String image;

    private String title;

    private String content;

    public AdminVerificationGetResponse(Verification verification) {
        this.image = verification.getImage();
        this.title = verification.getTitle();
        this.content = verification.getContent();
    }

}
