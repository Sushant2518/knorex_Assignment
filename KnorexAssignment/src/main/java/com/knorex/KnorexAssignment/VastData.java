package com.knorex.KnorexAssignment;

public class VastData {
	private String version;
    private String id;
    private String title;
    private String description;
    private String impressionId;
    private String impressionUrl;

    public VastData(String version, String id, String title, String description, String impressionId, String impressionUrl) {
        this.version = version;
        this.id = id;
        this.title = title;
        this.description = description;
        this.impressionId = impressionId;
        this.impressionUrl = impressionUrl;
    }

    public String getVersion() { return version; }
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImpressionId() { return impressionId; }
    public String getImpressionUrl() { return impressionUrl; }

    @Override
    public String toString() {
        return "VastData{" +
                "version='" + version + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", impressionId='" + impressionId + '\'' +
                ", impressionUrl='" + impressionUrl + '\'' +
                '}';
    }
}
