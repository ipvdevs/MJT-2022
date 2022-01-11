package bg.sofia.uni.fmi.mjt.twitch.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testDefaultUserInitialStatus() {
        User defaultUser = new DefaultUser("TimTester");
        assertEquals(defaultUser.getStatus(), UserStatus.OFFLINE);
    }

}