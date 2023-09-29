package ru.skypro.homework.mappers;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.service.entities.CommentEntity;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {

    public CommentDTO toCommentDto(CommentEntity commentEntity) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setAuthor(commentEntity.getId());
        commentDTO.setAuthorImage(commentEntity.getUser().getImage());
        commentDTO.setAuthorFirstName(commentEntity.getUser().getFirstName());
        commentDTO.setCreatedAt(commentEntity.getCreatedAt().toEpochSecond(java.time.ZoneOffset.UTC));
        commentDTO.setPk(commentEntity.getId());
        commentDTO.setText(commentEntity.getText());
        return commentDTO;
    }

    public List<CommentDTO> toCommentDTOList(List<CommentEntity> commentEntityList) {
        return commentEntityList.stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
    }



}

