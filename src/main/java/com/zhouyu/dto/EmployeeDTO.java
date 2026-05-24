package com.zhouyu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author shanweidi
 * @since 2026-03-13 15:26
 **/
@Data
@Deprecated
public class EmployeeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    public EmployeeDTO() {
    }

    public EmployeeDTO(BasicInfo staffBasicInfo, HrInfo staffHrmInfo) {
        this.staffBasicInfo = staffBasicInfo;
        this.staffHrmInfo = staffHrmInfo;
    }

    private BasicInfo staffBasicInfo;
    private HrInfo staffHrmInfo;



    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class BasicInfo implements Serializable {
        private static final long serialVersionUID = 3L;


        //员工号
        private String stfNumber;
        private String stfName;
        private String mobileNumber;
        //居民身份证
        private String certificateType;
        private String certificateNumber;
        private String sex;
        private LocalDate birthday;
    }

    @Data
    static class HrInfo implements Serializable {
        private static final long serialVersionUID = 4L;

        //入职日期
        private LocalDate entryDate;
    }
}
