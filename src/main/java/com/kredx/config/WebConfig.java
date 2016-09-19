package com.kredx.config;

import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Created by Vibhor on 16/09/16.
 */

@Configuration
@ComponentScan(
        basePackages = "com.kredx.*"
)
@PropertySources({
        @PropertySource("classpath:init.properties")
})
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.mediaType("json", MediaType.APPLICATION_JSON);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp("/WEB-INF/views/", ".jsp");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    }
}
