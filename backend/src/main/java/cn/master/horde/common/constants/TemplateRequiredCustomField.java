package cn.master.horde.common.constants;

/**
 * @author : 11's papa
 * @since : 2026/2/6, 星期五
 **/
public enum TemplateRequiredCustomField {
    BUG_DEGREE("functional_priority");

    private final String name;

    TemplateRequiredCustomField(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
