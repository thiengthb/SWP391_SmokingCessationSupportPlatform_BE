package com.swpteam.smokingcessation.feature.integration.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.swpteam.smokingcessation.utils.GoogleAuthorizeUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import java.util.Arrays;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class GoogleMeetService {

    public String createGoogleMeet(String title, String email) throws Exception {
        Credential credential = GoogleAuthorizeUtil.getCredentials();

        Calendar service = new Calendar.Builder(
                credential.getTransport(),
                credential.getJsonFactory(),
                credential
        ).setApplicationName("CoachMeetApp").build();

        long now = System.currentTimeMillis();

        Event event = new Event()
                .setSummary(title)
                .setDescription("Auto generated meeting")
                .setStart(new EventDateTime()
                        .setDateTime(new com.google.api.client.util.DateTime(now + 3600000))
                        .setTimeZone("Asia/Ho_Chi_Minh"))
                .setEnd(new EventDateTime()
                        .setDateTime(new com.google.api.client.util.DateTime(now + 5400000))
                        .setTimeZone("Asia/Ho_Chi_Minh"))
                .setAttendees(Arrays.asList(new EventAttendee().setEmail("chinhchinhnguq@gmail.com")));

        // Add Meet link
        ConferenceData conferenceData = new ConferenceData();
        conferenceData.setCreateRequest(new CreateConferenceRequest().setRequestId("meet-" + now));
        event.setConferenceData(conferenceData);

        Event createdEvent = service.events().insert("primary", event)
                .setConferenceDataVersion(1)
                .execute();

        return createdEvent.getHangoutLink(); // ✅ Trả về Google Meet link
    }
}
