package pluto.upik.domain.notification.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.notification.data.DTO.NotificationMutation;

@Controller
@RequiredArgsConstructor
public class NotificationRootMutationResolver {

    @SchemaMapping(typeName = "Mutation", field = "notification")
    public NotificationMutation getNotificationMutation() {
        return new NotificationMutation();
    }
}