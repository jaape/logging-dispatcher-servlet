package fi.exampleapplication.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@WebAppConfiguration
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TestControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Test
    public void testControllerReturnsHelloWorld() throws Exception {
        var mvcMock = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        var mvcResult = mvcMock.perform(MockMvcRequestBuilders.get("/test/hello").accept(MediaType.ALL)).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString, is("Hello world"));
    }
}
