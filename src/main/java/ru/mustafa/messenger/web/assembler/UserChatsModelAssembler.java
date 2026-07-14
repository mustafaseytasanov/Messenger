package ru.mustafa.messenger.web.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import ru.mustafa.messenger.controller.ChatController;
import ru.mustafa.messenger.controller.MessageController;
import ru.mustafa.messenger.dto.UserChatsDTO;

@Component
public class UserChatsModelAssembler extends RepresentationModelAssemblerSupport<UserChatsDTO, EntityModel<UserChatsDTO>> {

    @SuppressWarnings("unchecked")
    public UserChatsModelAssembler() {
        super(ChatController.class, (Class<EntityModel<UserChatsDTO>>) (Class<?>) EntityModel.class);
    }

    @Override
    public EntityModel<UserChatsDTO> toModel(UserChatsDTO dto) {

        EntityModel<UserChatsDTO> entityModel = EntityModel.of(dto);

        entityModel.add(WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(ChatController.class)
                                .getUserChats()).withSelfRel());

        entityModel.add(WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(MessageController.class)
                                .getChatMessages(dto.id()))
                .withRel("messages"));

        return entityModel;
    }
}