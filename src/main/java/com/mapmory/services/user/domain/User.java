package com.mapmory.services.user.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
	
	private String userId;               // user_id VARCHAR(20) NOT NULL
    private String userPassword;         // user_password VARCHAR(120) NOT NULL
    private Byte role;                   // role TINYINT NOT NULL
    private String userName;             // user_name VARCHAR(18) NOT NULL
    private String nickname;             // nickname VARCHAR(10) NOT NULL UNIQUE
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate birthday;          // birthday DATE NOT NULL
    private String email;                // email VARCHAR(254) NOT NULL
    private String phoneNumber;          // phone_number VARCHAR(13) NOT NULL
    private Integer sex = 0;             // sex INT DEFAULT 0
    private LocalDateTime regDate; // reg_date DATETIME DEFAULT CURRENT_TIMESTAMP
    										// 이렇게 작성해도 된다. :: private LocalDateTime regDate = LocalDateTime.now();
    private LocalDateTime updatePasswordDate; // update_password_date DATETIME DEFAULT CURRENT_TIMESTAMP
    private Byte setSecondaryAuth;       // set_secondary_auth TINYINT NOT NULL
    private String profileImageName;     // profile_image_name VARCHAR(254) NOT NULL
    private String introduction;         // introduction VARCHAR(100)
    private Byte hideProfile = 0;        // hide_profile TINYINT DEFAULT 0
    private LocalDateTime leaveAccountDate; // leave_account_date DATETIME
    private LocalDate endSuspensionDate; // end_suspension_date DATE
}
