package ua.gov.dp.econtact.model.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aleksandr
 * 29.09.2015.
 */
public class TicketLikeDTO {

    @SerializedName("likes_counter")
    private int likesCount;

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(final int likesCount) {
        this.likesCount = likesCount;
    }

}
