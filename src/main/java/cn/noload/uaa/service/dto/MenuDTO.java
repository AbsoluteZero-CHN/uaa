package cn.noload.uaa.service.dto;

import cn.noload.uaa.domain.Menu;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the Menu entity.
 */
public class MenuDTO implements Serializable {

    private String id;

    private String name;

    private String url;

    private Boolean activated;

    private List<Menu> children;

    private Integer sort;

    private String  parentIds;

    private String icon;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean isActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }


    public List<Menu> getChildren() {
        return children;
    }

    public MenuDTO setChildren(List<Menu> children) {
        this.children = children;
        return this;
    }

    public Integer getSort() {
        return sort;
    }

    public MenuDTO setSort(Integer sort) {
        this.sort = sort;
        return this;
    }

    public String getParentIds() {
        return parentIds;
    }

    public MenuDTO setParentIds(String parentIds) {
        this.parentIds = parentIds;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public MenuDTO setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MenuDTO menuDTO = (MenuDTO) o;
        if (menuDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), menuDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "MenuDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", url='" + getUrl() + "'" +
            ", activated='" + isActivated() + "'" +
            "}";
    }
}
