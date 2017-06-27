/*
 * Copyright 2017 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti;

import java.util.Collections;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.activiti.client.model.resources.ProcessDefinitionResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {

    private RestTemplate restTemplate;

    @LocalServerPort
    private int serverPort;

    @Before
    public void setUp() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                         false);
        mapper.registerModule(new Jackson2HalModule());

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
        converter.setObjectMapper(mapper);

        restTemplate = new RestTemplate(Collections.singletonList(converter));
    }

    @Test
    public void should_retrieve_list_of_processDefinition() throws Exception {
        //given
        ParameterizedTypeReference<PagedResources<ProcessDefinitionResource>> responseType = new ParameterizedTypeReference<PagedResources<ProcessDefinitionResource>>() {
        };

        //when
        ResponseEntity<PagedResources<ProcessDefinitionResource>> entity = restTemplate.exchange("http://localhost:" + serverPort + "/api/repository/process-definitions",
                                                                                                  HttpMethod.GET,
                                                                                                  null,
                                                                                                  responseType);


        //then
        assertThat(entity).isNotNull();
    }

    @Test
    public void should_return_process_denition_by_id() throws Exception {
        //given
        ParameterizedTypeReference<PagedResources<ProcessDefinitionResource>> responseType = new ParameterizedTypeReference<PagedResources<ProcessDefinitionResource>>() {
        };

        //when
        ResponseEntity<PagedResources<ProcessDefinitionResource>> entity = restTemplate.exchange("/api/repository/process-definitions/hireProcess:1:4",
                                                                                                 HttpMethod.GET,
                                                                                                 null,
                                                                                                 responseType);

        //then
        assertThat(entity).isNotNull();
    }
}