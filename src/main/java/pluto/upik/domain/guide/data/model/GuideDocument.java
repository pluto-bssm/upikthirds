package pluto.upik.domain.guide.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "guides")
@Setting(settingPath = "elasticsearch/es-settings.json")
public class GuideDocument {
    
    @Id
    private UUID id;
    
    @Field(type = FieldType.Text, analyzer = "korean")
    private String title;
    
    @Field(type = FieldType.Text, analyzer = "korean")
    private String content;
    
    @Field(type = FieldType.Keyword)
    private String category;
    
    @Field(type = FieldType.Keyword)
    private String guideType;
    
    @Field(type = FieldType.Long)
    private Long like;

    @Field(type = FieldType.Long)
    private Long revoteCount;

    @Field(type = FieldType.Date)
    private LocalDate createdAt;

    @Field(type = FieldType.Keyword)
    private UUID userId;

    @Field(type = FieldType.Keyword)
    private String userName;
    
    @Field(type = FieldType.Keyword)
    private String userEmail;
}