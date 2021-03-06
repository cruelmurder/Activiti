/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.rest.service.api.runtime.task;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.rest.service.api.engine.CommentRequest;
import org.activiti.rest.service.api.engine.CommentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**

 */
@RestController
@Api(tags = { "Tasks" }, description = "Manage Tasks", authorizations = { @Authorization(value = "basicAuth") })
public class TaskCommentCollectionResource extends TaskBaseResource {


  @ApiOperation(value = "Get all comments on a task", tags = {"Tasks"}, nickname = "listTaskComments")
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "Indicates the task was found and the comments are returned."),
      @ApiResponse(code = 404, message = "Indicates the requested task was not found.")
  })	
  @RequestMapping(value = "/runtime/tasks/{taskId}/comments", method = RequestMethod.GET, produces = "application/json")
  public List<CommentResponse> getComments(@ApiParam(name = "taskId", value="The id of the task to get the comments for.") @PathVariable String taskId, HttpServletRequest request) {
    HistoricTaskInstance task = getHistoricTaskFromRequest(taskId);
    return restResponseFactory.createRestCommentList(taskService.getTaskComments(task.getId()));
  }

  @ApiOperation(value = "Create a new comment on a task", tags = {"Tasks"},  nickname = "createTaskComments")
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "Indicates the comment was created and the result is returned."),
      @ApiResponse(code = 400, message = "Indicates the comment is missing from the request."),
      @ApiResponse(code = 404, message = "Indicates the requested task was not found.")
  })
  @RequestMapping(value = "/runtime/tasks/{taskId}/comments", method = RequestMethod.POST, produces = "application/json")
  public CommentResponse createComment(@ApiParam(name = "taskId", value="The id of the task to create the comment for.") @PathVariable String taskId, @RequestBody CommentRequest comment, HttpServletRequest request, HttpServletResponse response) {

    Task task = getTaskFromRequest(taskId);

    if (comment.getMessage() == null) {
      throw new ActivitiIllegalArgumentException("Comment text is required.");
    }

    String processInstanceId = null;
    if (comment.isSaveProcessInstanceId()) {
      Task taskEntity = taskService.createTaskQuery().taskId(task.getId()).singleResult();
      processInstanceId = taskEntity.getProcessInstanceId();
    }
    Comment createdComment = taskService.addComment(task.getId(), processInstanceId, comment.getMessage());
    response.setStatus(HttpStatus.CREATED.value());

    return restResponseFactory.createRestComment(createdComment);
  }
}
