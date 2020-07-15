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

package com.google.sps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/** Finds time ranges when meeting could happen in a day. */
public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    
    // Gather information from given meeting request.
    Collection<String> attendeesRequired = request.getAttendees();
    long duration = request.getDuration();

    // Initialize 
    ArrayList<TimeRange> possibleMeetingTimes = new ArrayList<TimeRange>();
    int currentTime = TimeRange.START_OF_DAY;

    // No attendees case and longer than a day.
    if (attendeesRequired.size() == 0) {
      possibleMeetingTimes.add(TimeRange.WHOLE_DAY);
      return possibleMeetingTimes;
    } else if (duration > 1440) {
      return possibleMeetingTimes;
    }

    // Assume events will be sorted from beginning of the day till end.
    TimeRange prevTimeRange = null;

    for (Event event : events) {
      TimeRange when = event.getWhen();

      if (possibleMeetingTimes.size() >= 1) {
        if (currentTime < when.end()) {

          if (when.overlaps(prevTimeRange)) {
            currentTime = when.end();
            break;
          }
        } else {
          break;
        }
      }
      possibleMeetingTimes.add(TimeRange.fromStartEnd(currentTime, when.start(), false));
      currentTime = when.end();
      prevTimeRange = when;
    }

    if (currentTime != TimeRange.END_OF_DAY) {
      possibleMeetingTimes.add(TimeRange.fromStartEnd(currentTime, TimeRange.END_OF_DAY, true));
    }
    return possibleMeetingTimes;
  }
}
