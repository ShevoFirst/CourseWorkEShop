package ru.skypro.homework.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.AdsDTO;
import ru.skypro.homework.dto.CreateOrUpdateAdDTO;
import ru.skypro.homework.dto.ExtendedAdDTO;
import ru.skypro.homework.mappers.AdMapper;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.entities.AdEntity;
import ru.skypro.homework.service.entities.ImageEntity;
import ru.skypro.homework.service.repositories.AdRepository;
import ru.skypro.homework.service.repositories.CommentRepository;
import ru.skypro.homework.service.repositories.UserRepository;

import javax.xml.crypto.OctetStreamData;
import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Service
public class AdServiceImpl implements AdService {
    private final AdMapper adMapper;
    private final AdRepository adRepository;
    private final CommentRepository commentRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;


    // Метод для получения всех объявлений
    @Override
    public AdsDTO getAllAds() {
        // Получаем список всех объявлений из репозитория
        List<AdEntity> adEntity = adRepository.findAll();
        // Преобразуем список объявлений в объект AdsDTO с помощью маппера и возвращаем его
        return adMapper.toAdsDto(adEntity);
    }

    // Метод для добавления нового объявления
    @Override
    public AdDTO addAd(CreateOrUpdateAdDTO ad, MultipartFile image) {
        // Создаем новую сущность объявления
        AdEntity adEntity = new AdEntity();
        // Заполняем поля объявления из объекта CreateOrUpdateAdDTO
        adEntity.setDescription(ad.getDescription());
        adEntity.setTitle(ad.getTitle());
        adEntity.setPrice(ad.getPrice());
        // Загружаем изображение и сохраняем его в поле image сущности объявления
        adEntity.setImage(imageService.downloadImage(image));
        // Получаем пользователя, который добавил объявление, и сохраняем его в поле user сущности объявления
        adEntity.setUser(userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()));
        // Сохраняем сущность объявления в репозитории и возвращаем ее в виде объекта AdDTO с помощью маппера
        adRepository.save(adEntity);
        return adMapper.toAdDto(adEntity);
    }

    // Метод для получения расширенной информации об объявлении по его id
    @Override
    public ExtendedAdDTO getInfoByAd(int id) {
        // Получаем сущность объявления по его id из репозитория и преобразуем ее в объект ExtendedAdDTO с помощью маппера
        return adMapper.toExtendedAdDTO(adRepository.findById(id).orElseThrow());
    }

    // Метод для удаления объявления по его id
    @Override
    public void deleteAd(int id) {
        // Удаляем все комментарии, связанные с объявлением, из репозитория
        commentRepository.deleteAllByAdEntity(adRepository.findById(id).orElseThrow());
        // Удаляем объявление из репозитория
        adRepository.deleteById(id);
    }

    // Метод для обновления информации об объявлении по его id
    @Override
    public AdDTO updateInfoByAd(int id, CreateOrUpdateAdDTO ad) {
        // Получаем ссылку на сущность объявления по его id
        AdEntity adEntity= adRepository.getReferenceById(id);
        // Обновляем поля объявления из объекта CreateOrUpdateAdDTO и сохраняем изменения в репозитории
        adEntity.setDescription(ad.getDescription());
        adEntity.setPrice(ad.getPrice());
        adEntity.setTitle(adEntity.getTitle());
        adRepository.save(adEntity);
        // Преобразуем обновленную сущность объявления в объект AdDTO с помощью маппера и возвращаем его
        return adMapper.toAdDto(adEntity);
    }

    // Метод для получения всех объявлений, добавленных пользователем с указанным email
    @Override
    public AdsDTO getAdsByAuthUser(String email) {
        // Получаем список всех объявлений, добавленных пользователем с указанным email, из репозитория
        return adMapper.toAdsDto(adRepository.findByEmail(email));
    }

    // Метод для обновления изображения объявления по его id
    @Override
    public OctetStreamData updateImage(int id, MultipartFile image) throws IOException {
        // Получаем сущность объявления по его id из репозитория
        AdEntity ad = adRepository.findById(id).orElseThrow();
        // Загружаем новое изображение и сохраняем его в поле image сущности объявления
        ImageEntity imageEntity = imageService.downloadImage(image);
        ad.setImage(imageEntity);
        // Сохраняем изменения в репозитории и возвращаем новое изображение в виде OctetStreamData
        adRepository.saveAndFlush(ad);
        return new OctetStreamData(image.getInputStream());
    }
}
