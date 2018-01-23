package io.pivotal.pal.tracker;

import java.util.*;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    Map<Long, TimeEntry> timeEntryMap = new HashMap<>();
    private long defaultId = 1;
    @Override
    public List<TimeEntry> list() {
        List<TimeEntry> list = new ArrayList<>(timeEntryMap.values());

        return list;
    }

    @Override
    public TimeEntry find(long id) {
        return timeEntryMap.get(id);
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        TimeEntry toUpdateTimeEntry = timeEntryMap.get(id);
        toUpdateTimeEntry.setProjectId(timeEntry.getProjectId());
        toUpdateTimeEntry.setDate(timeEntry.getDate());
        toUpdateTimeEntry.setHours(timeEntry.getHours());
        toUpdateTimeEntry.setUserId(timeEntry.getUserId());

        timeEntryMap.put(id, toUpdateTimeEntry);
        return toUpdateTimeEntry;
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        if(timeEntry.getId() == 0){
            timeEntry.setId(defaultId);
            timeEntryMap.put(defaultId++, timeEntry);
        }else {
            timeEntryMap.put(timeEntry.getId(), timeEntry);
        }
        return timeEntry;
    }

    @Override
    public void delete(long id) {
        timeEntryMap.remove(id);
    }


}
