package com.databinder.config.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.databinder.config.entities.DbVersion;
import com.databinder.config.repositories.DbVersionRepository;
import com.databinder.core.repositories.CardRepository;
import com.databinder.core.repositories.PrintingRepository;
import com.databinder.core.repositories.SetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DBConfigService {

    private final DbVersionRepository dbVersionRepository;
    private final CardRepository cardRepository;
    private final SetRepository cardSetRepository;
    private final PrintingRepository printingRepository;

    public List<DbVersion> getAllVersions() {
        return dbVersionRepository.findAll();
    }

    public void resetDatabase() {
        printingRepository.deleteAll();
        cardRepository.deleteAll();
        cardSetRepository.deleteAll();
        dbVersionRepository.deleteAll();
    }
	
}
