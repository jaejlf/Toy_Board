package com.filling.good.domain.user.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Job {

    STUDENT("학생", 0),
    RECRUITER("리크루터", 1),
    FREELANCER("프리랜서", 2),
    DEVELOPER("개발자", 3),
    ETC("기타", 4);

    private final String jobValue;
    private final int jobCode;

    public static Job findJobCode(String jobValue) {
        switch (jobValue) {
            case "학생":
                return STUDENT;
            case "리크루터":
                return RECRUITER;
            case "프리랜서":
                return FREELANCER;
            case "개발자":
                return DEVELOPER;
            default:
                return ETC;
        }
    }

}
