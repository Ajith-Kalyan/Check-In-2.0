package gavs.first.com.home;

public class ListItem {

    public String title = "";
    public String description = "";
    public String id;



    public ListItem(String title, String description, String id) {
        this.title = title;
        this.description = description;
        this.id = id;

    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
