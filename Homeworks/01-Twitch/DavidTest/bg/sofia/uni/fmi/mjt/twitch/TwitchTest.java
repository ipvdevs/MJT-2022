package bg.sofia.uni.fmi.mjt.twitch;

import bg.sofia.uni.fmi.mjt.twitch.content.Category;
import bg.sofia.uni.fmi.mjt.twitch.content.Metadata;
import bg.sofia.uni.fmi.mjt.twitch.content.stream.Stream;
import bg.sofia.uni.fmi.mjt.twitch.content.video.Video;
import bg.sofia.uni.fmi.mjt.twitch.user.DefaultUser;
import bg.sofia.uni.fmi.mjt.twitch.user.User;
import bg.sofia.uni.fmi.mjt.twitch.user.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.twitch.user.UserStreamingException;
import bg.sofia.uni.fmi.mjt.twitch.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TwitchTest2 {

    private void mockUsers(String... names) {
        Map<String, User> users = new HashMap<>();

        for (String name : names) {
            users.put(name, new DefaultUser(name));
        }

        when(userServiceMock.getUsers()).thenReturn(users);
    }

    @Mock
    private UserService userServiceMock;

    @InjectMocks
    private Twitch twitch;

    @Test
    void testStartStreamValidation() {
        assertThrows(IllegalArgumentException.class, () -> twitch.startStream(null, "title", Category.ESPORTS),
            "Null user name must be invalid!");
        assertThrows(IllegalArgumentException.class, () -> twitch.startStream("", "title", Category.ESPORTS),
            "Empty user name must be invalid!");
        assertThrows(IllegalArgumentException.class, () -> twitch.startStream("u1", null, Category.ESPORTS),
            "Null title must be invalid!");
        assertThrows(IllegalArgumentException.class, () -> twitch.startStream("u1", "", Category.ESPORTS),
            "Empty title must be invalid!");
        assertThrows(IllegalArgumentException.class, () -> twitch.startStream("u1", "title", null),
            "Null category must be invalid!");

        mockUsers("u1", "u2", "u3");
        assertThrows(UserNotFoundException.class, () -> twitch.startStream("missing user", "title", Category.IRL),
            "Starting stream with missing user must throw an exception!");
        assertDoesNotThrow(() -> twitch.startStream("u1", "title", Category.ESPORTS),
            "User should be able to start streaming!");
        assertThrows(UserStreamingException.class, () -> twitch.startStream("u1", "title", Category.ESPORTS),
            "User is already streaming!");
    }

    @Test
    void testStartFirstStream() throws Exception {
        mockUsers("u1", "u2");
        Stream stream = twitch.startStream("u1", "My first stream!", Category.IRL);
        assertEquals(stream.getMetadata(), new Metadata("My first stream!", Category.IRL, new DefaultUser("u1")),
            "Existing user must be able to start streaming!");
    }

    @Test
    void testEndStreamValidation() throws Exception {
        mockUsers("u1", "u2");
        assertThrows(IllegalArgumentException.class, () -> twitch.endStream("u1", null),
            "Null stream must be invalid!");

        Stream streamMock = mock(Stream.class);
        assertThrows(UserStreamingException.class, () -> twitch.endStream("u1", streamMock),
            "User is not streaming, exception expected!");

        Stream actualStream = twitch.startStream("u1", "title", Category.ESPORTS);
        twitch.startStream("u2", "other stream", Category.ESPORTS);
        assertThrows(UserStreamingException.class, () -> twitch.endStream("u2", actualStream),
            "Stream does not belong to that user!");
    }

    @Test
    void testEndStreamValid() throws Exception {
        mockUsers("u1", "u2");
        Stream stream = twitch.startStream("u1", "title", Category.IRL);
        Video video = twitch.endStream("u1", stream);
        assertNotNull(video.getDuration(), "Running streams must be able to get ended and return a video instance!");
    }

    @Test
    void testWatchValidation() throws Exception {
        mockUsers("u1", "u2");

        assertThrows(IllegalArgumentException.class, () -> twitch.watch("u1", null), "Null content is invalid!");

        Stream stream = twitch.startStream("u1", "title", Category.IRL);
        assertThrows(UserStreamingException.class, () -> twitch.watch("u1", stream),
            "Can't watch other content while streaming, exception expected!");

        assertDoesNotThrow(() -> twitch.watch("u2", stream), "Running streams must be able to get watched!");
    }

    @Test
    void testGetMostWatchedContentFromUserNoContent() throws Exception {
        mockUsers("u1");
        assertNull(twitch.getMostWatchedContentFrom("u1"), "User has no content, so null was expected!");
    }

    @Test
    void testGetMostWatchedContentFrom() throws Exception {
        mockUsers("u1", "u2", "u3", "u4");

        Stream stream = twitch.startStream("u1", "First stream", Category.IRL);
        twitch.watch("u2", stream);
        twitch.watch("u3", stream);
        assertEquals(twitch.getMostWatchedContentFrom("u1"), stream,
            "A single stream with 2 views must be the most watched content in the platform!");

        Video video = twitch.endStream("u1", stream);
        stream = twitch.startStream("u1", "Second stream", Category.ESPORTS);
        twitch.watch("u2", stream);
        twitch.watch("u3", video);
        assertTrue(List.of(stream, video).contains(twitch.getMostWatchedContentFrom("u1")),
            "When a stream and a video have equal number of watches, either one of them must be returned!");
        twitch.watch("u2", video);
        assertEquals(twitch.getMostWatchedContentFrom("u1"), video,
            "Wrong most watched content, must have been a video!");
    }

    @Test
    void testGetMostWatchedStreamer() throws Exception {
        assertNull(twitch.getMostWatchedStreamer(),
            "Expected null most watched streamer when nobody has ever streamed!");

        mockUsers("u1", "u2", "u3");
        Stream stream = twitch.startStream("u1", "First stream ever!", Category.GAMES);
        twitch.watch("u2", stream);
        assertEquals(twitch.getMostWatchedStreamer().getName(), "u1",
            "One stream with a single view, most watched streamer not null expected!");

        twitch.endStream("u1", stream);
        assertNull(twitch.getMostWatchedStreamer(),
            "Stream ended, no views on video, most watched streamer must be null!");
    }

    @Test
    void testGetMostWatchedContent() throws Exception {
        mockUsers("u1", "u2", "u3", "u4");

        Stream stream1 = twitch.startStream("u1", "title", Category.IRL);
        twitch.watch("u2", stream1);

        assertEquals(twitch.getMostWatchedContent(), stream1,
            "A single stream with a single view must be the most watched content!");

        Video video1 = twitch.endStream("u1", stream1);

        assertNull(twitch.getMostWatchedContent(),
            "No streams and a video with no views is a null most watched content situation!");

        twitch.watch("u1", video1);
        twitch.watch("u2", video1);
        //an experimental double assertion :D
        assertTrue(twitch.getMostWatchedContent().equals(video1) && video1.getNumberOfViews() == 2,
            "A video with 2 views must be the most watched content!");

        Stream stream2 = twitch.startStream("u2", "title", Category.ESPORTS);

        twitch.watch("u1", stream2);
        twitch.watch("u3", stream2);
        twitch.watch("u4", stream2);
        assertEquals(twitch.getMostWatchedContent(), stream2,
            "Wrong most watched content, a stream with 3 views expected!");

        twitch.endStream("u2", stream2);
        assertEquals(twitch.getMostWatchedContent(), video1,
            "Wrong most watched content, a video with 1 view expected!");
    }

    @Test
    void testGetMostWatchedCategoriesBy() throws Exception {
        mockUsers("u1", "u2", "u3", "u4", "u5", "u6", "u7");

        assertEquals(0, twitch.getMostWatchedCategoriesBy("u1").size());

        List<Stream> streams = List.of(
            twitch.startStream("u2", "title", Category.GAMES),
            twitch.startStream("u3", "title", Category.GAMES),
            twitch.startStream("u4", "title", Category.IRL),
            twitch.startStream("u5", "title", Category.ESPORTS),
            twitch.startStream("u6", "title", Category.ESPORTS),
            twitch.startStream("u7", "title", Category.ESPORTS)
        );

        for (Stream stream : streams) {
            twitch.watch("u1", stream);
        }

        //assert that lists are equal WITH the same order
        assertEquals(List.of(Category.ESPORTS, Category.GAMES, Category.IRL), twitch.getMostWatchedCategoriesBy("u1"),
            "Wrong ordered list of most watched categories!");
        //assert that the order does indeed match!
        assertNotEquals(List.of(Category.GAMES, Category.IRL, Category.ESPORTS),
            twitch.getMostWatchedCategoriesBy("u1"), "Wrong ordered list of most watched categories!");
    }
}