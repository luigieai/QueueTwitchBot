package br.dev.luigi.twitchqueue.queueduser;

import br.dev.luigi.twitchqueue.TwitchqueueApplication;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QueuedService {
    //Make in controller a method for setting who is actually playing with the streamer
    @Autowired
    private QueuedUserRepository queuedUserRepo;

    @Getter
    @Setter
    private String currentlyPlaying;

    public long getQueueCount() {
        return queuedUserRepo.count();
    }

    public List<QueuedUser> getAllQueuedUsers() {
        return queuedUserRepo.findAll(Sort.by(Sort.Direction.ASC, "joinedDateTime"));
    }

    public List<QueuedUser> getNextQueuedUsersInQueue() {
        Page<QueuedUser> nextInQueue = queuedUserRepo.findAll(PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC,"joined_datetime") ));
        return nextInQueue.stream().toList();
    }

    public QueuedUser addQueuedUser(String username) {
        QueuedUser queuedUser = new QueuedUser(username.toLowerCase());
        QueuedUser resultUser = queuedUserRepo.saveAndFlush(queuedUser);
        TwitchqueueApplication.log.info(username + " has been added to the Queue");
        return resultUser;
    }

    public boolean isUserQueued(String username) {
        return queuedUserRepo.existsByUsername(username.toLowerCase());
    }

    public void removeNextQueuedUsersInQueue() {
        queuedUserRepo.deleteAll(getNextQueuedUsersInQueue());
    }

    @Transactional
    public void removeQueuedUser(String username) {
        queuedUserRepo.deleteByUsername(username.toLowerCase());
        TwitchqueueApplication.log.info(username + " has been removed from the Queue");
    }

}
