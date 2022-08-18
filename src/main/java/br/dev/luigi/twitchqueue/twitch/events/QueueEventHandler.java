package br.dev.luigi.twitchqueue.twitch.events;

import br.dev.luigi.twitchqueue.TwitchqueueApplication;
import br.dev.luigi.twitchqueue.queueduser.QueuedService;
import br.dev.luigi.twitchqueue.queueduser.QueuedUser;
import br.dev.luigi.twitchqueue.twitch.TwitchBot;
import br.dev.luigi.twitchqueue.twitch.TwitchConfiguration;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.enums.CommandPermission;
import com.github.twitch4j.pubsub.domain.ChannelPointsReward;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@RequiredArgsConstructor
public class QueueEventHandler {

    @NonNull
    private QueuedService queuedService;

    @NonNull
    private TwitchConfiguration twitchConfiguration;

    @NonNull
    private TwitchClient twitchClient;

    public void registerEvents(){
        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> handleMessageEvent(event));
        twitchClient.getEventManager().onEvent(RewardRedeemedEvent.class, event -> handleChannelRedeem(event));
    }

    private void handleMessageEvent(ChannelMessageEvent channelMessageEvent) {
        String message = channelMessageEvent.getMessage();
        if(!message.startsWith("!")) return;

        if(message.equalsIgnoreCase("!queue")){
            List<QueuedUser> users = queuedService.getAllQueuedUsers();
            if(users.isEmpty()){
                twitchClient.getChat().sendMessage(twitchConfiguration.getChannelName(),
                        "The queue is empty");
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Queue: ");
            users.forEach(user -> sb.append( user.getUsername() + " " ));
            twitchClient.getChat().sendMessage(twitchConfiguration.getChannelName(), sb.toString());
        }

        if(message.startsWith("!queue add")) {
            String user = channelMessageEvent.getUser().getName();
            Set<CommandPermission> userPermissions = channelMessageEvent.getPermissions();
            boolean havePermission = userPermissions.stream()
                    .anyMatch(permission -> permission.name().equals("MODERATOR"));
            if(!havePermission){
                TwitchqueueApplication.log.info(user + " don't have permission to manually add user at queue");
                return;
            }
            String userToAdd = message.split(" ", 3)[2];
            if(queuedService.isUserQueued(userToAdd)){
                TwitchqueueApplication.log.info("Moderator " + user + " tried to add " + userToAdd +
                        " to queue but the user is already in queue");
                twitchClient.getChat().sendMessage(twitchConfiguration.getChannelName(),
                        "@"+ user + " the user is already in queue!");
                return;
            }
            TwitchqueueApplication.log.info("Moderator " + user + " Issued manual addition of the user " + userToAdd +
                    " in queue");
            queuedService.addQueuedUser(userToAdd);
            twitchClient.getChat().sendMessage(twitchConfiguration.getChannelName(),
                    "@" + userToAdd + " had been added to the queue to play by " + user);
        }

    }

    private void handleChannelRedeem(RewardRedeemedEvent rewardRedeemedEvent) {
        ChannelPointsReward reward = rewardRedeemedEvent.getRedemption().getReward();
        String user = rewardRedeemedEvent.getRedemption().getUser().getDisplayName();
        String title = reward.getTitle();
        if(title.equals(twitchConfiguration.getRewardName())){
            if(queuedService.isUserQueued(user)){
                TwitchqueueApplication.log.info(user + " redeemed the queue reward, but he is already in queue");
                twitchClient.getChat().sendMessage(twitchConfiguration.getChannelName(),
                        "@" + user + " you already in the queue to play! Please seek a mod for refund");
                return;
            }
            TwitchqueueApplication.log.info(user + " redeemed the queue reward");
            queuedService.addQueuedUser(user);
            twitchClient.getChat().sendMessage(twitchConfiguration.getChannelName(),
                    "@" + user + " had been added to the queue to play!");
        }
    }
}
