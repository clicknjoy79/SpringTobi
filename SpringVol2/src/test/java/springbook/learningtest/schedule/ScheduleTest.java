package springbook.learningtest.schedule;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import springbook.learningtest.web.AbstractDispatcherServletTest;

import javax.servlet.ServletException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ScheduleTest extends AbstractDispatcherServletTest {
    @Test
    @Ignore   // 풀고 개별적으로 실행할 것
    public void XmlScheduleTest() throws ServletException, IOException, InterruptedException {
        setLocations("/schedule/schedule.xml").buildDispatcherServlet();
        Thread.sleep(1000000);
    }

    @Test
    @Ignore
    public void XmlScheduleTest2() throws ServletException, IOException, InterruptedException {
        setClasses(SystemAdminService.class, ResourceService.class);
        setLocations("/schedule/schedule2.xml").buildDispatcherServlet();
        Thread.sleep(1000000);
    }

    static class AlarmTask {
        @Scheduled(cron = "*/3 * * * * *")
        public void scheduledRun() {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println("스케줄러 실행: " + dateFormat.format(calendar.getTime()));
        }
    }

    @Component("systemAdminService")
    static class SystemAdminService {
        public void checkSystem() {
            System.out.println("checkSystem called each 5 seconds .......");
        }
    }
    @Component("resourceService")
    static class ResourceService {
        public void clearAll() {
            System.out.println("clearAll called each 3 seconds .......");
        }
    }

    @Test
    @Ignore
    public void annotationScheduleTest() throws ServletException, IOException, InterruptedException {
        setClasses(AppConfig.class, TaskService.class, TaskService2.class);
        buildDispatcherServlet();
        Thread.sleep(100000);
    }

    @Configuration
    @EnableScheduling
    static class AppConfig {}

    static class TaskService {
        @Scheduled(cron = "*/5 * * * * *")
        public void task() {
            System.out.println("TaskService called each 5 seconds");
        }
    }

    static class TaskService2 {
        @Scheduled(fixedRate = 3000)
        public void task() {
            System.out.println("TaskService2 called each 3 seconds");
        }
    }

}









































