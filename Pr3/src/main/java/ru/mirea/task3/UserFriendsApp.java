package ru.mirea.task3;

import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@SuppressWarnings("ResultOfMethodCallIgnored")
@Slf4j
public class UserFriendsApp {

    private static final int USER_FRIEND_COUNT = 20;
    private static final int USER_COUNT = 20;

    public static void main(String[] args) {
        List<UserFriend> userFriends = UserFriendUtils.randomUserFriends(USER_FRIEND_COUNT, USER_COUNT, USER_COUNT);
        List<Integer> userIds = UserFriendUtils.collectUserIds(userFriends);

        UserFiendService userFiendService = new UserFiendService(userFriends);

        Observable.fromIterable(userIds)
                .flatMap(userFiendService::getFriends)
                .subscribe(uf -> log.info("User {} subscribed on user {}", uf.userId(), uf.friendId()));
    }
}
