package br.dev.luigi.twitchqueue.queueduser;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
        return queuedUserRepo.findAll(Sort.by(Sort.Direction.ASC, "joined_datetime"));
    }

    public List<QueuedUser> getNextQueuedUsersInQueue() {
        Page<QueuedUser> nextInQueue = queuedUserRepo.findAll(PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC,"joined_datetime") ));
        return nextInQueue.stream().toList();
    }

    public QueuedUser addQueuedUser(String username) {
        QueuedUser queuedUser = new QueuedUser(username);
        return queuedUserRepo.saveAndFlush(queuedUser);
    }

    public void removeNextQueuedUsersInQueue(QueuedUser queuedUser) {
        queuedUserRepo.deleteAll(getNextQueuedUsersInQueue());
    }

}