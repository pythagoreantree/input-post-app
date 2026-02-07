package ru.post.PostApp.domain.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import ru.post.PostApp.api.dto.request.PostItemRequest;
import ru.post.PostApp.api.dto.request.PostalPartyRequest;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Document(collection = "post_items")
public class PostItemDocument {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @Field("type")
    private String type;

    @Field("sender_info")
    private PartyInfo sender;

    @Field("receiver_info")
    private PartyInfo receiver;

    @Field("post_office_code")
    private String postOfficeCode;

    @Field("operator_id")
    private String operatorId;

    @Field("source_system")
    private String sourceSystem;

    @Field("accepted_at")
    private LocalDateTime acceptedAt;

    @Field("status")
    @Builder.Default
    private String status = "CREATED"; // CREATED, IN_TRANSIT, DELIVERED, RETURNED

    @Field("notes")
    private String notes;

    @Field("created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Field("updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Field("pending_events")
    @Builder.Default
    private List<OutboxEvent> pendingEvents = new ArrayList<>();

    @Getter
    @Builder
    public static class PartyInfo {
        @Field("person")
        private PersonInfo person;

        @Field("address")
        private AddressInfo address;
    }

    @Getter
    @Builder
    public static class PersonInfo {
        @Field("first_name")
        private String firstName;

        @Field("middle_name")
        private String middleName;

        @Field("last_name")
        private String lastName;

        @Field("address")
        private AddressInfo address;
    }

    @Getter
    @Builder
    public static class AddressInfo {
        @Field("postal_code")
        private String postalCode;

        @Field("city")
        String city;

        @Field("street")
        String street;

        @Field("house")
        String house;

        @Field("building")
        String building;

        @Field("apartment")
        String apartment;
    }

    @Getter
    @Setter
    @Builder
    public static class OutboxEvent implements Serializable {

        @Field("event_id")
        String id;

        @Field("event_type")
        String type;

        @Field("payload")
        Object payload;

        @Field("status")
        @Builder.Default
        String status = "PENDING"; // PENDING, SENT, FAILED

        @Field("retry_count")
        @Builder.Default
        Integer retryCount = 0;

        @Field("created_at")
        LocalDateTime createdAt;

        @Field("sent_at")
        LocalDateTime sentAt;

        @Field("error_message")
        String errorMessage;
    }

    public static PostItemDocument fromRequest(PostItemRequest request) {
        return PostItemDocument.builder()
                .type(request.getType())
                .sender(extractPartyInfo(request.getSender()))
                .receiver(extractPartyInfo(request.getReceiver()))
                .postOfficeCode(request.getPostOfficeCode())
                .operatorId(request.getOperatorId())
                .sourceSystem(request.getSourceSystem())
                .acceptedAt(request.getAcceptedAt())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private static PartyInfo extractPartyInfo(PostalPartyRequest party) {
        if (party == null) return null;
        return PartyInfo.builder()
                .person(PersonInfo.builder()
                        .firstName(party.getFirstName())
                        .middleName(party.getMiddleName())
                        .lastName(party.getLastName())
                        .build())
                .address(AddressInfo.builder()
                        .postalCode(party.getPostalCode())
                        .city(party.getCity())
                        .street(party.getStreet())
                        .house(party.getHouse())
                        .building(party.getBuilding())
                        .apartment(party.getApartment())
                        .build())
                .build();
    }

}