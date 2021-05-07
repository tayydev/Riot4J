package io.github.nathannorth.riotWrapper.objects;

public class ValActId {
    private final String value;
    private ValActId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static final ValActId EPISODE_ONE_ACT_ONE = new ValActId("3f61c772-4560-cd3f-5d3f-a7ab5abda6b3");
    public static final ValActId EPISODE_ONE_ACT_TWO = new ValActId("0530b9c4-4980-f2ee-df5d-09864cd00542");
    public static final ValActId EPISODE_ONE_ACT_THREE = new ValActId("46ea6166-4573-1128-9cea-60a15640059b");
    public static final ValActId EPISODE_TWO_ACT_ONE = new ValActId("97b6e739-44cc-ffa7-49ad-398ba502ceb0");
    public static final ValActId EPISODE_TWO_ACT_TWO = new ValActId("ab57ef51-4e59-da91-cc8d-51a5a2b9b8ff");
    public static final ValActId EPISODE_TWO_ACT_THREE = new ValActId("52e9749a-429b-7060-99fe-4595426a0cf7");

    //todo comment/ annotate unsafe
    public static ValActId createUnvalidated(String id) {
        return new ValActId(id);
    }
}
