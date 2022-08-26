package com.filling.good.domain.talk.entity;

import com.filling.good.domain.talk.enumerate.TalkType;
import com.filling.good.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TalkChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long talkChannelId;

    @Enumerated(EnumType.STRING)
    private TalkType type;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "owner")
    private User owner;

    private String channelName;
    private int maxTalker;
    private int curTalker;

}
