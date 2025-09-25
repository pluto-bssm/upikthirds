package pluto.upik.domain.notification.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.notification.data.DTO.NotificationQuery;

@Controller
@RequiredArgsConstructor
public class NotificationRootQueryResolver {

    @SchemaMapping(typeName = "Query", field = "notification")
    public NotificationQuery getNotificationQuery() {
        return new NotificationQuery();
    }
}