package ru.skypro.homework.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.mappers.CommentMapper;
import ru.skypro.homework.service.CommentService;
import ru.skypro.homework.service.entities.AdEntity;
import ru.skypro.homework.service.entities.CommentEntity;
import ru.skypro.homework.service.entities.UserEntity;
import ru.skypro.homework.service.repositories.AdRepository;
import ru.skypro.homework.service.repositories.CommentRepository;
import ru.skypro.homework.service.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final UserRepository userRepository;


    // Метод receivingAdComments получает список комментариев для объявления
    @Override
    public CommentsDTO receivingAdComments(int adId) {
        AdEntity adEntity = checkForAd(adId); // получаем объявление по его id
        List<CommentEntity> commentEntityList = adEntity.getCommentEntityList(); // получаем список всех комментариев для данного объявления
        List<CommentDTO> commentDTOList = commentMapper.toCommentDTOList(commentEntityList); // маппим список комментариев в список DTO
        return new CommentsDTO(commentEntityList.size(), commentDTOList); // возвращаем объект CommentsDTO с количеством комментариев и списком DTO
    }

    // Метод deleteComment удаляет комментарий к объявлению
    @Override
    public void deleteComment(int adId, int commentId) {
        CommentEntity commentEntity = checkForAdAndComment(adId, commentId); // проверяем, существует ли комментарий для данного объявления
        commentRepository.delete(commentEntity); // удаляем комментарий из базы данных
    }

    // Метод addComment добавляет новый комментарий к объявлению
    @Override
    public CommentDTO addComment(int adId, CreateOrUpdateCommentDTO text) {
        AdEntity adEntity = checkForAd(adId); // получаем объявление по его id
        UserEntity userEntity = getCurrentUser(); // получаем текущего авторизованного пользователя
        CommentEntity newCommentEntity = commentMapper.createCommentEntity(text, adEntity, userEntity); // создаем новый объект CommentEntity из переданных данных и связываем его с объявлением и пользователем
        commentRepository.saveAndFlush(newCommentEntity); // сохраняем новый комментарий в базу данных
        return commentMapper.toCommentDto(newCommentEntity); // возвращаем DTO нового комментария
    }

    // Метод updateComment обновляет существующий комментарий к объявлению
    @Override
    public CommentDTO updateComment(int adId, int commentId, CreateOrUpdateCommentDTO text) {
        CommentEntity commentEntityToUpdate = checkForAdAndComment(adId, commentId); // проверяем, существует ли комментарий для данного объявления
        commentEntityToUpdate.setText(text.getText()); // обновляем текст комментария
        commentRepository.saveAndFlush(commentEntityToUpdate); // сохраняем изменения в базе данных
        return commentMapper.toCommentDto(commentEntityToUpdate); // возвращаем DTO обновленного комментария
    }

    // Приватный метод checkForAdAndComment проверяет, существует ли комментарий для данного объявления
    private CommentEntity checkForAdAndComment(int adId, int commentId) {
        checkForAd(adId); // проверяем, существует ли объявление с данным id
        Optional<CommentEntity> commentEntity = commentRepository.findCommentByCommentIdAndAdId(adId, commentId); // ищем комментарий по его id и id объявления

        return commentEntity.orElseThrow(() -> new NotFoundException(String.format(
                "Комментарий с индексом \"%s\" не найден для объявления с индексом \"%s\".",
                commentId, adId))
        ); // если комментарий не найден, выбрасываем исключение NotFoundException с сообщением об ошибке
    }

    // Приватный метод checkForAd проверяет, существует ли объявление с данным id
    private AdEntity checkForAd(int adId) {
        return adRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Объявление с индексом \"%s\" не найдено.", adId)
                )); // если объявление не найдено, выбрасываем исключение NotFoundException с сообщением об ошибке
    }

    // Приватный метод getCurrentUser получает текущего авторизованного пользователя
    private UserEntity getCurrentUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName(); // получаем имя пользователя из контекста безопасности
        return userRepository.findByEmail(userName); // находим пользователя по его email
    }
}