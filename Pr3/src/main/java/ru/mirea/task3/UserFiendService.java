package ru.mirea.task3;

import io.reactivex.rxjava3.core.Observable;

import java.util.List;

public class UserFiendService {

    private List<UserFriend> userFriends;

    public UserFiendService(List<UserFriend> userFriends) {
        this.userFriends = userFriends;
    }

    public Observable<UserFriend> getFriends(int userId) {
        return Observable.fromIterable(userFriends)
                .filter(uf -> uf.userId() == userId);
    }
}
