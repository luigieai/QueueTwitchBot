package br.dev.luigi.twitchqueue.twitch;

import br.dev.luigi.twitchqueue.queueduser.QueuedService;
import br.dev.luigi.twitchqueue.twitch.events.QueueEventHandler;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.reactor.ReactorEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
public class TwitchBot {

    @Autowired
    @Getter
    private TwitchConfiguration configuration;

    @Autowired
    private QueuedService queuedService;

    @Bean
    @Scope("singleton")
    public TwitchClient getBot(){
        //Builder
        OAuth2Credential chatbotUserCredential = new OAuth2Credential("twitch", configuration.getChatUser());
        TwitchClient twitchClient = TwitchClientBuilder.builder()
                //.withEnableHelix(true)
                .withEnableChat(true)
                .withChatAccount(chatbotUserCredential)
                .withEnablePubSub(true)
                .withDefaultEventHandler(ReactorEventHandler.class)
                .build();
        //Join Channel
        twitchClient.getChat().joinChannel(configuration.getChannelName());
        //Events
        String channelID = twitchClient.getChat().getChannelNameToChannelId().get(configuration.getChannelName());
        twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(chatbotUserCredential, channelID);
        QueueEventHandler queueEventHandler = new QueueEventHandler(queuedService,configuration,twitchClient);
        queueEventHandler.registerEvents();
        //End
        twitchClient.getChat().sendMessage(configuration.getChannelName(), "Bot activated!");
        return twitchClient;
    }

    public void sendMessage(String message){
        getBot().getChat().sendMessage(configuration.getChannelName(), message);
    }

}
