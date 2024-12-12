package com.identity.lms;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class LibraryManagementSystemApplicationTest implements WithAssertions {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldLoadApplicationContext() {
        assertThat(applicationContext).isNotNull();
    }

}
