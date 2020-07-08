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

import com.google.sps.data.Marker;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Returns Marker data as a JSON array, e.g. [{"lat": 38.4404675, "lng": -122.7144313, "content": "tokyo, japan"}] */
@WebServlet("/marker-data")
public class MarkerDataServlet extends HttpServlet {

  private Collection<Marker> markers;

  @Override
  public void init() {
    markers = new ArrayList<>();

    /** Iterate through entire CSV file to get every location. */
    Scanner scanner = new Scanner(getServletContext().getResourceAsStream("/WEB-INF/marker-data.csv"));
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] row = line.split(",");

      double lat = Double.parseDouble(row[0]);
      double lng = Double.parseDouble(row[1]);
      String content = row[2];

      markers.add(new Marker(lat, lng, content));
    }
    scanner.close();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();
    String json = gson.toJson(markers);
    response.getWriter().println(json);
  }
}
