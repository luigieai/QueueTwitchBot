package br.dev.luigi.twitchqueue.twitch.events;

import br.dev.luigi.twitchqueue.TwitchqueueApplication;
import br.dev.luigi.twitchqueue.queueduser.QueuedService;
import br.dev.luigi.twitchqueue.twitch.TwitchBot;
import br.dev.luigi.twitchqueue.twitch.TwitchConfiguration;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.pubsub.domain.ChannelPointsReward;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

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
        TwitchqueueApplication.log.info(channelMessageEvent.getUser().getName()
                + " > " + channelMessageEvent.getMessage());
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
