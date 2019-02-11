package gavs.first.com.home;

public class MyListData {

    private String mainText;
    private String subText;

    public MyListData (String maintext,String subtext)
    {
        mainText = maintext;
        subText = subtext;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public String getSubText() {
        return subText;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }

}
