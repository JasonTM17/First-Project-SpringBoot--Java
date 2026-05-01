package vn.hoidanit.laptopshop.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public ViewResolver viewResolver() {
        final InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setViewClass(JstlView.class);
        bean.setPrefix("/WEB-INF/view/");
        bean.setSuffix(".jsp");
        return bean;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(viewResolver());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        CacheControl staticCache = CacheControl.maxAge(Duration.ofDays(30)).cachePublic();
        registry.addResourceHandler("/css/**").addResourceLocations("/resources/css/").setCacheControl(staticCache);
        registry.addResourceHandler("/js/**").addResourceLocations("/resources/js/").setCacheControl(staticCache);
        registry.addResourceHandler("/images/**").addResourceLocations("/resources/images/").setCacheControl(staticCache);
        registry.addResourceHandler("/client/**").addResourceLocations("/resources/client/").setCacheControl(staticCache);
        registry.addResourceHandler("/site.webmanifest")
                .addResourceLocations("/", "/resources/");
    }

}
