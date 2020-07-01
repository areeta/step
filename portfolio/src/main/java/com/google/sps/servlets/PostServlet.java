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

import com.google.sps.data.Post;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that creates a new Post*/
@WebServlet("/posts")
public class PostServlet extends HttpServlet {
  
  private ArrayList<Post> posts = new ArrayList<Post>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Convert the posts to JSON.
    String json = convertToJsonUsingGson(posts);

    // Send the JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Get the input from the form.
    String firstName = getParameter(request, "firstName", "");
    String lastName = getParameter(request, "lastName", "");
    String email = getParameter(request, "email", "");
    String message = getParameter(request, "message", "");

    // Create Post object.
    Post post = new Post(firstName, lastName, email, message);
    posts.add(post);

    // Redirect back to the HTML page.
    response.sendRedirect("/index.html");
  }

  /**
   * Converts a Post instance into a JSON string using the Gson library. Note: We first added
   * the Gson library dependency to pom.xml.
  */
  private String convertToJsonUsingGson(ArrayList<Post> arrayList) {
    Gson gson = new Gson();
    String json = gson.toJson(arrayList);
    return json;
  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
  */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
