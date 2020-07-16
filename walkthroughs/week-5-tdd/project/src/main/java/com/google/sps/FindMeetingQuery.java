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

/**
 * Finds time ranges when meeting could happen in a day assuming given events will be sorted from
 * beginning of the day till the end.
 */
public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

    // Gather information from given meeting request.
    Collection<String> attendeesRequired = request.getAttendees();
    long duration = request.getDuration();

    // Initialize return object holding all possible time ranges.
    ArrayList<TimeRange> possibleMeetingTimes = new ArrayList<TimeRange>();

    // Initialize external trackers of calendar date.
    TimeRange prevTimeRange = null;
    int currentTime = TimeRange.START_OF_DAY;
    int endTime = TimeRange.END_OF_DAY;

    // Iterate through each event to determine availibity.
    for (Event event : events) {

      // Check if event has relevant attendees and move to next one if doesn't
      if (Collections.disjoint(event.getAttendees(), attendeesRequired)) {
        continue;
      }

      TimeRange when = event.getWhen();

      // Establish time change for overlapping possibilities.
      if (possibleMeetingTimes.size() >= 1 && when.overlaps(prevTimeRange)) {
        if (currentTime < when.end()) {
          currentTime = when.end();
        }
        continue;
      }

      // Add new time range based on current event and if long enough duration.
      TimeRange possible = TimeRange.fromStartEnd(currentTime, when.start(), false);
      if (durationChecker(possible, duration)) {
        possibleMeetingTimes.add(possible);
      }

      // Update external trackers to most recent event.
      currentTime = when.end();
      prevTimeRange = when;
    }

    // Check if the rest of day needs to be included.
    TimeRange restOfDay = TimeRange.fromStartEnd(currentTime, TimeRange.END_OF_DAY, true);
    if (currentTime < TimeRange.END_OF_DAY && durationChecker(restOfDay, duration)) {
      possibleMeetingTimes.add(restOfDay);
    }

    return possibleMeetingTimes;
  }

  /** Determine if given time range is long enough for a meeting. */
  public boolean durationChecker(TimeRange time, long duration) {
    if (time.duration() >= duration) {
      return true;
    }
    return false;
  }
}
