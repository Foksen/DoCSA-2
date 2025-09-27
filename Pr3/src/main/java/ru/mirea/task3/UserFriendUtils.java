package ru.mirea.task3;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UserFriendUtils {

    public static final Random random = new Random();

    public static UserFriend randomUserFriend(int maxUserId, int maxFriendId) {
        return new UserFriend(random.nextInt(maxUserId) + 1, random.nextInt(maxFriendId) + 1);
    }

    public static List<UserFriend> randomUserFriends(int count, int maxUserId, int maxFriendId) {
        if (count > maxUserId * (maxFriendId - 1)) {
            throw new IllegalArgumentException("Not enough users to create enough user friends");
        }
        List<UserFriend> allPairs = IntStream.rangeClosed(1, maxUserId)
                .boxed()
                .flatMap(userId -> IntStream.rangeClosed(1, maxFriendId)
                        .filter(friendId -> friendId != userId)
                        .mapToObj(friendId -> new UserFriend(userId, friendId)))
                .collect(Collectors.toList());
        Collections.shuffle(allPairs);
        return allPairs.subList(0, count);
    }

    public static List<Integer> collectUserIds(List<UserFriend> userFriends) {
        return userFriends.stream().map(UserFriend::userId).distinct().toList();
    }
}
