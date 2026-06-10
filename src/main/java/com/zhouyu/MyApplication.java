package com.zhouyu;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.OracleKeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zhouyu.utils.LocalDateAdapter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDate;
import java.util.Properties;

/**
 * 作者：单伟迪
 */
@MapperScan("com.zhouyu.mapper")
@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class MyApplication {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.ORACLE));
        return interceptor;
    }


    @Bean
    public IKeyGenerator keyGenerator() {
        return new OracleKeyGenerator();
    }


    @Bean
    public Gson buildGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    @Bean
    public CommandLineRunner commandRun() {
        return args -> {
            Properties properties = System.getProperties();
            properties.list(System.out);

            Runtime rt = Runtime.getRuntime();
            String memoryStr = "JVM最大内存：" + rt.maxMemory()/1024/1024 + "m；JVM总内存：" + rt.totalMemory()/1024/1024 + "m；JVM可用内存：" + rt.freeMemory()/1024/1024 +"m";
            System.out.println(memoryStr);
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            String processorsStr = "availableProcessors：" + availableProcessors;
            System.out.println(processorsStr);
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class);
    }
}
