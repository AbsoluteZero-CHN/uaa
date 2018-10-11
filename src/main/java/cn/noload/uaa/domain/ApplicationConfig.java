package cn.noload.uaa.domain;

public class ApplicationConfig {

    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public ApplicationConfig setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ApplicationConfig setDescription(String description) {
        this.description = description;
        return this;
    }
}
