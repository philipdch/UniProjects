package com.project.mygym.values;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SubscriptionId implements Serializable {

    private static final long serialVersionUID = 1287916513919276037L;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "program_id")
    private Long programId;

    public SubscriptionId() {
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getProgramId() {
        return programId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SubscriptionId that = (SubscriptionId) o;
        return userId.equals(that.userId) && programId.equals(that.programId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, programId);
    }
}
