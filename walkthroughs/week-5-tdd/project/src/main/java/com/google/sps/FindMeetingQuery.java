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

public final class FindMeetingQuery {

  private Collection<String> requiredAttendees;
  private Collection<String> optionalAttendees;
  private long duration;
  public static final int START = TimeRange.START_OF_DAY;
  public static final int END = TimeRange.START_OF_DAY;

  /** Finds times when meeting could happen in a day. */
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

    // Gather information from given meeting request.
    requiredAttendees = request.getAttendees();
    optionalAttendees = request.getOptionalAttendees();
    duration = request.getDuration();

    // Initialize return object holding all free time ranges for attendees.
    ArrayList<TimeRange> possibleMeetingTimes = new ArrayList<TimeRange>();
    Collection<Event> optionalAttendeeEvents = getOptionalAttendeeEvents(events, optionalAttendees);

    // No required attendees case.
    if (requiredAttendees.size()==0) {

      // Check for optional attendees and count them in if in range.
      if (optionalAttendees.size()>0) {
        possibleMeetingTimes.addAll(getRanges(optionalAttendees, optionalAttendeeEvents, duration));
      } else {
        possibleMeetingTimes.add(TimeRange.WHOLE_DAY);
      }
      return possibleMeetingTimes;

      // Longer than a day case.
    } else if (duration>1440) {
      return possibleMeetingTimes;
    }

    possibleMeetingTimes = getRanges(requiredAttendees, events, duration);

    // Add any optional attendee that is free by removing their event time from overall meeting
    // ranges.
    if (optionalAttendeeEvents.size()>0) {
      ArrayList<TimeRange> optionalMeetingTimes = new ArrayList<TimeRange>();
      for (Event event : optionalAttendeeEvents) {
        optionalMeetingTimes.add(event.getWhen());
      }
      possibleMeetingTimes.removeAll(optionalMeetingTimes);
    }
    return possibleMeetingTimes;
  }

  public ArrayList<TimeRange> getRanges(
      Collection<String> requiredAttendees, Collection<Event> events, long duration) {

    ArrayList<TimeRange> possibleMeetingTimes = new ArrayList<TimeRange>();

    // Initialize external trackers of calendar date.
    TimeRange prevTimeRange = null;
    int currentTime = START;
    int endTime = END;

    // Iterate through each event to determine availibity.
    for (Event event : events) {

      // Check if event has relevant attendees and move to next one if doesn't
      if (Collections.disjoint(event.getAttendees(), requiredAttendees)) {
        continue;
      }

      TimeRange when = event.getWhen();

      // Establish time change for overlapping possibilities.
      if (possibleMeetingTimes.size()>=1 && when.overlaps(prevTimeRange)) {
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

  /**
   * Returns events with optional attendees.
   *
   * @param events The events of everyone involved. Must be non-null.
   * @param optionalAttendees The optional attendees from the meeting request. Must be non-null.
   */
  public Collection<Event> getOptionalAttendeeEvents(
      Collection<Event> events, Collection<String> optionalAttendees) {

    Collection<Event> optionalAttendeeEvents = new ArrayList<Event>();

    // Iterate through each event to determine which events with optional attendees.
    for (Event event : events) {
      if (Collections.disjoint(event.getAttendees(), optionalAttendees) == false) {
        optionalAttendeeEvents.add(event);
      }
    }

    return optionalAttendeeEvents;
  }

  /**
   * Returns true if given time range is long enough for a meeting.
   *
   * @param range The time range when the event takes place. Must be non-null.
   * @param duration The amount of time required for meeting request. Must be non-null.
   */
  public boolean durationChecker(TimeRange range, long duration) {
    if (range.duration()>=duration) {
      return true;
    }
    return false;
  }
}
