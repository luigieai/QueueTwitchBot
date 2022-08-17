package br.dev.luigi.twitchqueue.twitch;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class TwitchConfiguration {

    @Value("${twitch.oauthuser}")
    private String chatUser;

    @Value("${twitch.channelName}")
    private String channelName;

    @Value("${twitch.reward.name}")
    private String rewardName;

}
