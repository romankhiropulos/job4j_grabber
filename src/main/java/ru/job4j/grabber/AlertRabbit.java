package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.lang.Integer.parseInt;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit {

    private Properties config;

    public AlertRabbit() {
        init();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                config.getProperty("rabbit.url"),
                config.getProperty("rabbit.username"),
                config.getProperty("rabbit.password"));
    }

    public Properties getConfig() {
        return config;
    }

    private void init() {
        try (InputStream in = AlertRabbit.class
                .getClassLoader().getResourceAsStream("rabbit.properties")) {
            config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) {
        AlertRabbit rabbit = new AlertRabbit();
        rabbit.runAlertRabbit();
    }

    private void runAlertRabbit() {
        try {
            AlertRabbitStore alertRabbitStore = new AlertRabbitStore();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();

            /**
             * Каждый запуск работы вызывает конструтор. Чтобы в объект Job иметь общий
             * ресурс нужно использовать JobExecutionContext. При создании Job мы указываем
             * параметры data. В них мы передаем ссылку на store.
             */
            JobDataMap data = new JobDataMap();
            data.put("store", alertRabbitStore);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();

            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(
                            parseInt(this.getConfig().getProperty("rabbit.interval"))
                    )
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);

            /**
             * После выполнения работы в списке будут две даты.
             * Объект store является обшим для каждой работы.
             */
            Thread.sleep(10000);
            scheduler.shutdown();
            System.out.println(alertRabbitStore.getAllDates());
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    private class AlertRabbitStore {
        LocalDateTime add(LocalDateTime date) {
            try (Connection connection = getConnection(); PreparedStatement ps = connection
                    .prepareStatement("INSERT INTO rabbit (created_date) VALUES (?)")) {
                ps.setTimestamp(1, Timestamp.valueOf(date));
                ps.execute();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
            return date;
        }

        List<LocalDateTime> getAllDates() {
            List<LocalDateTime> items = new ArrayList<>();
            try (Connection connection = getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT * FROM rabbit")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    items.add(rs.getTimestamp("created_date").toLocalDateTime());
                }
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
            return items;
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");

            /**
             * Чтобы получить объекты из context используется следующий вызов.
             */
            AlertRabbitStore store = (AlertRabbitStore) context.getJobDetail()
                    .getJobDataMap().get("store");
            store.add(LocalDateTime.now());
        }
    }
}