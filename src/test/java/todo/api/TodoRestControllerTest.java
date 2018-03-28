package todo.api;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.terasoluna.gfw.web.logging.mdc.XTrackMDCPutFilter;
import todo.api.todo.TodoResource;

@RunWith(SpringRunner.class)
@ContextHierarchy({@ContextConfiguration({"classpath:META-INF/spring/applicationContext.xml"}),
    @ContextConfiguration({"classpath:META-INF/spring/spring-mvc-rest.xml"})})
@WebAppConfiguration
public class TodoRestControllerTest {

  @Autowired
  WebApplicationContext webApplicationContext;

  MockMvc mockMvc;

  ObjectMapper mapper;

  @Before
  public void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .addFilter(new XTrackMDCPutFilter(), "/**").alwaysDo(log()).build();
    mapper = new ObjectMapper();
  }

  @Test
  public void postTodoTest() throws Exception {
    String title = "title";
    TodoResource todoRequest = new TodoResource();
    todoRequest.setTodoTitle(title);
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/todos").content(mapper.writeValueAsString(todoRequest))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated()).andReturn();

    TodoResource todoResponce =
        mapper.readValue(result.getResponse().getContentAsString(), TodoResource.class);
    assertThat(todoResponce.getTodoId(), notNullValue());
    assertThat(todoResponce.getTodoTitle(), equalTo(title));
    assertThat(todoResponce.isFinished(), equalTo(false));
    assertThat(todoResponce.getCreatedAt(), notNullValue());
  }
}
