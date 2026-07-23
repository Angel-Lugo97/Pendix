package com.mx.tecdesoftware.Pendix.persistence.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(
        MapperContextTest.MapperTestConfiguration.class
)
class MapperContextTest {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Test
    void shouldLoadMappersAsSpringBeans() {
        assertNotNull(projectMapper);
        assertNotNull(taskMapper);
    }

    @Configuration
    @ComponentScan(basePackageClasses = ProjectMapper.class)
    static class MapperTestConfiguration {
    }
}
