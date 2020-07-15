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
import java.util.Collections;
import java.util.Set;

/** Finds times when meeting could happen in a day. */
public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    
    // Gather information from given meeting request.
    Collection<String> attendeesRequired = request.getAttendees();
    long duration = request.getDuration();

    Collection<TimeRange> possibleMeetingTimes = new ArrayList<TimeRange>();

    // No attendees case.
    if (attendeesRequired.size() == 0) {
      possibleMeetingTimes.add(TimeRange.WHOLE_DAY);
      return possibleMeetingTimes;

    // Longer than a day case.
    } else if (duration > 1440) {
      return possibleMeetingTimes;
   
    // Only scheduling one person
    } else if (attendeesRequired.size() == 1) {

      int currentTime = TimeRange.START_OF_DAY;

      // Assume events will be sorted from beginning of the day till end.
      for (Event event: events) {
        TimeRange when = event.getWhen();
        possibleMeetingTimes.add(TimeRange.fromStartEnd(currentTime, when.start(), false));
        currentTime = when.end();
      }

      if (currentTime != TimeRange.END_OF_DAY) {
        possibleMeetingTimes.add(TimeRange.fromStartEnd(currentTime, TimeRange.END_OF_DAY, true));
      }
    }

    return possibleMeetingTimes;
  }
}
