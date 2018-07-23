package com.tutorial.CalendarAPI.configuration;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.TimeZone;

@Configuration
public class CalendarSpringConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder(){
        Jackson2ObjectMapperBuilder jb = new Jackson2ObjectMapperBuilder();
        jb.dateFormat(new ISO8601DateFormat()).timeZone(TimeZone.getDefault());
        return jb;
    }
}
