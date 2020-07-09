// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;
import com.google.sps.data.Post;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/post")
public class PostDataServlet extends HttpServlet {

  /** Responsible for creating new post. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Get the input from the form.
    String firstName = getParameter(request, "firstName", "");
    String lastName = getParameter(request, "lastName", "");
    String email = getParameter(request, "email", "");
    String message = getParameter(request, "message", "");

    // Create an Entity type Post.
    Entity postEntity = new Entity("Post");
    postEntity.setProperty("firstName", firstName);
    postEntity.setProperty("lastName", lastName);
    postEntity.setProperty("email", email);
    postEntity.setProperty("message", message);

    // Store the Post in Datastore.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(postEntity);

    response.sendRedirect("/index.html");
  }

  /**
   * @return the request parameter, or the default value if the parameter was not specified by the
   *     client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  /** Responsible for listing posts. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Create Post Query instance.
    Query query = new Query("Post");

    // Find all Post entities in Datastore.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Add all Post entities into an array.
    List<Post> posts = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      String firstName = (String) entity.getProperty("firstName");
      String lastName = (String) entity.getProperty("lastName");
      String email = (String) entity.getProperty("email");
      String message = (String) entity.getProperty("message");

      Post post = new Post(firstName, lastName, email, message);
      posts.add(post);
    }

    // Return all Post entities with a JSON format.
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(posts));
  }
}
