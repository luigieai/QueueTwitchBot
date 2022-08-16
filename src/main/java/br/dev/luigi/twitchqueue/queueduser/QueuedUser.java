package br.dev.luigi.twitchqueue.queueduser;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Calendar;

@Entity(name="queued_user")
public class QueuedUser {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long id;

    @Column(length = 30, nullable = false, unique = true)
    @Getter
    private String username;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "joined_datetime")
    @Getter
    private Calendar joinedDateTime;

    public QueuedUser(String username){
        this.username = username;
    };
}