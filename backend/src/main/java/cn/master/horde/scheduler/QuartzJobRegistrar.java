package cn.master.horde.scheduler;

import cn.master.horde.scheduler.annotation.QuartzJob;
import cn.master.horde.scheduler.job.DelegateQuartzJob;
import org.jetbrains.annotations.NotNull;
import org.quartz.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author : 11's papa
 * @since : 2026/2/12, 星期四
 **/
@Component
public class QuartzJobRegistrar implements ApplicationRunner {
    private final Scheduler scheduler;
    private final ApplicationContext applicationContext;

    public QuartzJobRegistrar(Scheduler scheduler, ApplicationContext applicationContext) {
        this.scheduler = scheduler;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(@NotNull ApplicationArguments args) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        // 扫描所有被 @Component 注解的 Bean
        scanner.addIncludeFilter(new AnnotationTypeFilter(Component.class));
        // 任务类在 "cn.master.horde.scheduler.job" 包下
        Set<BeanDefinition> candidates = scanner.findCandidateComponents("cn.master.horde.scheduler.job");
        for (BeanDefinition candidate : candidates) {

            try {
                Class<?> clazz = Class.forName(candidate.getBeanClassName());
                // Object bean = applicationContext.getBean(clazz);
                String beanName = applicationContext.getBeanNamesForType(clazz)[0];
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(QuartzJob.class)) {
                        QuartzJob quartzJob = method.getAnnotation(QuartzJob.class);
                        String jobName = beanName + "_" + method.getName();
                        String groupName = quartzJob.group();
                        // 检查是否已存在
                        JobKey jobKey = JobKey.jobKey(jobName, groupName);
                        if (scheduler.checkExists(jobKey)) {
                            System.out.println("任务已存在，跳过注册: " + jobName + " / " + groupName);
                            continue;
                        }

                        JobDetail jobDetail = JobBuilder.newJob(DelegateQuartzJob.class)
                                .withIdentity(jobName, groupName)
                                .usingJobData("beanName", beanName)
                                .usingJobData("methodName", method.getName())
                                .usingJobData("paramsJson", quartzJob.paramsJson())
                                .build();
                        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(quartzJob.cron());
                        Trigger trigger = TriggerBuilder.newTrigger()
                                .withIdentity(jobName + "_trigger", groupName)
                                .withSchedule(scheduleBuilder)
                                .build();
                        scheduler.scheduleJob(jobDetail, trigger);
                        // ✅ 关键：注册后立即暂停
                        if (!quartzJob.autoStart()) {
                            scheduler.pauseJob(jobKey);
                        }
                        System.out.println("✅ 已注册 Quartz 任务: " + jobName + " cron=" + quartzJob.cron());
                    }
                }
            } catch (ClassNotFoundException | BeansException | SchedulerException e) {
                System.err.println("❌ 注册 Quartz 任务失败: " + candidate.getBeanClassName());
                throw new RuntimeException(e);
            }
        }
    }
}
