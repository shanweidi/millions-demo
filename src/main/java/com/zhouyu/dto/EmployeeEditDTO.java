package com.zhouyu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author shanweidi
 * @since 2026-03-30 09:21
 **/
@Data
public class EmployeeEditDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<StaffInfo> staffInfoList;

    @Data
    public static class StaffInfo implements Serializable {
        private static final long serialVersionUID = 2L;

        private BasicInfo staffBasicInfo;
        private HrInfo staffHrmInfo;
        private List<WorkInfo> staffWorkInfoList;
        private List<EduInfo> staffEducationInfoList;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicInfo implements Serializable {
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
    public static class HrInfo implements Serializable {
        private static final long serialVersionUID = 4L;

        //入职日期
        private LocalDate entryDate;
    }

    @Data
    public static class WorkInfo implements Serializable {
        private static final long serialVersionUID = 5L;

        //公司名
        private String lastCompanyName;
        //岗位
        private String lastPositionName;

        private LocalDate workBeginDate;
        private LocalDate workEndDate;
    }

    @Data
    public static class EduInfo implements Serializable {
        private static final long serialVersionUID = 6L;

        private String graduateSchool;
        private String specialty;

        private LocalDate graduateDate;
    }
}
