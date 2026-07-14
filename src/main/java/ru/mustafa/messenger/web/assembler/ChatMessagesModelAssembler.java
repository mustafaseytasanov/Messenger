package ru.mustafa.messenger.web.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import ru.mustafa.messenger.controller.MessageController;
import ru.mustafa.messenger.dto.ChatMessagesDTO;

@Component
public class ChatMessagesModelAssembler extends RepresentationModelAssemblerSupport<ChatMessagesDTO, EntityModel<ChatMessagesDTO>> {

    @SuppressWarnings("unchecked")
    public ChatMessagesModelAssembler() {
        super(MessageController.class, (Class<EntityModel<ChatMessagesDTO>>) (Class<?>) EntityModel.class);
    }

    @Override
    public EntityModel<ChatMessagesDTO> toModel(ChatMessagesDTO dto) {
        return EntityModel.of(dto);
    }
}