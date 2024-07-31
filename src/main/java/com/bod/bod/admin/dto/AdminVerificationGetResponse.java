package com.bod.bod.admin.dto;

import com.bod.bod.verification.entity.Status;
import com.bod.bod.verification.entity.Verification;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class AdminVerificationGetResponse {

    private Long verificationId;

    private String user;

    private LocalDateTime createdAt;

    private String image;

    private String title;

    private Status status;

    public AdminVerificationGetResponse(Verification verification) {
        this.verificationId = verification.getId();
        this.user = verification.getUser().getUsername();
        this.createdAt = verification.getCreatedAt();
        this.image = verification.getImageUrl();
        this.title = verification.getTitle();
        this.status = verification.getStatus();
    }

}
