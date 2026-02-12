package cn.master.horde.model.dto;

/**
 * @author : 11's papa
 * @since : 2026/2/12, 星期四
 **/
public class JobInfo {
    public String jobName;
    public String group;
    public String cron;
    public String state; // NORMAL, PAUSED, COMPLETE, ERROR...

    public JobInfo(String jobName, String group, String cron, String state) {
        this.jobName = jobName;
        this.group = group;
        this.cron = cron;
        this.state = state;
    }
}
