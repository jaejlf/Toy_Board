package com.filling.good.domain.talk.entity;

import com.filling.good.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Talker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long talkerId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "talker")
    private User talker;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "talkChannel")
    private TalkChannel talkChannel;

}
