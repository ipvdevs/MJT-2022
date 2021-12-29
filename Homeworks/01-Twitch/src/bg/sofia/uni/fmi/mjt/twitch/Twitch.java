package bg.sofia.uni.fmi.mjt.twitch;

import bg.sofia.uni.fmi.mjt.twitch.content.Category;
import bg.sofia.uni.fmi.mjt.twitch.content.Content;
import bg.sofia.uni.fmi.mjt.twitch.content.Metadata;
import bg.sofia.uni.fmi.mjt.twitch.content.stream.Stream;
import bg.sofia.uni.fmi.mjt.twitch.validations.CommonValidations;
import bg.sofia.uni.fmi.mjt.twitch.content.video.Video;
import bg.sofia.uni.fmi.mjt.twitch.user.User;
import bg.sofia.uni.fmi.mjt.twitch.user.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.twitch.user.UserStatus;
import bg.sofia.uni.fmi.mjt.twitch.user.UserStreamingException;
import bg.sofia.uni.fmi.mjt.twitch.user.service.UserService;

import java.util.*;

public class Twitch implements StreamingPlatform {
    private final UserService userService;
    private final Map<User, Set<Content>> platformContent;
    private final Map<User, Map<Category, Integer>> categoryStats;

    public Twitch(UserService userService) {
        this.userService = userService;
        this.categoryStats = new HashMap<>();
        this.platformContent = new HashMap<>();
    }

    @Override
    public Stream startStream(String username,
                              String title,
                              Category category) throws UserNotFoundException, UserStreamingException {
        CommonValidations.throwIfNullOrEmpty(username, "username");
        CommonValidations.throwIfNullOrEmpty(title, "title");
        CommonValidations.throwIfNull(category, "category");

        User user = findUser(username);

        if (user.getStatus() == UserStatus.STREAMING) {
            throw new UserStreamingException("User " + username + " is already streaming!");
        }

        user.setStatus(UserStatus.STREAMING);

        Metadata metadata = new Metadata(title, category, user);
        Stream stream = new Stream(metadata);

        platformContent.putIfAbsent(user, new HashSet<>());
        platformContent.get(user).add(stream);

        return stream;
    }

    @Override
    public Video endStream(String username, Stream stream) throws UserNotFoundException, UserStreamingException {
        CommonValidations.throwIfNullOrEmpty(username, "username");
        CommonValidations.throwIfNull(stream, "stream");

        User user = findUser(username);

        if (user.getStatus() != UserStatus.STREAMING) {
            throw new UserStreamingException("User " + username + " is not currently streaming!");
        }

        if (!stream.getMetadata().user().equals(user)) {
            throw new UserStreamingException(username + " and stream username mismatch!");
        }

        user.setStatus(UserStatus.OFFLINE);
        stream.end();
        Video videoOfStream = new Video(stream.getMetadata(), stream.getDuration());

        Set<Content> userContent = platformContent.get(user);

        userContent.remove(stream);
        userContent.add(videoOfStream);

        return videoOfStream;
    }

    @Override
    public void watch(String username, Content content) throws UserNotFoundException, UserStreamingException {
        CommonValidations.throwIfNullOrEmpty(username, "username");
        CommonValidations.throwIfNull(content, "content");

        User user = findUser(username);

        if (user.getStatus() == UserStatus.STREAMING) {
            throw new UserStreamingException("User " + username + " is currently streaming!");
        }

        categoryStats.putIfAbsent(user, new HashMap<>());
        Map<Category, Integer> viewsByCategory = categoryStats.get(user);

        Category category = content.getMetadata().category();
        viewsByCategory.putIfAbsent(category, 0);

        content.startWatching(user);

        viewsByCategory.put(category, viewsByCategory.get(category) + 1);
    }

    @Override
    public User getMostWatchedStreamer() {
        User mostWatchedStreamer = null;
        int mostWatchedViews = 0;

        for (Map.Entry<User, Set<Content>> entry : platformContent.entrySet()) {
            User currentUser = entry.getKey();
            int currentTotalViews = calculateTotalViews(entry.getValue());

            if (currentTotalViews > mostWatchedViews) {
                mostWatchedStreamer = currentUser;
                mostWatchedViews = currentTotalViews;
            }
        }

        return mostWatchedStreamer;
    }

    @Override
    public Content getMostWatchedContent() {
        Content mostWatchedContent = null;
        int maxViews = 0;

        for (Set<Content> repository : platformContent.values()) {
            Content userMostWatched = getMostWatchedInRepository(repository);

            if (userMostWatched != null) {
                int views = userMostWatched.getNumberOfViews();

                if (views > maxViews) {
                    mostWatchedContent = userMostWatched;
                    maxViews = views;
                }
            }
        }

        return mostWatchedContent;
    }


    @Override
    public Content getMostWatchedContentFrom(String username) throws UserNotFoundException {
        CommonValidations.throwIfNullOrEmpty(username, "username");

        User user = findUser(username);

        return getMostWatchedInRepository(platformContent.get(user));
    }

    @Override
    public List<Category> getMostWatchedCategoriesBy(String username) throws UserNotFoundException {
        CommonValidations.throwIfNullOrEmpty(username, "username");

        User user = findUser(username);

        List<Category> result = new ArrayList<>();
        Map<Category, Integer> viewsByCategory = categoryStats.get(user);

        if (viewsByCategory != null) {
            List<Map.Entry<Category, Integer>> entryList = new ArrayList<>(viewsByCategory.entrySet());

            Comparator<Map.Entry<Category, Integer>> comparator = Map.Entry.comparingByValue();
            entryList.sort(comparator.reversed()); // In order to provide descending relation

            for (Map.Entry<Category, Integer> entry : entryList) {
                if (entry.getValue() > 0) {
                    result.add(entry.getKey());
                }
            }
        }

        return List.copyOf(result);
    }

    private Content getMostWatchedInRepository(Set<Content> userRepository) {
        Content mostWatched = null;
        int maxViews = 0;

        if (userRepository != null) {
            for (Content content : userRepository) {
                int numberOfViews = content.getNumberOfViews();

                if (numberOfViews > maxViews) {
                    mostWatched = content;
                    maxViews = numberOfViews;
                }
            }
        }

        return mostWatched;
    }

    private int calculateTotalViews(Set<Content> userContentRepo) {
        CommonValidations.throwIfNull(userContentRepo, "userContentRepo");

        int totalContentViews = 0;

        for (Content content : userContentRepo) {
            totalContentViews += content.getNumberOfViews();
        }

        return totalContentViews;
    }

    private User findUser(String username) throws UserNotFoundException {
        CommonValidations.throwIfNull(userService, "userService");

        User user = userService.getUsers().get(username);

        if (user == null) {
            throw new UserNotFoundException("User " + username + " not found!");
        }

        return user;
    }
}
