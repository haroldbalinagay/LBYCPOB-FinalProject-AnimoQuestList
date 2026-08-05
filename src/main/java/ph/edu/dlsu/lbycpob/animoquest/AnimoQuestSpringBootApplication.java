package ph.edu.dlsu.lbycpob.animoquest;

import javafx.application.Application;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.spring.SpringFxWeaver;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

// To temporarily disable Spring Data JPA, since no Supabase yet
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class AnimoQuestSpringBootApplication {

    // Source - https://stackoverflow.com/a/78467021
    // Posted by Malavan
    // Retrieved 2026-08-05, License - CC BY-SA 4.0
    @Bean
    public FxWeaver fxWeaver(ConfigurableApplicationContext applicationContext) {
        // Would also work with javafx-weaver-core only:
        // return new FxWeaver(applicationContext::getBean, applicationContext::close);
        return new SpringFxWeaver(applicationContext);
    }

    public static void main(String[] args) {
        //SpringApplication.run(AnimoQuestSpringBootApplication.class, args);
        Application.launch(JavaFxApplication.class, args);
    }
}
