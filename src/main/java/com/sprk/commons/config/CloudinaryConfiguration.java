package com.sprk.commons.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfiguration {

    private Cloudinary cloudinary;

    @Bean
    public Cloudinary cloudinary(){
        Map<String, String> config = ObjectUtils.asMap(
                "cloud_name", "duttop4n6",
                "api_key", "231688289872219",
                "api_secret", "517o5r81xL9MZuQ7tm06JaUGTMw",
                "secure","true"
        );
        return new Cloudinary(config);
    }




}
