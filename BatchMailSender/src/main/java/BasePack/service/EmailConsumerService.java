package BasePack.service;

import BasePack.Model.EmailObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.dsl.DirectChannelSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class EmailConsumerService {

    @Autowired
    EmailProcessingService emailProcessingService;

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    DirectChannelSpec requestsChannel(){
        return MessageChannels.direct();
    }

    @Bean
    IntegrationFlow requestsFlow(ConnectionFactory connectionFactory, MessageChannel requestsChannel) {
        var smc = new SimpleMessageConverter();
        smc.addAllowedListPatterns("*");
        return IntegrationFlow
            .from(Amqp.inboundAdapter(connectionFactory,"emailQueue").messageConverter(smc))
            .channel(requestsChannel)
            .handle(message -> {
                EmailObject emailObject = (EmailObject) message.getPayload();
                emailProcessingService.processEmail(emailObject);
            })
            .get();
    }

}

