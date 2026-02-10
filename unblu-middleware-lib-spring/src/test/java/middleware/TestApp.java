package middleware;

import com.unblu.middleware.bot.annotation.UnbluConversationObservingBot;
import com.unblu.middleware.bot.annotation.UnbluDialogBot;
import com.unblu.middleware.webhooks.annotation.UnbluWebhooks;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
        UnbluWebhooks.class,
        UnbluDialogBot.class,
        UnbluConversationObservingBot.class
//        UnbluExternalMessenger.class
})
public class TestApp {
    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
    }
}
