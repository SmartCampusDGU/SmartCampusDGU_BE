//package org.smartcampus.smartcampus_be.domain.outlier.config;
//
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//@Getter
//@Setter
//@Component
//@ConfigurationProperties(prefix = "outlier")
//public class OutlierProperties {
//
//    private Monitoring monitoring = new Monitoring();
//    private DuplicatePrevention duplicatePrevention = new DuplicatePrevention();
//
//    @Getter
//    @Setter
//    public static class Monitoring {
//        private int durationHours = 24;
//    }
//
//    @Getter
//    @Setter
//    public static class DuplicatePrevention {
//        private int minutes = 180;
//    }
//}