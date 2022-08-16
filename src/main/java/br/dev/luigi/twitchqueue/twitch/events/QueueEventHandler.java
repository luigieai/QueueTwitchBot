package br.dev.luigi.twitchqueue.twitch.events;

import br.dev.luigi.twitchqueue.TwitchqueueApplication;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.pubsub.domain.ChannelPointsReward;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;


public class QueueEventHandler {

    public void registerEvents(EventManager eventManager){
        eventManager.onEvent(ChannelMessageEvent.class, event -> handleMessageEvent(event));
        eventManager.onEvent(RewardRedeemedEvent.class, event -> handleChannelRedeem(event));
    }

    private void handleMessageEvent(ChannelMessageEvent channelMessageEvent) {
        TwitchqueueApplication.log.info(channelMessageEvent.getUser().getName()
                + " > " + channelMessageEvent.getMessage());
    }

    private void handleChannelRedeem(RewardRedeemedEvent rewardRedeemedEvent) {
        ChannelPointsReward reward = rewardRedeemedEvent.getRedemption().getReward();
        String user = rewardRedeemedEvent.getRedemption().getUser().getDisplayName();
        String title = reward.getTitle();
        long cost = reward.getCost();
        TwitchqueueApplication.log.info(user + " redeemed " + title + " for " + cost + " points!");
    }
}
