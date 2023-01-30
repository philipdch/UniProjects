package com.project.mygym.domain;

import com.project.mygym.utils.SystemDateTime;
import com.project.mygym.values.SubscriptionId;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @EmbeddedId
    private SubscriptionId subscriptionId = new SubscriptionId(); //must be initialised. Else foreign key ids can't be set via reflection

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private SimpleUser simpleUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("programId")
    private Program program;

    @Column(name = "subscription_date", nullable = false)
    private LocalDateTime createdOn;

    public Subscription() {

    }

    public Subscription(SimpleUser simpleUser, Program program) {
        this.simpleUser = simpleUser;
        this.program = program;
        createdOn = SystemDateTime.now();
    }

    public SimpleUser getSimpleUser() {
        return simpleUser;
    }

    public Program getProgram() {
        return program;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Subscription that = (Subscription) o;
        return simpleUser.equals(that.simpleUser) && program.equals(that.program);
    }

    @Override
    public int hashCode() {
        return Objects.hash(simpleUser, program);
    }
}
