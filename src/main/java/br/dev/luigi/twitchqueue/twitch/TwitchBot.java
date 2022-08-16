package br.dev.luigi.twitchqueue.twitch;

import br.dev.luigi.twitchqueue.TwitchqueueApplication;
import br.dev.luigi.twitchqueue.twitch.events.QueueEventHandler;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.reactor.ReactorEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwitchBot {

    @Value("${twitch.oauthuser}")
    private String chatUser;

    @Value("${twitch.channelName}")
    private String channelName;

    @Bean
    public TwitchClient getTwitchBot(){
        //Builder
        OAuth2Credential chatbotUserCredential = new OAuth2Credential("twitch", chatUser);
        TwitchClient twitchClient = TwitchClientBuilder.builder()
                //.withEnableHelix(true)
                .withEnableChat(true)
                .withChatAccount(chatbotUserCredential)
                .withEnablePubSub(true)
                .withDefaultEventHandler(ReactorEventHandler.class)
                .build();
        //Join Channel
        twitchClient.getChat().joinChannel(channelName);
        //Events
        String channelID = twitchClient.getChat().getChannelNameToChannelId().get(channelName);
        twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(chatbotUserCredential, channelID);
        QueueEventHandler eventHandler = new QueueEventHandler();
        eventHandler.registerEvents(twitchClient.getEventManager());
        //End
        twitchClient.getChat().sendMessage(channelName, "Bot activated!");
        return twitchClient;
    }


}
