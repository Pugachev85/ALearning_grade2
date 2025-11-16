package com.ALearning_grade2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурационный класс для настройки Spring MVC.
 *
 * @author ALearning_grade2
 * @version 1.0
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * Настраивает mapping для корневого пути.
     *
     * @param registry реестр контроллеров представлений
     */
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/").setViewName("redirect:/users");
            registry.addViewController("/index").setViewName("redirect:/users");
            registry.addViewController("/index.html").setViewName("redirect:/users");
        }

        @Bean
        public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
            return new HiddenHttpMethodFilter();
        }
    }

