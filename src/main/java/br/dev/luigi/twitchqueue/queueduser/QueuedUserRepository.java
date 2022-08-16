package br.dev.luigi.twitchqueue.queueduser;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueuedUserRepository extends JpaRepository<QueuedUser, Long> {

}
