package com.github.dimka9910.redistest.mapper;

import com.github.dimka9910.redistest.dto.DialogMetaDto;
import com.github.dimka9910.redistest.dto.MessageDto;
import com.github.dimka9910.redistest.service.redis.models.OrderHash;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public OrderHash dtoToHash(MessageDto messageDto, DialogMetaDto dialogMetaDto){

        OrderHash messageHash = new OrderHash();
//        CompositeId compositeId = new CompositeId(messageDto.getId(), dialogMetaDto.getD());
//        messageHash.setCompositeId(compositeId);
//        messageHash.setText(messageDto.getText());
//        messageHash.setFromId(messageDto.getFromId());
//        messageHash.setTime(messageDto.getDate());
        return messageHash;
    }


}
