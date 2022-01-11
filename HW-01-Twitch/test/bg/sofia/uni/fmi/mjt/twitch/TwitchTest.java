package bg.sofia.uni.fmi.mjt.twitch;

import bg.sofia.uni.fmi.mjt.twitch.content.Category;
import bg.sofia.uni.fmi.mjt.twitch.content.Metadata;
import bg.sofia.uni.fmi.mjt.twitch.content.stream.Stream;
import bg.sofia.uni.fmi.mjt.twitch.content.video.Video;
import bg.sofia.uni.fmi.mjt.twitch.user.*;
import bg.sofia.uni.fmi.mjt.twitch.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwitchTest {

    private static final String GENERAL_USERNAME = "username";
    private static final String GENERAL_TITLE = "title";
    private static final Category GENERAL_CATEGORY = Category.IRL;

    private void loadMockUsers(String... mockUsernames) {
        Map<String, User> stubMap = new HashMap<>();

        for (String mockUsername : mockUsernames) {
            // NOTE. REPLACE WITH YOUR USER IMPLEMENTATION HERE
            stubMap.put(mockUsername, new DefaultUser(mockUsername));
        }

        when(mockUserService.getUsers()).thenReturn(stubMap);
    }

    @Mock
    private UserService mockUserService;

    @InjectMocks
    private Twitch twitch;

    @Test
    void testStartStreamWithNullArgs() {
        assertThrows(IllegalArgumentException.class, () -> twitch.startStream(null, GENERAL_TITLE, GENERAL_CATEGORY));
        assertThrows(IllegalArgumentException.class, () -> twitch.startStream(GENERAL_USERNAME, null, GENERAL_CATEGORY));
        assertThrows(IllegalArgumentException.class, () -> twitch.startStream(GENERAL_USERNAME, GENERAL_TITLE, null));
    }

    @Test
    void testStartStreamWithEmptyStringArguments() {
        String emptyString = "";

        assertThrows(IllegalArgumentException.class, () -> twitch.startStream(emptyString, GENERAL_TITLE, GENERAL_CATEGORY));
        assertThrows(IllegalArgumentException.class, () -> twitch.startStream(GENERAL_USERNAME, emptyString, GENERAL_CATEGORY));
    }

    @Test
    void testStartStreamUserNotFound() {
        String unavailableUser = "unavailable";

        loadMockUsers();

        assertThrows(UserNotFoundException.class, () -> twitch.startStream(unavailableUser, GENERAL_TITLE, GENERAL_CATEGORY));

        verify(mockUserService).getUsers();
    }

    @Test
    void testStartStreamWithValidData() throws Exception {
        loadMockUsers(GENERAL_USERNAME);

        Stream streamStarted = twitch.startStream(GENERAL_USERNAME, GENERAL_TITLE, GENERAL_CATEGORY);

        User streamer = mockUserService.getUsers().get(GENERAL_USERNAME);

        assertEquals(streamer.getName(), GENERAL_USERNAME);
        assertEquals(streamStarted.getMetadata(), new Metadata(GENERAL_TITLE, GENERAL_CATEGORY, streamer));
        assertEquals(streamer.getStatus(), UserStatus.STREAMING);

        verify(mockUserService, times(2)).getUsers();
    }

    @Test
    void testStartStreamWithStreamingUser() throws Exception {
        loadMockUsers(GENERAL_USERNAME);

        twitch.startStream(GENERAL_USERNAME, GENERAL_TITLE, GENERAL_CATEGORY);

        assertThrows(UserStreamingException.class, () -> twitch.startStream(GENERAL_USERNAME, GENERAL_TITLE, GENERAL_CATEGORY));

        verify(mockUserService, times(2)).getUsers();
    }


    @Test
    void testEndStreamWithNullArgs() throws Exception {
        loadMockUsers(GENERAL_USERNAME);

        Stream stream = twitch.startStream(GENERAL_USERNAME, GENERAL_TITLE, GENERAL_CATEGORY);

        assertThrows(IllegalArgumentException.class, () -> twitch.endStream(null, stream));
        assertThrows(IllegalArgumentException.class, () -> twitch.endStream(GENERAL_USERNAME, null));
    }

    @Test
    void testEndStreamUserNotFound() throws Exception {
        loadMockUsers(GENERAL_USERNAME);

        Stream stream = twitch.startStream(GENERAL_USERNAME, GENERAL_TITLE, GENERAL_CATEGORY);

        mockUserService.getUsers().remove(GENERAL_USERNAME);

        assertThrows(UserNotFoundException.class, () -> twitch.endStream(GENERAL_USERNAME, stream));

        verify(mockUserService, times(3)).getUsers();
    }

    @Test
    void testEndStreamUserNotStreaming() throws Exception {
        loadMockUsers(GENERAL_USERNAME);

        Stream stream = twitch.startStream(GENERAL_USERNAME, GENERAL_TITLE, GENERAL_CATEGORY);
        twitch.endStream(GENERAL_USERNAME, stream);

        User user = mockUserService.getUsers().get(GENERAL_USERNAME);

        assertEquals(user.getStatus(), UserStatus.OFFLINE);
        assertThrows(UserStreamingException.class, () -> twitch.endStream(GENERAL_USERNAME, stream));

        verify(mockUserService, times(4)).getUsers();
    }

    @Test
    void testEndStreamWithValidData() throws UserNotFoundException, UserStreamingException {

        loadMockUsers(GENERAL_USERNAME);

        User streamer = mockUserService.getUsers().get(GENERAL_USERNAME);
        Stream stream = twitch.startStream(GENERAL_USERNAME, GENERAL_TITLE, GENERAL_CATEGORY);
        Video convertedVideo = twitch.endStream(GENERAL_USERNAME, stream);

        assertEquals(streamer.getStatus(), UserStatus.OFFLINE);
        assertEquals(convertedVideo.getMetadata(), stream.getMetadata());
        assertEquals(convertedVideo.getDuration(), stream.getDuration());
        assertEquals(convertedVideo.getNumberOfViews(), 0);

        verify(mockUserService, times(3)).getUsers();
    }

    @Test
    void testWatchWithNullArgs() throws Exception {
        loadMockUsers(GENERAL_USERNAME);

        Stream stream = twitch.startStream(GENERAL_USERNAME, GENERAL_TITLE, GENERAL_CATEGORY);

        assertThrows(IllegalArgumentException.class, () -> twitch.watch(null, stream));
        assertThrows(IllegalArgumentException.class, () -> twitch.watch(GENERAL_USERNAME, null));

        verify(mockUserService, times(1)).getUsers();
    }

    @Test
    void testWatchWithEmptyUsername() throws Exception {
        String empty = "";

        loadMockUsers(GENERAL_USERNAME);

        Stream stream = twitch.startStream(GENERAL_USERNAME, GENERAL_TITLE, GENERAL_CATEGORY);

        assertThrows(IllegalArgumentException.class, () -> twitch.watch(empty, stream));

        verify(mockUserService, times(1)).getUsers();
    }

    @Test
    void testWatchUserNotFound() throws Exception {
        String unavailable = "unavailable";

        loadMockUsers(GENERAL_USERNAME);

        Stream stream = twitch.startStream(GENERAL_USERNAME, GENERAL_TITLE, GENERAL_CATEGORY);

        assertThrows(UserNotFoundException.class, () -> twitch.watch(unavailable, stream));

        verify(mockUserService, times(2)).getUsers();
    }

    @Test
    void testWatchWithStreamingUser() throws Exception {
        loadMockUsers(GENERAL_USERNAME);

        Stream stream = twitch.startStream(GENERAL_USERNAME, GENERAL_TITLE, GENERAL_CATEGORY);

        assertThrows(UserStreamingException.class, () -> twitch.watch(GENERAL_USERNAME, stream));

        verify(mockUserService, times(2)).getUsers();
    }

    @Test
    void testWatchStream() throws Exception {
        String streamer = "streamer";
        String viewer = "viewer";
        loadMockUsers(streamer, viewer);

        Stream stream = twitch.startStream(streamer, GENERAL_TITLE, GENERAL_CATEGORY);

        assertEquals(stream.getNumberOfViews(), 0);

        twitch.watch(viewer, stream);

        assertEquals(stream.getNumberOfViews(), 1);

        verify(mockUserService, times(2)).getUsers();
    }

    @Test
    void testWatchVideo() throws Exception {
        String streamer = "streamer";
        String viewer = "viewer";
        loadMockUsers(streamer, viewer);

        Stream stream = twitch.startStream(streamer, GENERAL_TITLE, GENERAL_CATEGORY);
        Video video = twitch.endStream(streamer, stream);

        assertEquals(video.getNumberOfViews(), 0);

        twitch.watch(viewer, video);
        twitch.watch(viewer, video);

        assertEquals(video.getNumberOfViews(), 2);

        verify(mockUserService, times(4)).getUsers();
    }

    @Test
    void testGetMostWatchedStreamer() throws Exception {
        String streamer1 = "streamer1";
        String streamer2 = "streamer2";
        String mostWatched = "Most Watched";

        String viewer = "viewer";

        loadMockUsers(streamer1, streamer2, mostWatched, viewer);

        Stream stream1 = twitch.startStream(streamer1, GENERAL_TITLE, GENERAL_CATEGORY);
        twitch.startStream(streamer2, GENERAL_TITLE, GENERAL_CATEGORY);
        Stream mostWatchedStream = twitch.startStream(mostWatched, GENERAL_TITLE, GENERAL_CATEGORY);

        twitch.watch(viewer, stream1);

        twitch.watch(viewer, mostWatchedStream);
        twitch.watch(viewer, mostWatchedStream);

        assertEquals(twitch.getMostWatchedStreamer(), mockUserService.getUsers().get(mostWatched));
    }

    @Test
    void testGetMostWatchedStreamerWithNoContentAvailable() {
        assertNull(twitch.getMostWatchedStreamer());
    }

    @Test
    void testGetMostWatchedStreamerWithZeroViews() throws Exception {
        String streamer = "streamer";

        loadMockUsers(streamer);

        Stream stream = twitch.startStream(streamer, GENERAL_TITLE, GENERAL_CATEGORY);

        assertEquals(stream.getNumberOfViews(), 0);
        assertNull(twitch.getMostWatchedStreamer());
    }

    @Test
    void testGetMostWatchedStreamerWithEqualViews() throws Exception {
        String streamer1 = "streamer1";
        String streamer2 = "streamer2";
        String viewer = "viewer";

        loadMockUsers(streamer1, streamer2, viewer);

        Stream stream1 = twitch.startStream(streamer1, GENERAL_TITLE, GENERAL_CATEGORY);
        Stream stream2 = twitch.startStream(streamer2, GENERAL_TITLE, GENERAL_CATEGORY);

        twitch.watch(viewer, stream1);
        twitch.watch(viewer, stream2);

        assertTrue(twitch.getMostWatchedStreamer().getName().equals(streamer1) ||
                twitch.getMostWatchedStreamer().getName().equals(streamer2));
    }

    @Test
    void testGetMostWatchedStreamerWithDifferentContent() throws Exception {
        String streamer1 = "streamer1";
        String streamer2 = "streamer2";
        String viewer = "viewer";

        loadMockUsers(streamer1, streamer2, viewer);

        Stream stream1 = twitch.startStream(streamer1, GENERAL_TITLE, GENERAL_CATEGORY);
        Stream stream2 = twitch.startStream(streamer2, GENERAL_TITLE, GENERAL_CATEGORY);

        assertNull(twitch.getMostWatchedStreamer(), "No streams had been watched.");

        twitch.watch(viewer, stream1);

        assertEquals(twitch.getMostWatchedStreamer().getName(), streamer1);

        twitch.watch(viewer, stream2);
        twitch.watch(viewer, stream2);

        assertEquals(twitch.getMostWatchedStreamer().getName(), streamer2);

        Video video1 = twitch.endStream(streamer1, stream1);

        assertEquals(twitch.getMostWatchedStreamer().getName(), streamer2);

        twitch.watch(viewer, video1);
        twitch.watch(viewer, video1);
        twitch.watch(viewer, video1);

        assertEquals(twitch.getMostWatchedStreamer().getName(), streamer1);
    }

    @Test
    void testGetMostWatchedContentWithZeroViews() throws Exception {
        String streamer = "streamer";

        loadMockUsers(streamer);

        Stream stream = twitch.startStream(streamer, GENERAL_TITLE, GENERAL_CATEGORY);

        assertEquals(stream.getNumberOfViews(), 0);
        assertNull(twitch.getMostWatchedContent());
    }

    @Test
    void testGetMostWatchedContent() throws Exception {
        String streamer1 = "streamer1";
        String streamer2 = "streamer2";
        String streamer3 = "Most Watched";

        String viewer = "viewer";

        loadMockUsers(streamer1, streamer2, streamer3, viewer);

        Stream stream1 = twitch.startStream(streamer1, GENERAL_TITLE, GENERAL_CATEGORY);
        twitch.startStream(streamer2, GENERAL_TITLE, GENERAL_CATEGORY);
        Stream mostWatchedStream = twitch.startStream(streamer3, GENERAL_TITLE, GENERAL_CATEGORY);

        twitch.watch(viewer, stream1);
        twitch.watch(viewer, mostWatchedStream);
        twitch.watch(viewer, mostWatchedStream);

        assertEquals(twitch.getMostWatchedContent(), mostWatchedStream);
    }

    @Test
    void testGetMostWatchedContentWithDifferentContent() throws Exception {
        String streamer1 = "streamer1";
        String streamer2 = "streamer2";

        String viewer = "viewer";

        loadMockUsers(streamer1, streamer2, viewer);

        Stream stream1 = twitch.startStream(streamer1, GENERAL_TITLE, GENERAL_CATEGORY);
        Stream stream2 = twitch.startStream(streamer2, GENERAL_TITLE, GENERAL_CATEGORY);

        assertNull(twitch.getMostWatchedContent(), "No content is ever watched!");

        twitch.watch(viewer, stream1);
        twitch.watch(viewer, stream1);

        assertEquals(twitch.getMostWatchedContent(), stream1, "Stream1: 2, Stream2: 0");

        twitch.watch(viewer, stream2);
        assertEquals(twitch.getMostWatchedContent(), stream1, "Stream1: 2, Stream2: 1");

        Video video2 = twitch.endStream(streamer2, stream2);

        assertEquals(twitch.getMostWatchedContent(), stream1, "Stream1: 1, Stream2: REMOVED");

        twitch.watch(viewer, video2);
        twitch.watch(viewer, video2);
        twitch.watch(viewer, video2);

        assertEquals(twitch.getMostWatchedContent(), video2, "Stream1: 1, Video2: 3");
    }

    @Test
    void testGetMostWatchedContentWithEqualViews() throws Exception {
        String streamer1 = "streamer1";
        String streamer2 = "streamer2";
        String viewer = "viewer";

        loadMockUsers(streamer1, streamer2, viewer);

        Stream stream1 = twitch.startStream(streamer1, GENERAL_TITLE, GENERAL_CATEGORY);
        Stream stream2 = twitch.startStream(streamer2, GENERAL_TITLE, GENERAL_CATEGORY);

        twitch.watch(viewer, stream1);
        twitch.watch(viewer, stream2);

        assertTrue(twitch.getMostWatchedContent().equals(stream1) ||
                twitch.getMostWatchedContent().equals(stream2));
    }

    @Test
    void testGetMostWatchedContentWithMovingStreamViewers() throws Exception {
        String streamer = "streamer";
        String viewer = "viewer";

        loadMockUsers(streamer, viewer);

        Stream stream = twitch.startStream(streamer, GENERAL_TITLE, GENERAL_CATEGORY);

        assertNull(twitch.getMostWatchedContent());

        twitch.watch(viewer, stream);

        assertEquals(twitch.getMostWatchedContent().getNumberOfViews(), 1);
        assertEquals(twitch.getMostWatchedContent(), stream);

        stream.stopWatching(mockUserService.getUsers().get(viewer));

        assertEquals(stream.getNumberOfViews(), 0);
        assertNull(twitch.getMostWatchedContent());
    }

    @Test
    void testGetMostWatchedContentWithNoContentAvailable() {
        assertNull(twitch.getMostWatchedContent());
    }

    @Test
    void testGetMostWatchedContentFromWithNullUsername() {
        assertThrows(IllegalArgumentException.class, () -> twitch.getMostWatchedContentFrom(null));
    }


    @Test
    void testGetMostWatchedContentFromWithEmptyUsername() {
        String empty = "";

        assertThrows(IllegalArgumentException.class, () -> twitch.getMostWatchedContentFrom(empty));
    }

    @Test
    void testGetMostWatchedContentFromWithUnavailableUser() {
        String unavailable = "unavailable";

        loadMockUsers();

        assertThrows(UserNotFoundException.class, () -> twitch.getMostWatchedContentFrom(unavailable));
    }

    @Test
    void testGetMostWatchedContentFromWithNoContentAvailable() throws Exception {
        loadMockUsers(GENERAL_USERNAME);

        assertNull(twitch.getMostWatchedContentFrom(GENERAL_USERNAME));
    }

    @Test
    void testGetMostWatchedContentFromWithOneStream() throws Exception {
        String streamer = "streamer";
        String viewer = "viewer";

        loadMockUsers(streamer, viewer);

        Stream stream = twitch.startStream(streamer, GENERAL_TITLE, GENERAL_CATEGORY);

        assertNull(twitch.getMostWatchedContentFrom(streamer));

        twitch.watch(viewer, stream);

        assertEquals(twitch.getMostWatchedContentFrom(streamer).getNumberOfViews(), 1);
        assertEquals(twitch.getMostWatchedContentFrom(streamer), stream);
    }

    @Test
    void testGetMostWatchedContentFromWithOneStreamWithMovingViews() throws Exception {
        String streamer = "streamer";
        String viewer = "viewer";

        loadMockUsers(streamer, viewer);

        Stream stream = twitch.startStream(streamer, GENERAL_TITLE, GENERAL_CATEGORY);

        twitch.watch(viewer, stream);

        assertEquals(twitch.getMostWatchedContentFrom(streamer).getNumberOfViews(), 1);
        assertEquals(twitch.getMostWatchedContentFrom(streamer), stream);

        stream.stopWatching(mockUserService.getUsers().get(viewer));

        assertNull(twitch.getMostWatchedContentFrom(streamer));

        twitch.watch(viewer, stream);
        twitch.watch(viewer, stream);

        assertEquals(twitch.getMostWatchedContentFrom(streamer).getNumberOfViews(), 2);
        assertEquals(twitch.getMostWatchedContentFrom(streamer), stream);
    }

    @Test
    void testGetMostWatchedContentFromWithOneVideo() throws Exception {
        String streamer = "streamer";
        String viewer = "viewer";

        loadMockUsers(streamer, viewer);

        Stream stream = twitch.startStream(streamer, GENERAL_TITLE, GENERAL_CATEGORY);

        twitch.watch(viewer, stream);
        twitch.watch(viewer, stream);
        twitch.watch(viewer, stream);

        Video video = twitch.endStream(streamer, stream);

        assertNull(twitch.getMostWatchedContentFrom(streamer));

        twitch.watch(viewer, video);

        assertEquals(twitch.getMostWatchedContentFrom(streamer).getNumberOfViews(), 1);
        assertEquals(twitch.getMostWatchedContentFrom(streamer), video);
    }

    @Test
    void testGetMostWatchedContentFromWithZeroViews() throws Exception {
        String streamer = "streamer";
        String viewer = "viewer";

        loadMockUsers(streamer, viewer);

        Stream stream = twitch.startStream(streamer, GENERAL_TITLE, GENERAL_CATEGORY);

        assertNull(twitch.getMostWatchedContentFrom(streamer));

        Video video = twitch.endStream(streamer, stream);

        assertNull(twitch.getMostWatchedContentFrom(streamer));
    }


    @Test
    void testGetMostWatchedContentFrom() throws Exception {
        String streamer = "streamer";
        String viewer = "viewer";

        loadMockUsers(streamer, viewer);

        User userViewer = mockUserService.getUsers().get(viewer);

        Stream s1 = twitch.startStream(streamer, GENERAL_TITLE, GENERAL_CATEGORY);
        Video v1 = twitch.endStream(streamer, s1); // Available

        Stream s2 = twitch.startStream(streamer, GENERAL_TITLE, GENERAL_CATEGORY);
        Video v2 = twitch.endStream(streamer, s2); // Available

        Stream stream = twitch.startStream(streamer, GENERAL_TITLE, GENERAL_CATEGORY);  // Available

        assertNull(twitch.getMostWatchedContentFrom(streamer));
        twitch.watch(viewer, s1); // Must not be available in the content repository
        assertNull(twitch.getMostWatchedContentFrom(streamer));

        twitch.watch(viewer, stream);
        assertEquals(twitch.getMostWatchedContentFrom(streamer), stream);

        twitch.watch(viewer, v1);
        twitch.watch(viewer, v1);
        assertEquals(twitch.getMostWatchedContentFrom(streamer), v1);
        v1.stopWatching(userViewer); // Must not change vide views
        assertEquals(twitch.getMostWatchedContentFrom(streamer), v1);

        twitch.watch(viewer, v2);
        twitch.watch(viewer, v2);
        twitch.watch(viewer, v2);
        assertEquals(twitch.getMostWatchedContentFrom(streamer), v2);
    }

}
