package ua.gov.dp.econtact.model;

public class ShareModel {

    private int shareTypeCode;
    private String actionType;
    private int objectTitleId;
    private String objectName;
    private String url;
    private String description;
    private boolean needCropImage = false;

    public int getShareTypeCode() {
        return shareTypeCode;
    }

    public void setShareTypeCode(final int shareTypeCode) {
        this.shareTypeCode = shareTypeCode;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(final String actionType) {
        this.actionType = actionType;
    }

    public int getObjectTitleId() {
        return objectTitleId;
    }

    public void setObjectTitleId(final int objectTitleId) {
        this.objectTitleId = objectTitleId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(final String objectName) {
        this.objectName = objectName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isNeedCropImage() {
        return needCropImage;
    }

    public void setNeedCropImage(final boolean needCropImage) {
        this.needCropImage = needCropImage;
    }
}
